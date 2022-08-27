package machimania.gui.screen;

import machimania.Drawing;

public abstract class Screen
{
    protected Drawing drawing;

    public double textSize = 24;
    public double titleSize = 32;
    public double objWidth = 350;
    public double objHeight = 40;
    public double objXSpace = 380;
    public double objYSpace = 60;

    public double centerX = 700;
    public double centerY = 450;

    public Screen(Drawing d)
    {
        this.drawing = d;
    }

    public abstract void update();

    public abstract void draw();
}
