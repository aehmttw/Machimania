package machimania.gui.screen;

import machimania.Drawing;
import machimania.Game;

import java.util.ArrayList;

public class Message
{
    public final double speed = 0.2;
    public final double fastSpeed = 1.6;

    public ScreenOverlayMessage screen;
    public ArrayList<String> messageLines;
    public double progress = 0;

    public boolean fast = false;
    public boolean finished = false;

    public Message(ScreenOverlayMessage screen, String message)
    {
        this.screen = screen;
        this.messageLines = screen.drawing.wrapText(message, screen.width, screen.textSize);
    }

    public void update()
    {
        double s = this.speed;
        if (fast)
            s = this.fastSpeed;

        this.progress += s * Game.game.frameFrequency;

        if (!finished)
        {
            int chars = 0;
            for (String m: this.messageLines)
            {
                chars += m.length();
            }

            if (chars < this.progress)
                this.finished = true;
        }
    }

    public void draw()
    {
        Drawing d = this.screen.drawing;

        double verticalSpace = 30;

        int chars = 0;
        for (int i = 0; i < this.messageLines.size(); i++)
        {
            String m = this.messageLines.get(i);
            String s = m.substring(0, Math.max(0, Math.min(m.length(), (int) (this.progress - chars))));

            d.setColor(0, 0, 0);
            d.setInterfaceFontSize(screen.textSize);
            d.drawInterfaceText(this.screen.posX, this.screen.posY + (-this.messageLines.size() + 1 + i * 2) * verticalSpace / 2, s, false);

            chars += m.length();
        }

    }
}
