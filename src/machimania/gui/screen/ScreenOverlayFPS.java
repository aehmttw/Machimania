package machimania.gui.screen;

import machimania.Drawing;

public class ScreenOverlayFPS extends ScreenOverlay
{
    public long lastSecond = 0;
    public int frames = 0;
    public int lastFPS = 0;

    public ScreenOverlayFPS(Drawing d)
    {
        super(d);
    }

    @Override
    public void update()
    {
        frames++;

        long second = System.currentTimeMillis() / 1000;

        if (second > this.lastSecond)
        {
            this.lastSecond = second;
            this.lastFPS = frames;
            frames = 0;
        }
    }

    @Override
    public void draw()
    {
        this.drawing.setColor(255, 255, 255);
        this.drawing.fillInterfaceRect(-this.drawing.gameMarginX + 70, this.drawing.gameMarginY + 30, 60, 20);
        this.drawing.setColor(0, 0, 0);
        this.drawing.setInterfaceFontSize(12);
        this.drawing.drawInterfaceText(-this.drawing.gameMarginX + 70, this.drawing.gameMarginY + 30, "FPS: " + lastFPS);
    }
}
