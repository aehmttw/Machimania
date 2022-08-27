package machimania;

import basewindow.*;
import machimania.region.Region;

import java.awt.*;

public class HumanCharacter
{
    public static PosedModelPose idleHandsAnimation;

    public static PosedModelPose standAnimation;
    public static PosedModelAnimation walkAnimation;
    public static PosedModelAnimation runAnimation;

    protected Drawing drawing;
    public PosedModel model;

    public double age = 0;
    public double animationTimer = 0;

    public double rotation;
    public int tileX;
    public int tileY;

    public double posX;
    public double posY;
    public double posZ;

    public double prevRotation;
    public int prevTileX;
    public int prevTileY;

    public double turnTime;

    public boolean walking = false;
    public boolean running = false;

    public double runPercentage = 0;
    public double baseRunSpeed = 0.04;
    public double baseWalkSpeed = 0.02;
    public double baseMoveSpeed = 0.02;
    public double smoothMoveSpeed;
    public double walkSpeed = 0;
    public double runAcceleration = 0.0016;
    public double walkAcceleration = 0.0016;
    public double acceleration = 0.0008;

    public double walkTurnDuration = 50;
    public double runTurnDuration = 25;
    public double turnDuration = 25;
    public double currentTurnDuration = 25;

    public double posZOffset = 0;

    public World world;

    public HumanCharacter(Drawing d, String model, World world)
    {
        this.world = world;
        this.drawing = d;
        Model m = d.createModel(model);
        this.model = Game.game.window.createPosedModel(m);
    }

    public void update()
    {
        Region region = this.world.getRegion(this.posX, this.posY);

        if (region != null)
            this.posZ = region.getHeightAt(this.posX, this.posY) + posZOffset;

        this.model.resetBones();

        if (this.turnTime <= 0)
        {
            this.rotation = this.getNormalRotation();
            this.turnTime = 0;
        }
        else
            this.turnTime -= Game.game.frameFrequency;

        double moveDirection = -1;

        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_UP))
            moveDirection = 1;

        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_DOWN))
            moveDirection = 3;

        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_RIGHT))
            moveDirection = 0;

        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_LEFT))
            moveDirection = 2;

        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_SPACE))
            posZOffset += Game.game.frameFrequency / 50.0;

        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_LEFT_SHIFT))
            posZOffset -= Game.game.frameFrequency / 50.0;

        if (Game.game.window.validPressedKeys.contains(InputCodes.KEY_RIGHT_SHIFT))
        {
            Game.game.window.validPressedKeys.remove((Integer) InputCodes.KEY_RIGHT_SHIFT);
            this.running = !this.running;
        }

        this.updateRunning();

        idleHandsAnimation.apply(this.model, 1);
        standAnimation.apply(this.model, 1);

        if (!this.walking)
        {
            double vX = Math.cos(moveDirection * Math.PI / 2);
            double vY = -Math.sin(moveDirection * Math.PI / 2);

            if (Math.abs(vX) < 0.0001)
                vX = 0;

            if (Math.abs(vY) < 0.0001)
                vY = 0;

            boolean isBlocked = Game.game.world.isSolid((int) Math.round(this.posX + Math.signum(vX)), (int) Math.round(this.posY + Math.signum(vY)));

            if (moveDirection >= 0)
            {
                if (!isBlocked)
                    this.move((int) moveDirection % 4);
                else
                {
                    if (this.turnTime <= 0)
                        this.setRotation((int) moveDirection);

                    this.posX = this.tileX;
                    this.posY = this.tileY;
                }
            }
            else
            {
                this.posX = this.tileX;
                this.posY = this.tileY;
            }

            //this.walkSpeed = Math.max(0, this.walkSpeed - this.deceleration * Game.game.frameFrequency);
        }

        if (this.walking)
        {
            double min = 0.004;

            double vX = Math.max(this.walkSpeed, min) * Math.cos(this.rotation * Math.PI / 2);
            double vY = -Math.max(this.walkSpeed, min) * Math.sin(this.rotation * Math.PI / 2);

            if (Math.abs(vX) < 0.0001)
                vX = 0;

            if (Math.abs(vY) < 0.0001)
                vY = 0;

            this.posX += vX * Game.game.frameFrequency;
            this.posY += vY * Game.game.frameFrequency;

            this.walkSpeed = Math.min(this.baseMoveSpeed, this.walkSpeed + this.acceleration * Game.game.frameFrequency);

            if (this.turnTime > this.turnDuration)
                this.walkSpeed = 0;

            boolean isBlocked = Game.game.world.isSolid((int) Math.round(this.posX + Math.signum(vX)), (int) Math.round(this.posY + Math.signum(vY)));
            if ((vX != 0 || vY != 0) && (moveDirection != this.getNormalRotation() || isBlocked))
            {
                this.walkSpeed = Math.max(0, Math.min(this.walkSpeed, this.baseMoveSpeed * Math.pow(Math.max(this.getWalkProgress(), 0), 0.25)));
            }

            if ((this.posX > this.tileX && vX > 0.0001) || (this.posX < this.tileX && vX < -0.0001) || (this.posY > this.tileY && vY > 0.0001) || (this.posY < this.tileY && vY < -0.0001))
            {
                if (moveDirection == this.getNormalRotation() || this.walkSpeed > 0.007)
                    this.move((int) this.getNormalRotation());
                else
                {
                    this.posX = this.tileX;
                    this.posY = this.tileY;
                    this.walking = false;
                }
            }
        }

        double t = 0.05;
        if (this.walkSpeed - this.smoothMoveSpeed > this.baseMoveSpeed * t * Game.game.frameFrequency)
            this.smoothMoveSpeed += this.baseMoveSpeed * t * Game.game.frameFrequency;
        else if (this.walkSpeed - this.smoothMoveSpeed < -this.baseMoveSpeed * t * Game.game.frameFrequency)
            this.smoothMoveSpeed -= this.baseMoveSpeed * t * Game.game.frameFrequency;
        else
            this.smoothMoveSpeed = this.walkSpeed;

        this.animationTimer += (1.4 + 0.6 * this.runPercentage) * Game.game.frameFrequency;
        walkAnimation.apply(this.model, this.animationTimer, (1 - this.runPercentage) * this.smoothMoveSpeed / this.baseMoveSpeed);
        runAnimation.apply(this.model, this.animationTimer, this.runPercentage * this.smoothMoveSpeed / this.baseMoveSpeed);

        this.age += Game.game.frameFrequency;
    }

    public double getWalkProgress()
    {
        if (this.getNormalRotation() == 2)
            return this.posX - this.tileX;
        else if (this.getNormalRotation() == 1)
            return this.posY - this.tileY;
        else if (this.getNormalRotation() == 0)
            return this.tileX - this.posX;
        else if (this.getNormalRotation() == 3)
            return this.tileY - this.posY;
        else
            return 0;
    }

    public double getNormalRotation()
    {
        return (this.rotation + 4) % 4;
    }

    public boolean setRotation(double r)
    {
        this.rotation = this.getNormalRotation();

        if (this.turnTime > 0)
            return false;

        if (Math.abs(r - this.rotation) == 2)
            r = this.rotation + 2;
        else if (r == 3 && this.rotation == 0)
            r = -1;
        else if (r == 0 && this.rotation == 3)
            r = 4;

        this.prevRotation = this.rotation;
        this.rotation = r;

        double diff = Math.abs(this.getNormalRotation() - this.prevRotation);
        if (diff == 1 || diff == 3)
            this.turnTime = this.turnDuration;
        else if (diff == 2)
            this.turnTime = this.turnDuration * 2;

        if (diff != 0)
           this.currentTurnDuration = this.turnTime;

        return true;
    }

    public void move(int r)
    {
        if (!this.setRotation(r))
            return;

        this.prevTileX = this.tileX;
        this.prevTileY = this.tileY;

        if (r == 0)
            this.tileX++;
        else if (r == 1)
            this.tileY--;
        else if (r == 2)
            this.tileX--;
        else if (r == 3)
            this.tileY++;

        if (Game.game.world.isSolid(this.tileX, this.tileY))
        {
            this.tileX = this.prevTileX;
            this.tileY = this.prevTileY;
            this.walking = false;
        }
        else
            this.walking = true;

        Game.game.world.updateLoadedRegions(this.tileX, this.tileY);
    }

    public void updateRunning()
    {
        if (running)
            this.runPercentage = Math.min(1, this.runPercentage + Game.game.frameFrequency / 25.0);
        else
            this.runPercentage = Math.max(0, this.runPercentage - Game.game.frameFrequency / 25.0);

        this.baseMoveSpeed = this.baseRunSpeed * this.runPercentage + this.baseWalkSpeed * (1 - this.runPercentage);
        this.acceleration = this.runAcceleration * this.runPercentage + this.walkAcceleration * (1 - this.runPercentage);
        this.turnDuration = this.runTurnDuration * this.runPercentage + this.walkTurnDuration * (1 - this.runPercentage);
    }

    public void draw()
    {
        double frac = (this.turnTime) / this.currentTurnDuration;
        double frac2 = -Math.pow(frac * 2, 3) * 0.25 + Math.pow(frac * 2, 2) * 0.75;

        double r = this.rotation * (1 - frac2) + this.prevRotation * frac2;

        this.drawing.drawModel(this.model, this.posX, this.posY, this.posZ, 1, 1, 1, 0,  Math.PI / 2, (r - 1) * Math.PI / 2);

        //Game.game.drawing.setColor(255, 255, 0);
        //Game.game.drawing.fillBox(this.tileX, this.tileY, -0.2, 1, 1, 0.3);
        //Game.game.drawing.setColor(255, 127, 0);
        //Game.game.drawing.fillBox(this.prevTileX, this.prevTileY, -0.2, 1, 1, 0.3);
    }

    public static void loadAnimations()
    {
        idleHandsAnimation = new PosedModelPose(Game.game.fileManager, "/models/mustard-test/idlehands.pmp");
        standAnimation = new PosedModelPose(Game.game.fileManager, "/models/mustard-test/stand.pmp");
        walkAnimation = new PosedModelAnimation(Game.game.fileManager, "/models/mustard-test-2/walk.pma");
        runAnimation = new PosedModelAnimation(Game.game.fileManager, "/models/mustard-test-2/run.pma");
    }
}
