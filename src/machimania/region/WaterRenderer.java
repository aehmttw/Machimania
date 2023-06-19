package machimania.region;

import basewindow.BaseStaticBatchRenderer;
import machimania.Game;

public class WaterRenderer
{
    public BaseStaticBatchRenderer batchRenderer;
    public Region region;

    public float[] ambient = new float[]{0.5f, 0.5f, 0.5f};
    public float[] diffuse = new float[]{0.5f, 0.5f, 0.5f};
    public float[] specular = new float[]{0.5f, 0.5f, 0.5f};

    public WaterRenderer(Region r)
    {
        int tiles = 0;
        for (Tile[] ta: r.tiles)
        {
            for (Tile t: ta)
            {
                if (t.hasWater)
                    tiles++;
            }
        }

        this.region = r;

        this.batchRenderer = Game.game.window.createStaticBatchRenderer(Game.game.shaderWater, false, null, false, tiles * 12);

        for (Tile[] ta: r.tiles)
        {
            for (Tile t: ta)
            {
                this.addVertices((float) t.posX, (float) t.posY, (float) (t.posX - 0.5), (float) (t.posY - 0.5), (float) (t.posX - 0.5), (float) (t.posY + 0.5), (float) t.waterLevel);
                this.addVertices((float) t.posX, (float) t.posY, (float) (t.posX - 0.5), (float) (t.posY + 0.5), (float) (t.posX + 0.5), (float) (t.posY + 0.5), (float) t.waterLevel);
                this.addVertices((float) t.posX, (float) t.posY, (float) (t.posX + 0.5), (float) (t.posY + 0.5), (float) (t.posX + 0.5), (float) (t.posY - 0.5), (float) t.waterLevel);
                this.addVertices((float) t.posX, (float) t.posY, (float) (t.posX + 0.5), (float) (t.posY - 0.5), (float) (t.posX - 0.5), (float) (t.posY - 0.5), (float) t.waterLevel);
            }
        }

        this.batchRenderer.stage();
    }

    public void addVertices(float x1, float y1, float x2, float y2, float x3, float y3, float z)
    {
        this.batchRenderer.addVertex(x1, y1, z);
        this.batchRenderer.addAttributeF(Game.game.shaderWater.otherPos, x2, y2, x3, y3);
        this.batchRenderer.addAttributeF(Game.game.shaderWater.waveSize, (float) Math.max(0, Math.min(this.region.tiles[(int)x1][(int)x2].waterLevel - this.region.getHeightAtRelative(x1, y1), 10)) / 10);
        this.batchRenderer.addVertex(x2, y2, z);
        this.batchRenderer.addAttributeF(Game.game.shaderWater.otherPos, x3, y3, x1, y1);
        this.batchRenderer.addAttributeF(Game.game.shaderWater.waveSize, (float) Math.max(0, Math.min(this.region.tiles[(int)x1][(int)x2].waterLevel - this.region.getHeightAtRelative(x2, y2), 10)) / 10);
        this.batchRenderer.addVertex(x3, y3, z);
        this.batchRenderer.addAttributeF(Game.game.shaderWater.otherPos, x1, y1, x2, y2);
        this.batchRenderer.addAttributeF(Game.game.shaderWater.waveSize, (float) Math.max(0, Math.min(this.region.tiles[(int)x1][(int)x2].waterLevel - this.region.getHeightAtRelative(x3, y3), 10)) / 10);
    }

    public void draw()
    {
        Game.game.window.setMaterialLights(ambient, diffuse, specular, 5, 0, 1, false);

        Game.game.drawing.setColor(80, 150, 200, 127);
        Game.game.window.setShader(Game.game.shaderWater);
        Game.game.shaderWater.useNormal.set(true);
        Game.game.shaderWater.edgeLight.set(1);
        Game.game.shaderWater.edgeCutoff.set(0.75f);
        Game.game.shaderWater.time.set((float) (System.currentTimeMillis() / 500.0 % (Math.PI * 16)));
        Game.game.shaderWater.regionPos.set(this.region.posX, this.region.posY);
        Game.game.drawing.drawBatch(this.batchRenderer, region.posX, region.posY, 0, 1, 1, 1, true, false);
        Game.game.shaderWater.edgeLight.set(0);
        Game.game.shaderWater.edgeCutoff.set(0.0f);
        Game.game.window.setShader(Game.game.window.shaderBase);
    }

    public void free()
    {
        this.batchRenderer.free();
        this.batchRenderer = null;
    }
}
