package machimania.gui.screen;

import machimania.Drawing;

public abstract class ScreenOverlay extends Screen
{
    public boolean finished = false;

    public ScreenOverlay(Drawing d)
    {
        super(d);
    }
}
