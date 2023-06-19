package machimania.region;

import machimania.Drawing;

public class Tile
{
    public Region region;

    public boolean isSolid = false;
    public boolean canInteract = false;

    public boolean hasWater = true;
    public double waterLevel = 20;

    public boolean update = false;

    public double posX;
    public double posY;

    public Tile(Region r, double x, double y)
    {
        this.region = r;
        this.posX = x;
        this.posY = y;
    }

    public void drawTile(Drawing d)
    {
        if (!isSolid)
            return;

        if ((this.posX + this.region.posX + this.posY + this.region.posY) % 2 == 0)
        {
            if (isSolid)
                d.setColor(255, 255, 0);
            else
                d.setColor(0, 255, 0);
        }
        else
        {
            if (isSolid)
                d.setColor(200, 200, 0);
            else
                d.setColor(0, 200, 0);
        }

        if (isSolid)
            d.fillBox(this.posX + this.region.posX, this.posY + this.region.posY, -0.2, 1, 1, 1.2);
        else
            d.fillBox(this.posX + this.region.posX, this.posY + this.region.posY, -0.2, 1, 1, 0.2);
    }
}
