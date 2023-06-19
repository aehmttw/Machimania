package machimania;

import basewindow.*;
import machimania.gui.screen.ScreenOverlayMessage;
import machimania.region.Region;

public class HumanCharacter
{
    public static PosedModelPose idleHandsAnimation;

    public static PosedModelPose standAnimation;
    public static PosedModelAnimation walkAnimation;
    public static PosedModelAnimation runAnimation;
    public static PosedModelPose catapultAnimation;

    protected Drawing drawing;
    public PosedModel model;

    public double age = 0;
    public double animationTimer = 0;

    public int tileX;
    public int tileY;

    public double lastPosX;
    public double lastPosY;
    public double lastPosZ;

    public double posX;
    public double posY;
    public double posZ;

    public double vX;
    public double vY;
    public double vZ;

    public double direction;
    public double pitch;
    public double yaw;

    public double turnTime;

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
    public double friction = 0.05;

    public double walkTurnDuration = 50;
    public double runTurnDuration = 25;
    public double turnDuration = 25;
    public double currentTurnDuration = 25;

    public double posZOffset = 0;

    public double gravity = 0.00098;
    public boolean grounded = false;

    public boolean catapultMode = false;
    public double catapultTime = 0;
    public double peakCatapultHeight;
    public boolean catapultRotationLock = false;
    public double catapultRotationPitch = 0;

    public World world;

    public Model testModel;

    public HumanCharacter(Drawing d, String model, World world)
    {
        this.world = world;
        this.drawing = d;
        Model m = d.createModel(model);
        this.model = Game.game.window.createPosedModel(m);

        Model m2 = d.createModel("/models/sphere/");
        this.testModel = m2;
    }

    public void update()
    {
        //pls change this TODO
        this.tileX = (int) Math.round(this.posX);
        this.tileY = (int) Math.round(this.posY);

        Game.game.world.updateLoadedRegions(this.tileX, this.tileY, false);
        Game.game.world.updateLoadedRegions(this.tileX, this.tileY, true);

        Region region = this.world.getRegion(this.posX, this.posY);

        this.model.resetBones();

        if (!this.catapultRotationLock)
        {
            this.direction = (this.direction + Math.PI * 2) % (Math.PI * 2);
            double dist = Math.sqrt(Math.pow(this.posX - this.lastPosX, 2) + Math.pow(this.posY - this.lastPosY, 2));

            double dir = Math.PI + this.getAngleInDirection(this.lastPosX, this.lastPosY);
            this.direction -= angleBetween(this.direction, dir) * 3 * dist;
        }

        this.lastPosX = this.posX;
        this.lastPosY = this.posY;
        this.lastPosZ = this.posZ;

        boolean up = Game.game.input.moveUp.isPressed();
        boolean down = Game.game.input.moveDown.isPressed();
        boolean left = Game.game.input.moveLeft.isPressed();
        boolean right = Game.game.input.moveRight.isPressed();
        boolean jump = Game.game.input.moveJump.isPressed();

        if (this.grounded && jump)
        {
            this.grounded = false;
            this.vZ = 0.05;
        }

        double acceleration = this.acceleration;
        double maxVelocity = this.baseMoveSpeed;

        double x = 0;
        double y = 0;

        double a = -1;

        if (left)
            x -= 1;

        if (right)
            x += 1;

        if (up)
            y -= 1;

        if (down)
            y += 1;

        if (x == 1 && y == 0)
            a = 0;
        else if (x == 1 && y == 1)
            a = Math.PI / 4;
        else if (x == 0 && y == 1)
            a = Math.PI / 2;
        else if (x == -1 && y == 1)
            a = 3 * Math.PI / 4;
        else if (x == -1 && y == 0)
            a = Math.PI;
        else if (x == -1 && y == -1)
            a = 5 * Math.PI / 4;
        else if (x == 0 && y == -1)
            a = 3 * Math.PI / 2;
        else if (x == 1 && y == -1)
            a = 7 * Math.PI / 4;

        if (a == -1 && grounded)
        {
            this.vX *= Math.pow(1 - (this.friction), Game.game.frameFrequency);
            this.vY *= Math.pow(1 - (this.friction), Game.game.frameFrequency);

            if (Math.abs(this.vX) < 0.001)
                this.vX = 0;

            if (Math.abs(this.vY) < 0.001)
                this.vY = 0;
        }

        double speed = this.getSpeed();

        if (speed > maxVelocity && grounded)
            this.setPolarMotion(this.getPolarDirection(), maxVelocity);

        if (grounded)
        {
            if (a >= 0)
                this.addPolarMotion(a, acceleration * Game.game.frameFrequency);
        }

        this.posX += this.vX * Game.game.frameFrequency;
        this.posY += this.vY * Game.game.frameFrequency;
        this.posZ += this.vZ * Game.game.frameFrequency;

        this.vZ -= this.gravity;

        if (catapultMode)
            this.catapultTime += Game.game.frameFrequency;
        else
            this.catapultTime = 0;

        if (region != null)
        {
            double groundHeight = region.getHeightAtAbsolute(this.posX, this.posY) + posZOffset;
            if (this.posZ <= groundHeight)
            {
                if (!catapultMode)
                {
                    this.posZ = groundHeight;
                    this.vZ = 0;
                }
                else
                {
                    this.posZ = 2 * groundHeight - posZ;
                    double z = Math.sqrt(this.vX * this.vX + this.vY * this.vY + this.vZ * this.vZ);
                    float[] n = region.getNormalAt(this.posX - region.posX, this.posY - region.posY);
                    this.vX = n[0] * z;
                    this.vY = n[1] * z;
                    this.vZ = -n[2] * z * 0.3;
                    this.catapultRotationLock = true;
                    this.catapultRotationPitch = pitch;

                    if (Math.abs(this.vZ) <= 0.01)
                    {
                        this.catapultMode = false;
                        this.catapultRotationLock = false;
                        this.vZ = 0;
                        this.posZ = groundHeight;
                    }
                }
            }
            else if (this.catapultMode)
            {
                double frac = Math.pow(0.995, Game.game.frameFrequency);
                this.vX *= frac;
                this.vY *= frac;
            }

            this.grounded = this.posZ <= groundHeight + 0.02;
        }


        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_K) && region != null)
            this.posZ = region.getHeightAtAbsolute(this.posX, this.posY) + posZOffset;

        if (Game.game.window.validPressedKeys.contains(InputCodes.KEY_B))
        {
            Game.game.window.validPressedKeys.remove((Integer) InputCodes.KEY_B);
            this.catapultMode = true;
            this.grounded = false;
            this.addPolarMotion(this.direction, 0.1);
            this.vZ = 0.05;
        }

        if (Game.game.window.validPressedKeys.contains(InputCodes.KEY_RIGHT_SHIFT))
        {
            Game.game.window.validPressedKeys.remove((Integer) InputCodes.KEY_RIGHT_SHIFT);
            this.running = !this.running;
        }

        if (this.posZ < -16)
        {
            this.posX = 0;
            this.posY = 0;
            this.posZ = 0;

            ScreenOverlayMessage s = new ScreenOverlayMessage(this.drawing);
            s.addMessage("Oh noes! It looks like you fell off the map!");
            s.addMessage("Let's get you back!");
            Game.game.screenOverlays.add(s);
        }

        this.updateRunning();

        idleHandsAnimation.apply(this.model, 1);
        standAnimation.apply(this.model, 1);

        double catapultFrac = Math.min(1, this.catapultTime / 50.0);
        if (this.catapultMode)
            catapultAnimation.apply(this.model, catapultFrac);

        this.walkSpeed = speed;

        double t = 0.05;
        if (this.walkSpeed - this.smoothMoveSpeed > this.baseMoveSpeed * t * Game.game.frameFrequency)
            this.smoothMoveSpeed += this.baseMoveSpeed * t * Game.game.frameFrequency;
        else if (this.walkSpeed - this.smoothMoveSpeed < -this.baseMoveSpeed * t * Game.game.frameFrequency)
            this.smoothMoveSpeed -= this.baseMoveSpeed * t * Game.game.frameFrequency;
        else
            this.smoothMoveSpeed = this.walkSpeed;

        this.animationTimer += (1.4 + 0.6 * this.runPercentage) * Game.game.frameFrequency;

        walkAnimation.apply(this.model, this.animationTimer, (1 - catapultFrac) * (1 - this.runPercentage) * this.smoothMoveSpeed / this.baseMoveSpeed);
        runAnimation.apply(this.model, this.animationTimer, (1 - catapultFrac) * this.runPercentage * this.smoothMoveSpeed / this.baseMoveSpeed);

        this.age += Game.game.frameFrequency;

        this.pitch = Math.PI / 2;
        this.calculateCatapultAngles();
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

    public void calculateCatapultAngles()
    {
        if (this.catapultMode)
        {
            Region region = this.world.getRegion(this.posX, this.posY);

            if (region == null)
                return;

            double h = region.getHeightAtAbsolute(this.posX, this.posY);
            pitch += Math.atan(this.vZ / Math.sqrt(this.vX * this.vX + this.vY * this.vY + this.vZ * this.vZ));

            if (this.catapultRotationLock)
            {
                pitch = catapultRotationPitch;
                // r = - Math.PI / 2 - this.catapultRotationOrientation;
            }

            double speed = this.getSpeed();
            double angle = region.getSlopeAtFacing(this.posX, this.posY, this.posX + this.vX / speed * 2, this.posY + this.vY / speed * 2);
            double angle2 = region.getSlopeAtFacing(this.posX - this.vY / speed * 2, this.posY + this.vX / speed * 2, this.posX + this.vY / speed * 2, this.posY - this.vX / speed * 2);

            if (this.vZ <= 0)
            {
                double splatDist = 2;
                if (this.posZ <= Math.min(h + splatDist, this.peakCatapultHeight))
                {
                    double splatFrac = 1 - (this.posZ - h) / Math.min(splatDist, this.peakCatapultHeight - h);
                    pitch = (pitch * (1 - splatFrac) + angle * splatFrac);
                    yaw = (yaw * (1 - splatFrac) + angle2 * splatFrac);
                }
            }
            else
            {
                this.peakCatapultHeight = this.posZ;
            }
        }
    }

    public void draw()
    {
        double frac = (this.turnTime) / this.currentTurnDuration;
        double frac2 = -Math.pow(frac * 2, 3) * 0.25 + Math.pow(frac * 2, 2) * 0.75;

        double r = - Math.PI / 2 - this.direction; //this.rotation * (1 - frac2) + this.prevRotation * frac2;

        this.drawing.drawModel(this.model, this.posX, this.posY, this.posZ, 1, 1, 1, yaw,  pitch, r /*(r - 1) * Math.PI / 2*/);
        //this.drawing.drawModel(this.testModel, this.posX, this.posY, this.posZ, 1, 1, 1, -r, pitch, yaw);

        //Game.game.drawing.setColor(255, 255, 0);
        //Game.game.drawing.fillBox(this.tileX, this.tileY, -0.2, 1, 1, 0.3);
        //Game.game.drawing.setColor(255, 127, 0);
        //Game.game.drawing.fillBox(this.prevTileX, this.prevTileY, -0.2, 1, 1, 0.3);
    }

    public static void loadAnimations()
    {
        idleHandsAnimation = new PosedModelPose(Game.game.fileManager, "/models/mustard-test-2/idlehands.pmp");
        standAnimation = new PosedModelPose(Game.game.fileManager, "/models/mustard-test-2/stand.pmp");
        catapultAnimation = new PosedModelPose(Game.game.fileManager, "/models/mustard-test-2/oof.pmp");
        walkAnimation = new PosedModelAnimation(Game.game.fileManager, "/models/mustard-test-2/walk.pma");
        runAnimation = new PosedModelAnimation(Game.game.fileManager, "/models/mustard-test-2/run.pma");
    }

    public double getPolarDirection()
    {
        double angle = 0;
        if (this.vX > 0)
            angle = Math.atan(this.vY/this.vX);
        else if (this.vX < 0)
            angle = Math.atan(this.vY/this.vX) + Math.PI;
        else
        {
            if (this.vY > 0)
                angle = Math.PI / 2;
            else if (this.vY < 0)
                angle = Math.PI * 3 / 2;
        }

        return angle;
    }

    public double getSpeed()
    {
        return Math.sqrt(this.vX * this.vX + this.vY * this.vY);
    }

    public void setPolarMotion(double angle, double velocity)
    {
        double velX = velocity * Math.cos(angle);
        double velY = velocity * Math.sin(angle);
        this.vX = velX;
        this.vY = velY;
    }

    public void addPolarMotion(double angle, double velocity)
    {
        double velX = velocity * Math.cos(angle);
        double velY = velocity * Math.sin(angle);
        this.vX += velX;
        this.vY += velY;
    }

    public double getAngleInDirection(double x, double y)
    {
        x -= this.posX;
        y -= this.posY;

        double angle = 0;
        if (x > 0)
            angle = Math.atan(y/x);
        else if (x < 0)
            angle = Math.atan(y/x) + Math.PI;
        else
        {
            if (y > 0)
                angle = Math.PI / 2;
            else if (y < 0)
                angle = Math.PI * 3 / 2;
        }

        return angle;
    }

    public static double angleBetween(double a, double b)
    {
        return (a - b + Math.PI * 3) % (Math.PI*2) - Math.PI;
    }

    public static double absoluteAngleBetween(double a, double b)
    {
        return Math.abs((a - b + Math.PI * 3) % (Math.PI * 2) - Math.PI);
    }
}
