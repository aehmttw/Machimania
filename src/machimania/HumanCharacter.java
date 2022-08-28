package machimania;

import basewindow.*;
import machimania.gui.screen.ScreenOverlayMessage;
import machimania.region.Region;

import java.awt.*;
import java.util.Arrays;

public class HumanCharacter
{
    public static PosedModelPose idleHandsAnimation;

    public static PosedModelPose standAnimation;
    public static PosedModelAnimation walkAnimation;
    public static PosedModelAnimation runAnimation;

    protected Drawing drawing;
    public PosedModel model;
    public double animationTimer = 0;

    public double posX, posY, posZ;

    public double r,c,z;

    public boolean running = false;
    public static double baseWalkSpeed = -0.02;
    public static double baseRunSpeed = 4*baseWalkSpeed;
    public static double baseReverseSpeed = -baseWalkSpeed/2;
    public static double acceleration = 0.005;
    public static double friction = 0.00018;
    public static double baseTurnSpeed = Math.PI/144;

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
        //Update Render for next Frame
        Game.game.world.updateLoadedRegions(this.posX, this.posY);

        //Reset Pose
        this.model.resetBones();
        standAnimation.apply(this.model, 1);

        //Set local ground height
        Region region = this.world.getRegion(this.posX, this.posY);
        double localelev = 0;
        if (region != null)
            localelev = region.getHeightAt(this.posX, this.posY);

        //Binding keys to actions
        if (Game.game.input.moveUp.isPressed())
            this.r -=  this.acceleration*Game.game.frameFrequency;

        if (Game.game.input.moveDown.isPressed())
            this.r += this.acceleration*Game.game.frameFrequency;

        if (Game.game.input.moveLeft.isPressed())
            this.c += this.baseTurnSpeed*Game.game.frameFrequency;

        if (Game.game.input.moveRight.isPressed())
            this.c -= this.baseTurnSpeed*Game.game.frameFrequency;

        if (Game.game.input.sprint.isPressed())
            this.running = true;
        else
            this.running = false;

        //Enforce Limits
        if (this.running && this.r < this.baseRunSpeed)
            this.r = this.baseRunSpeed;
        if (!this.running && this.r < this.baseWalkSpeed)
            this.r += (this.acceleration - this.friction)*Game.game.frameFrequency;
        else if (this.r > this.baseReverseSpeed)
            this.r = this.baseReverseSpeed;
        else if (Math.abs(this.r) < 0.00001)
            this.r = 0;


        //Apply Forces
        if (Math.abs(this.r) > 0)
            this.r -= this.friction*Game.game.frameFrequency * sign(this.r);

        //Execute movements
        this.posX += this.r * Math.sin(this.c)*Game.game.frameFrequency;
        this.posY += this.r * Math.cos(this.c)*Game.game.frameFrequency;
        this.posZ = localelev;

        //Animation
        if(Math.abs(this.r) > 0){
            if(this.r < this.baseWalkSpeed) {
                runAnimation.apply(this.model, animationTimer, Math.abs(this.r/this.baseRunSpeed));
                this.animationTimer += 4 * Game.game.frameFrequency;
            } else if (this.r > this.baseWalkSpeed){
                walkAnimation.apply(this.model, animationTimer, Math.abs(this.r / this.baseWalkSpeed));
                this.animationTimer += 2 * Game.game.frameFrequency;
            }
        }else {
            idleHandsAnimation.apply(this.model, 1);
            animationTimer = 0;
        }
    }

    public static int sign(double x){
        return x != 0 ? (int)(Math.abs(x)/x) : 0;
    }

    public void draw()
    {
        this.drawing.drawModel(this.model, this.posX, this.posY, this.posZ, 1, 1, 1, 0,  Math.PI / 2, c);

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
