package machimania.gui.screen;

import basewindow.InputCodes;
import machimania.Drawing;
import machimania.Game;
import machimania.input.InputBindings;

import java.util.ArrayList;

public class ScreenOverlayMessage extends ScreenOverlay
{
    public final double width = 1000;
    public final double height = 100;

    public ArrayList<Message> messages = new ArrayList<>();

    public double posX = 200;
    public double posY;

    public ScreenOverlayMessage(Drawing d)
    {
        super(d);

        this.posY = d.interfaceBoundBottom - 100;
    }

    @Override
    public void update()
    {
        if (messages.size() <= 0)
        {
            this.finished = true;
            Game.game.window.validPressedKeys.clear();
            Game.game.window.pressedKeys.clear();
            return;
        }

        Message m = messages.get(0);
        this.posY = this.drawing.interfaceBoundBottom - 100;
        m.update();

        if (Game.game.input.advance.isValid() || Game.game.window.validPressedKeys.contains(InputCodes.KEY_RIGHT))
        {
            Game.game.input.advance.invalidate();
            Game.game.window.validPressedKeys.remove((Integer) InputCodes.KEY_RIGHT);

            if (m.finished)
                messages.remove(0);
            else
                m.fast = true;
        }

        if (!Game.game.input.advance.isPressed() && !Game.game.window.pressedKeys.contains(InputCodes.KEY_RIGHT))
            m.fast = false;
    }

    @Override
    public void draw()
    {
        if (messages.size() <= 0)
            return;

        this.drawing.setColor(255, 255, 255);
        this.drawing.fillInterfaceRect(this.posX + this.width / 2, this.posY, this.width * 1.2, this.height * 1.2);
        this.messages.get(0).draw();

        if (this.messages.get(0).finished && (System.currentTimeMillis() / 500 ) % 2 == 1 && this.messages.size() > 1)
            this.drawing.drawInterfaceText(this.posX + this.width * 1.05, this.posY, "->");
    }

    public void addMessage(String m)
    {
        this.messages.add(new Message(this, m));
    }
}
