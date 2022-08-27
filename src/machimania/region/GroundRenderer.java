package machimania.region;

import basewindow.BaseStaticBatchRenderer;
import basewindow.Color;
import machimania.Game;
import machimania.World;

public class GroundRenderer
{
    public Region region;
    public float[][] tileHeights;
    public float[][][] tileNormals;
    public Color[][] tileColors;
    public BaseStaticBatchRenderer batchRenderer;
    public GrassRenderer grassRenderer;

    public float[] ambient = new float[]{0.5f, 0.5f, 0.5f};
    public float[] diffuse = new float[]{0.7f, 0.7f, 0.7f};
    public float[] specular = new float[]{0.2f, 0.2f, 0.2f};

    public int tileBuffer = 2;

    public GroundRenderer(Region r)
    {
        this.region = r;
        World w = Game.game.world;

        this.tileColors = new Color[r.sizeX + 1][r.sizeY + 1];
        this.tileHeights = new float[r.sizeX + 1 + tileBuffer * 2][r.sizeY + 1 + tileBuffer * 2];
        this.tileNormals = new float[r.sizeX + 1 + tileBuffer * 2][r.sizeY + 1 + tileBuffer * 2][3];

        for (int i = -tileBuffer; i < r.tiles.length + 1 + tileBuffer; i++)
        {
            for (int j = -tileBuffer; j < r.tiles[0].length + 1 + tileBuffer; j++)
            {
                double rx = i + r.posX - 0.5;
                double ry = j + r.posY - 0.5;

                if (i >= 0 && i <= r.sizeX && j >= 0 && j <= r.sizeY)
                    tileColors[i][j] = new Color(0.6 * (1 * w.noiseMap16.get(rx, ry) + 0.3 * w.noiseMap4.get(rx, ry) + 0.15 * w.noiseMap1.get(rx, ry)), 0.9, 0.2, 1.0);
                tileHeights[i + tileBuffer][j + tileBuffer] = (float) r.getHeightAt(rx, ry);

                double x1 = r.getHeightAt(rx - 0.1, ry);
                double x2 = r.getHeightAt(rx + 0.1, ry);
                double y1 = r.getHeightAt(rx, ry - 0.1);
                double y2 = r.getHeightAt(rx, ry + 0.1);

                double dx = 5 * (x2 - x1);
                double dy = 5 * (y2 - y1);
                double size = Math.sqrt(dx * dx + dy * dy + 1);

                tileNormals[i + tileBuffer][j + tileBuffer] = new float[]{(float) (-dx / size), (float) (-dy / size), (float) (-1 / size)};
            }
        }

        this.batchRenderer = Game.game.window.createStaticBatchRenderer(Game.game.window.shaderBase, true, "/images/environment/ground.png", true, r.tiles.length * r.tiles[0].length * 6);

        for (int i = 0; i < r.tiles.length; i++)
        {
            for (int j = 0; j < r.tiles[i].length; j++)
            {
                batchRenderer.addVertex(i - 0.5f, j - 0.5f, tileHeights[i + tileBuffer][j + tileBuffer]);
                batchRenderer.addTexCoord(0, 0);
                batchRenderer.addColor(tileColors[i][j]);
                batchRenderer.addNormal(tileNormals[i + tileBuffer][j + tileBuffer]);
                batchRenderer.addVertex(i + 0.5f, j - 0.5f, tileHeights[i + 1 + tileBuffer][j + tileBuffer]);
                batchRenderer.addTexCoord(1, 0);
                batchRenderer.addColor(tileColors[i + 1][j]);
                batchRenderer.addNormal(tileNormals[i + 1 + tileBuffer][j + tileBuffer]);
                batchRenderer.addVertex(i - 0.5f, j + 0.5f, tileHeights[i + tileBuffer][j + 1 + tileBuffer]);
                batchRenderer.addTexCoord(0, 1);
                batchRenderer.addColor(tileColors[i][j + 1]);
                batchRenderer.addNormal(tileNormals[i + tileBuffer][j + 1 + tileBuffer]);

                batchRenderer.addVertex(i + 0.5f, j - 0.5f, tileHeights[i + 1 + tileBuffer][j + tileBuffer]);
                batchRenderer.addTexCoord(1, 0);
                batchRenderer.addColor(tileColors[i + 1][j]);
                batchRenderer.addNormal(tileNormals[i + 1 + tileBuffer][j + tileBuffer]);
                batchRenderer.addVertex(i - 0.5f, j + 0.5f, tileHeights[i + tileBuffer][j + 1 + tileBuffer]);
                batchRenderer.addTexCoord(0, 1);
                batchRenderer.addColor(tileColors[i][j + 1]);
                batchRenderer.addNormal(tileNormals[i + tileBuffer][j + 1 + tileBuffer]);
                batchRenderer.addVertex(i + 0.5f, j + 0.5f, tileHeights[i + 1 + tileBuffer][j + 1 + tileBuffer]);
                batchRenderer.addTexCoord(1, 1);
                batchRenderer.addColor(tileColors[i + 1][j + 1]);
                batchRenderer.addNormal(tileNormals[i + 1 + tileBuffer][j + 1 + tileBuffer]);
            }
        }

        this.batchRenderer.stage();

        this.grassRenderer = new GrassRenderer(this);
    }

    public double getHeightAt(double x, double y)
    {
        x += 0.5;
        y += 0.5;

        // TODO
        int ix = (int) Math.floor(x);//Math.max(-tileBuffer, Math.min((int) Math.floor(x), region.sizeX + tileBuffer - 2));
        int iy = (int) Math.floor(y);//Math.max(-tileBuffer, Math.min((int) Math.floor(y), region.sizeY + tileBuffer - 2));

        double fx = x - ix;
        double fy = y - iy;

        if (fx + fy <= 1)
            return (1 - fy) * (tileHeights[ix + tileBuffer][iy + tileBuffer] * (1 - fx) + tileHeights[ix + 1 + tileBuffer][iy + tileBuffer] * fx) + tileHeights[ix + tileBuffer][iy + 1 + tileBuffer] * fy;
        else
            return (fy) * (tileHeights[ix + tileBuffer][iy + 1 + tileBuffer] * (1 - fx) + tileHeights[ix + 1 + tileBuffer][iy + 1 + tileBuffer] * fx) + tileHeights[ix + 1 + tileBuffer][iy + tileBuffer] * (1 - fy);
    }

    public float[] getNormalAt(double x, double y)
    {
        double x1 = this.getHeightAt(x - 0.1, y);
        double x2 = this.getHeightAt(x + 0.1,  y);
        double y1 = this.getHeightAt(x, y - 0.1);
        double y2 = this.getHeightAt(x, y + 0.1);

        double dx = 5 * (x2 - x1);
        double dy = 5 * (y2 - y1);
        double size = Math.sqrt(dx * dx + dy * dy + 1);

        return new float[]{(float) (-dx / size), (float) (-dy / size), (float) (-1 / size)};
    }

    public void free()
    {
        this.batchRenderer.free();
        this.grassRenderer.free();
        this.grassRenderer = null;
    }

    public void draw()
    {
        Game.game.drawing.setColor(255, 255, 255);
        Game.game.window.shaderBase.useNormal.set(true);
        Game.game.window.setMaterialLights(ambient, diffuse, specular, 25, 0, 1, false);
        Game.game.drawing.drawBatch(this.batchRenderer, region.posX, region.posY, 0, 1, 1, 1, true, true);

        if (!Game.game.window.drawingShadow)
            this.grassRenderer.draw();

        Game.game.window.shaderBase.useNormal.set(false);
    }
}
