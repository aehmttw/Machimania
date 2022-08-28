package machimania.region;

import machimania.Drawing;
import machimania.Game;
import machimania.MachimaniaBattle;

import java.util.ArrayList;

public class Region
{
    public final int posX;
    public final int posY;

    public final int sizeX;
    public final int sizeY;

    public final Tile[][] tiles;

    public boolean loaded = false;
    public GroundRenderer groundRenderer;

    public Region(int posX, int posY, int sizeX, int sizeY)
    {
        this.posX = posX;
        this.posY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        this.tiles = new Tile[sizeX][sizeY];

        for (int i = 0; i < tiles.length; i++)
        {
            for (int j = 0; j < tiles[i].length; j++)
            {
                tiles[i][j] = new Tile(this, i, j);
            }
        }

        /*for (int i = 0; i < 8; i++)
        {
            tiles[i][0].isSolid = true;
            tiles[0][i].isSolid = true;
        }

        if (Math.random() < 0.5)
            tiles[4][0].isSolid = false;

        if (Math.random() < 0.5)
            tiles[0][4].isSolid = false;*/
    }

    public double getHeightAt(double x, double y)
    {
        //return Math.max(Math.abs(x), Math.abs(y));
        return Game.game.world.heightMap256.get(x, y) * 100 + Game.game.world.heightMap32.get(x, y) * 10 + Game.game.world.heightMap8.get(x, y) * 2 + Game.game.world.heightMap2.get(x, y) * 0.5;
    }

    public boolean load()
    {
        if (this.loaded)
            return false;

        this.loaded = true;
        this.groundRenderer = new GroundRenderer(this);
        return true;
    }

    public boolean unload()
    {
        if (!this.loaded)
            return false;

        this.loaded = false;
        this.groundRenderer.free();
        this.groundRenderer = null;
        return true;
    }

    public float[] getNormalAt(double x, double y)
    {
        double x1 = this.getHeightAt(this.posX + x - 0.1, this.posY + y);
        double x2 = this.getHeightAt(this.posX + x + 0.1, this.posY + y);
        double y1 = this.getHeightAt(this.posX + x, this.posY + y - 0.1);
        double y2 = this.getHeightAt(this.posX + x, this.posY + y + 0.1);

        double dx = 5 * (x2 - x1);
        double dy = 5 * (y2 - y1);
        double size = Math.sqrt(dx * dx + dy * dy + 1);

        return new float[]{(float) (-dx / size), (float) (-dy / size), (float) (-1 / size)};
    }

    public void update()
    {

    }

    public void drawTiles(Drawing d)
    {
        if (this.loaded)
        {
            for (int i = 0; i < tiles.length; i++)
            {
                for (int j = 0; j < tiles[i].length; j++)
                {
                    tiles[i][j].drawTile(d);
                }
            }

            this.groundRenderer.draw();
        }
    }
}
