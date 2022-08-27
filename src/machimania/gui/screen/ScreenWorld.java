package machimania.gui.screen;

import basewindow.BaseWindow;
import basewindow.InputCodes;
import basewindow.transformation.RotationAboutPoint;
import basewindow.transformation.ScaleAboutPoint;
import basewindow.transformation.Translation;
import machimania.BattlePlayer;
import machimania.Drawing;
import machimania.Game;
import machimania.MachimaniaBattle;

public class ScreenWorld extends Screen
{
    double y = 0;
    double p = 0;
    double r = 0;

    double dist = 0;

    public RotationAboutPoint perspectiveRotation;

    public ScreenWorld(Drawing d)
    {
        super(d);
        perspectiveRotation = new RotationAboutPoint(Game.game.window, 0, -Math.PI / 8, 0, 0, 0, -1);
    }

    @Override
    public void update()
    {
        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_W))
            p += 0.01;

        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_S))
            p -= 0.01;

        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_D))
            r += 0.01;

        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_A))
            r -= 0.01;

        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_EQUAL))
            dist -= 0.02;

        if (Game.game.window.pressedKeys.contains(InputCodes.KEY_MINUS))
            dist += 0.02;

        if (Game.game.window.validPressedKeys.contains(InputCodes.KEY_M))
        {
            Game.game.window.validPressedKeys.remove((Integer) InputCodes.KEY_M);
            MachimaniaBattle b = new MachimaniaBattle(Game.game.character.tileX, Game.game.character.tileY, 7, 5, new BattlePlayer[]{Game.debugPlayer, Game.debugEnemy});
            Game.game.world.battles.add(b);
            Game.game.screen = new ScreenBattle(drawing, this, b, Game.debugPlayer);
        }

        Game.game.character.update();

        this.drawing.gamePosX = Game.game.character.posX;
        this.drawing.gamePosY = Game.game.character.posY;
    }

    @Override
    public void draw()
    {
        perspectiveRotation = new RotationAboutPoint(Game.game.window, 0, -Math.PI / 8, 0, 0, 0, -1);
        BaseWindow window = Game.game.window;

        window.transformations.clear();
        window.transformations.add(new Translation(Game.game.window, 0, 0, -Game.game.character.posZ / drawing.gameDepth));
        window.transformations.add(new RotationAboutPoint(Game.game.window, 0, 0, r, 0, 0, -1));
        window.transformations.add(new RotationAboutPoint(Game.game.window, y, p, 0, 0, 0, -1));
        window.transformations.add(perspectiveRotation);
        window.transformations.add(new Translation(Game.game.window, 0, 0, -dist));
        ScaleAboutPoint s = ((ScaleAboutPoint) window.lightBaseTransformation[0]);
        s.x = 0.2 / (1 + dist);
        s.y = 0.2 / (1 + dist);
        s.z = 0.2 / (1 + dist);

        window.loadPerspective();

        Game.game.world.drawTiles(drawing);

        drawing.setColor(255, 255, 255);
        Game.game.character.draw();

        window.transformations.clear();
        window.loadPerspective();
    }
}
