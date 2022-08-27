package main;

import basewindow.ComputerFileManager;
import lwjglwindow.LWJGLWindow;
import machimania.Game;

import java.io.File;

public class Machimania
{
    public static void main(String[] args)
    {
        Game.game.framework = Game.Framework.lwjgl;

        boolean relaunch = System.getProperties().toString().contains("Mac OS X");

        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("debug"))
                Game.game.debug = true;

            if (args[i].equals("mac") || args[i].equals("no_relaunch"))
                relaunch = false;
        }

        if (relaunch && Game.game.framework == Game.Framework.lwjgl)
        {
            try
            {
                String path = new File(Machimania.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();

                if (path.endsWith(".jar"))
                {
                    String[] command = new String[]{"java", "-XstartOnFirstThread", "-jar", path, "mac"};
                    Runtime.getRuntime().exec(command);
                    Runtime.getRuntime().exit(0);
                    return;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if (Game.game.framework == Game.Framework.lwjgl)
            Game.game.fileManager = new ComputerFileManager();

        Game.game.initialize();

        if (Game.game.framework == Game.Framework.lwjgl)
        {
            Game.game.window = new LWJGLWindow("Machimania", 1400, 900, (int) Game.game.drawing.interfaceDepth, Game.game, Game.game, Game.game, Game.game.vsync, true);
            Game.game.window.antialiasingEnabled = Game.game.antialiasing;
        }

        Game.game.window.run();
    }
}
