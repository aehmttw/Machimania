package machimania.region;

import machimania.Drawing;
import machimania.Game;

public class Region
{
    public final int posX;
    public final int posY;

    public final int sizeX;
    public final int sizeY;

    public final Tile[][] tiles;

    public boolean loaded = false;
    public boolean loadedLowDetail = false;
    public GroundRenderer groundRenderer;
    public WaterRenderer waterRenderer;
    public GroundRendererLowDetail groundRendererLowDetail;

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

    public double getHeightAtAbsolute(double x, double y)
    {
        //return Math.max(Math.abs(x), Math.abs(y));
        return (Game.game.world.heightMap2048.get(x, y) * 1000
                + Game.game.world.heightMap256.get(x, y) * 100
                + Game.game.world.heightMap32.get(x, y) * 10
                + Game.game.world.heightMap8.get(x, y) * 2
                /*+ Game.game.world.heightMap2.get(x, y) * 0.5
                + Game.game.world.heightMap1.get(x, y) * 0.25*/);
    }

    public double getHeightAtRelative(double x, double y)
    {
        return getHeightAtAbsolute(x + this.posX, y + this.posY);
    }

    public boolean load()
    {
        if (this.loaded)
            return false;

        this.loaded = true;
        this.groundRenderer = new GroundRenderer(this);
        this.waterRenderer = new WaterRenderer(this);
        return true;
    }

    public boolean unload()
    {
        if (!this.loaded)
            return false;

        this.loaded = false;
        this.groundRenderer.free();
        this.groundRenderer = null;
        this.waterRenderer.free();
        this.waterRenderer = null;
        return true;
    }

    public boolean loadLowDetail()
    {
        if (this.loadedLowDetail)
            return false;

        this.loadedLowDetail = true;
        this.groundRendererLowDetail = new GroundRendererLowDetail(this);
        return true;
    }

    public boolean unloadLowDetail()
    {
        if (!this.loadedLowDetail)
            return false;

        this.loadedLowDetail = false;
        this.groundRendererLowDetail.free();
        this.groundRendererLowDetail = null;
        return true;
    }

    public float[] getNormalAt(double x, double y)
    {
        double x1 = this.getHeightAtAbsolute(this.posX + x - 0.1, this.posY + y);
        double x2 = this.getHeightAtAbsolute(this.posX + x + 0.1, this.posY + y);
        double y1 = this.getHeightAtAbsolute(this.posX + x, this.posY + y - 0.1);
        double y2 = this.getHeightAtAbsolute(this.posX + x, this.posY + y + 0.1);

        double dx = 5 * (x2 - x1);
        double dy = 5 * (y2 - y1);
        double size = Math.sqrt(dx * dx + dy * dy + 1);

        return new float[]{(float) (-dx / size), (float) (-dy / size), (float) (-1 / size)};
    }

    public double getSlopeAtFacing(double x1, double y1, double x2, double y2)
    {
        double p1 = this.getHeightAtAbsolute(this.posX + x1, this.posY + y1);
        double p2 = this.getHeightAtAbsolute(this.posX + x2, this.posY + y2);

        return (p2 - p1) / Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public void update()
    {

    }

    public void drawTiles(Drawing d)
    {
        if (this.loaded)
        {
//            for (int i = 0; i < tiles.length; i++)
//            {
//                for (int j = 0; j < tiles[i].length; j++)
//                {
//                    tiles[i][j].drawTile(d);
//                }
//            }

            this.groundRenderer.draw();
        }
        else if (this.loadedLowDetail)
        {
            Game.game.drawing.setColor(255, 255, 255);
            Game.game.window.shaderBase.useNormal.set(true);

            double light = 1;
            for (int i = 0; i < this.groundRendererLowDetail.ambientAdjusted.length; i++)
            {
                this.groundRendererLowDetail.ambientAdjusted[i] = (float) (this.groundRendererLowDetail.ambient[i] * (1 - light) + this.groundRendererLowDetail.diffuse[i] * light);
            }

            Game.game.window.setMaterialLights(this.groundRendererLowDetail.ambientAdjusted, this.groundRendererLowDetail.diffuse, this.groundRendererLowDetail.specular, 25, 0, 1, false);
            this.groundRendererLowDetail.draw();
            Game.game.window.setMaterialLights(this.groundRendererLowDetail.ambient, this.groundRendererLowDetail.diffuse, this.groundRendererLowDetail.specular, 25, 0, 1, false);

            Game.game.window.shaderBase.useNormal.set(false);
            Game.game.window.disableMaterialLights();
        }
    }

    public double distToPlayer()
    {
        return Math.sqrt(Math.pow(this.posX + this.sizeX / 2.0 - Game.game.character.posX, 2) + Math.pow(this.posY + this.sizeY / 2.0 - Game.game.character.posY, 2));
    }

    public void drawTilesTransparent(Drawing d)
    {
//        if (this.loaded)
//        {
//            this.waterRenderer.draw();
//        }
    }
}
