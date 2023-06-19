package machimania.region;

import basewindow.BaseStaticBatchRenderer;
import basewindow.Color;
import machimania.Drawing;
import machimania.Game;
import machimania.World;

public class GroundRendererLowDetail
{
    public Region region;
    public float[][] tileHeights;
    public float[][][] tileNormals;
    public Color[][] tileColors;

    public static final int[] cutoffs = {100, 200, 300, 400, 500, 600, 1000, 1600};

    public BaseStaticBatchRenderer batchRenderer;
    public int currentScale = Integer.MAX_VALUE;

    public float[] zero = new float[]{0f, 0f, 0f};
    public float[] ambient = new float[]{0.5f, 0.5f, 0.5f};
    public float[] diffuse = new float[]{0.9f, 0.9f, 0.9f};
    public float[] specular = new float[]{0.2f, 0.2f, 0.2f};
    public float[] ambientAdjusted = new float[]{0.5f, 0.5f, 0.5f};

    public float color = 0.7f;

    public int tileBuffer = 2;

    public GroundRendererLowDetail(Region r)
    {
        this.region = r;

        this.tileColors = new Color[r.sizeX + 1][r.sizeY + 1];
        this.tileHeights = new float[r.sizeX + 1 + tileBuffer * 2][r.sizeY + 1 + tileBuffer * 2];
        this.tileNormals = new float[r.sizeX + 1 + tileBuffer * 2][r.sizeY + 1 + tileBuffer * 2][3];
    }

    public void load(int scale)
    {
        if (this.currentScale == scale)
            return;

        World w = Game.game.world;

        Region r = this.region;
        int verts = Math.max(r.tiles.length * r.tiles[0].length / (int) Math.pow(4, scale), 1) * 6;
        BaseStaticBatchRenderer oldBatchRenderer = this.batchRenderer;
        this.batchRenderer = Game.game.window.createStaticBatchRenderer(Game.game.window.shaderBase, true, null, true, verts);

        int s = (int) Math.pow(2, scale);

        Color[][] oldTileColors = this.tileColors;
        float[][] oldTileHeights = this.tileHeights;
        float[][][] oldTileNormals = this.tileNormals;

        this.tileColors = new Color[Math.max(r.sizeX / s, 1) + 1][Math.max(r.sizeY, 1) + 1];
        this.tileHeights = new float[Math.max(r.sizeX / s, 1) + 1 + tileBuffer * 2][Math.max(r.sizeY / s, 1) + 1 + tileBuffer * 2];
        this.tileNormals = new float[Math.max(r.sizeX / s, 1) + 1 + tileBuffer * 2][Math.max(r.sizeY / s, 1) + 1 + tileBuffer * 2][3];

        //if (this.currentScale > scale)
        {
            for (int i = -tileBuffer; i < Math.max(1, r.sizeX / s) + 1 + tileBuffer; i += 1)
            {
                for (int j = -tileBuffer; j < Math.max(1, r.sizeY / s) + 1 + tileBuffer; j += 1)
                {
                    double rx = i * s + r.posX - 0.5;
                    double ry = j * s + r.posY - 0.5;

                    if (i >= 0 && i <= Math.max(1, r.sizeX / s) && j >= 0 && j <= Math.max(1, r.sizeY / s))
                        tileColors[i][j] = new Color(0.6 * (1 * w.noiseMap16.get(rx, ry) + 0.3 * w.noiseMap4.get(rx, ry) + 0.15 * w.noiseMap1.get(rx, ry)) * color, 0.9 * color, 0.2 * color, 1.0);
                    tileHeights[i + tileBuffer][j + tileBuffer] = (float) r.getHeightAtAbsolute(rx, ry);

                    double x1 = r.getHeightAtAbsolute(rx - 0.1, ry);
                    double x2 = r.getHeightAtAbsolute(rx + 0.1, ry);
                    double y1 = r.getHeightAtAbsolute(rx, ry - 0.1);
                    double y2 = r.getHeightAtAbsolute(rx, ry + 0.1);

                    double dx = 5 * (x2 - x1);
                    double dy = 5 * (y2 - y1);
                    double size = Math.sqrt(dx * dx + dy * dy + 1);

                    tileNormals[i + tileBuffer][j + tileBuffer] = new float[]{(float) (-dx / size), (float) (-dy / size), (float) (-1 / size)};
                }
            }
        }

        this.currentScale = scale;

        for (int i = 0; i < Math.max(1, r.sizeX / s); i++)
        {
            for (int j = 0; j < Math.max(1, r.sizeY / s); j++)
            {
                batchRenderer.addVertex(i * s - 0.5f, j * s - 0.5f, tileHeights[i + tileBuffer][j + tileBuffer]);
                batchRenderer.addColor(tileColors[i][j]);
                batchRenderer.addNormal(tileNormals[i + tileBuffer][j + tileBuffer]);
                batchRenderer.addVertex((i + 1) * s - 0.5f, j * s - 0.5f, tileHeights[i + 1 + tileBuffer][j + tileBuffer]);
                batchRenderer.addColor(tileColors[i + 1][j]);
                batchRenderer.addNormal(tileNormals[i + 1 + tileBuffer][j + tileBuffer]);
                batchRenderer.addVertex(i * s - 0.5f, (j + 1) * s - 0.5f, tileHeights[i + tileBuffer][j + 1 + tileBuffer]);
                batchRenderer.addColor(tileColors[i][j + 1]);
                batchRenderer.addNormal(tileNormals[i + tileBuffer][j + 1 + tileBuffer]);

                batchRenderer.addVertex((i + 1) * s - 0.5f, j * s - 0.5f, tileHeights[i + 1 + tileBuffer][j + tileBuffer]);
                batchRenderer.addColor(tileColors[i + 1][j]);
                batchRenderer.addNormal(tileNormals[i + 1 + tileBuffer][j + tileBuffer]);
                batchRenderer.addVertex(i * s - 0.5f, (j + 1) * s - 0.5f, tileHeights[i + tileBuffer][j + 1 + tileBuffer]);
                batchRenderer.addColor(tileColors[i][j + 1]);
                batchRenderer.addNormal(tileNormals[i + tileBuffer][j + 1 + tileBuffer]);
                batchRenderer.addVertex((i + 1) * s - 0.5f, (j + 1) * s - 0.5f, tileHeights[i + 1 + tileBuffer][j + 1 + tileBuffer]);
                batchRenderer.addColor(tileColors[i + 1][j + 1]);
                batchRenderer.addNormal(tileNormals[i + 1 + tileBuffer][j + 1 + tileBuffer]);
            }
        }

        if (oldBatchRenderer != null)
            oldBatchRenderer.free();
        batchRenderer.stage();
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
    }

    public void draw()
    {
        int detail = this.getLoadDetail();
        this.load(detail);

        Game.game.drawing.drawBatch(this.batchRenderer, region.posX, region.posY, 0, 1, 1, 1, true, true);
    }

    public int getLoadDetail()
    {
        double dist = this.region.distToPlayer();

        return getLoadDetail(dist);
    }

    public static int getLoadDetail(double dist)
    {
        int detail = 0;
        for (int cutoff : cutoffs)
        {
            if (cutoff < dist)
                detail++;
        }

        return detail;
    }
}
