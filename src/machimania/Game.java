package machimania;

import basewindow.*;
import basewindow.transformation.Shear;
import machimania.gui.screen.*;
import machimania.input.InputBindingGroup;
import machimania.input.InputBindings;
import machimania.machine.MachineElement;
import machimania.region.ShaderGrass;
import machimania.region.ShaderWater;

import java.util.ArrayList;

public class Game implements IUpdater, IDrawer, IWindowHandler
{
    public enum Framework {lwjgl, libgdx}
    public static Game game = new Game();

    protected boolean firstFrame = true;

    public Framework framework;
    public BaseWindow window;
    public BaseFileManager fileManager;
    public Drawing drawing = new Drawing();
    public InputBindings input;

    public ArrayList<InputBindingGroup> inputBindings = new ArrayList<>();

    public Screen screen;
    public ArrayList<ScreenOverlay> screenOverlays = new ArrayList<>();

    public boolean debug = false;
    public boolean vsync = true;
    public boolean antialiasing = false;

    public double frameFrequency;

    public World world;
    public HumanCharacter character;

    public ShaderGrass shaderGrass;
    public ShaderWater shaderWater;

    public ScreenOverlayFPS fpsCounter;

    public static BattleRuleset rules = new BattleRuleset();
    public static BattlePlayer debugPlayer = new BattlePlayer(rules);
    public static BattlePlayer debugEnemy = new BattlePlayer(rules);

    public void initialize()
    {
        MachineElement.initialize();
        debugEnemy.team = 1;
    }

    public void loadResources()
    {
        HumanCharacter.loadAnimations();
        drawing.updateDimensions();

        world = new World();

        character = new HumanCharacter(drawing, "/models/mustard-test-2/", world);
        firstFrame = false;

        window.setShadowQuality(2);
        window.lightBaseTransformation[1] = new Shear(window, 0, 0, 0, 0, 0.5, -0.5);

        ScreenOverlayMessage s = new ScreenOverlayMessage(this.drawing);
        /*s.addMessage("Welcome to Machimania! You can move the character with the arrow keys. Press space, enter, or the right arrow to see the next message or speed up the rate at which this text appears.");
        s.addMessage("Use WASD to rotate and = and - to zoom the camera.");
        s.addMessage("You can toggle running mode with right shift.");
        s.addMessage("To enter a battle, press M.");
        s.addMessage("This is still a very early development phase. Things are likely buggy and subject to change.");*/
        s.addMessage("im tired of clicking through text");

        fpsCounter = new ScreenOverlayFPS(this.drawing);

        screenOverlays.add(s);
        screen = new ScreenWorld(drawing);

        input = new InputBindings();

        try
        {
            this.shaderGrass = new ShaderGrass(this.window);
            this.shaderGrass.initialize();

            this.shaderWater = new ShaderWater(this.window);
            this.shaderWater.initialize();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void update()
    {
        if (firstFrame)
            loadResources();

        this.frameFrequency = window.frameFrequency;
        drawing.updateDimensions();

        for (int i = 0; i < screenOverlays.size(); i++)
        {
            if (screenOverlays.get(i).finished)
            {
                screenOverlays.remove(i);
                i--;
            }
        }

        if (screenOverlays.isEmpty())
            screen.update();
        else
            screenOverlays.get(screenOverlays.size() - 1).update();

        fpsCounter.update();
    }

    @Override
    public void draw()
    {
        screen.draw();

        for (ScreenOverlay s: screenOverlays)
            s.draw();

        fpsCounter.draw();
    }

    @Override
    public boolean attemptCloseWindow()
    {
        return true;
    }

    @Override
    public void onWindowClose()
    {

    }


    public static String formatString(String s)
    {
        if (s.length() == 0)
            return s;
        else if (s.length() == 1)
            return s.toUpperCase();
        else
            return Character.toUpperCase(s.charAt(0)) + s.substring(1).replace("-", " ").replace("_", " ").toLowerCase();
    }

}
