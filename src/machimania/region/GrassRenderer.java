package machimania.region;

import basewindow.BaseStaticBatchRenderer;
import machimania.Game;
import machimania.World;

import java.util.ArrayList;

public class GrassRenderer
{
    public GroundRenderer groundRenderer;
    public Region region;
    public ArrayList<Grass> grass = new ArrayList<>();
    public BaseStaticBatchRenderer batchRenderer;

    public GrassRenderer(GroundRenderer gr)
    {
        this.groundRenderer = gr;
        this.region = this.groundRenderer.region;
        World w = Game.game.world;

        for (Tile[] ta: region.tiles)
        {
            for (Tile t: ta)
            {
                if (!t.isSolid)
                {
                    for (int i = 0; i < Math.random() * 5 + 5; i++)
                    {
                        double x = t.posX + Math.random() - 0.5;
                        double y = t.posY + Math.random() - 0.5;
                        double rx = x + this.region.posX;
                        double ry = y + this.region.posY;
                        grass.add(new Grass(x, y, (1 * w.noiseMap16.get(rx, ry) + 0.3 * w.noiseMap4.get(rx, ry) + 0.15 * w.noiseMap1.get(rx, ry))));
                    }
                }
            }
        }

        this.batchRenderer = Game.game.window.createStaticBatchRenderer(Game.game.shaderGrass, true, "/images/environment/grass.png", true, grass.size() * 6);

        for (Grass g: this.grass)
        {
            g.render(this.batchRenderer, this.region, this.groundRenderer);
        }

        this.batchRenderer.stage();
    }

    public void free()
    {
        this.batchRenderer.free();
    }

    public void draw()
    {
        Game.game.window.setShader(Game.game.shaderGrass);
        Game.game.shaderGrass.time.set((float) (System.currentTimeMillis() / 500.0 % (Math.PI * 16)));
        Game.game.shaderGrass.regionPos.set(this.region.posX, this.region.posY);
        Game.game.shaderGrass.playerPos.set((float) Game.game.character.posX, (float) Game.game.character.posY, (float) Game.game.character.posZ);
        Game.game.drawing.drawBatch(this.batchRenderer, region.posX, region.posY, 0, 1, 1, 1, true, true);
        Game.game.window.setShader(Game.game.window.shaderBase);
    }

    public static class Grass
    {
        public double posX;
        public double posY;
        public double width;
        public double height;
        public double angle;
        public double skew;

        public Grass(double posX, double posY, double height)
        {
            this.posX = posX;
            this.posY = posY;
            this.width = Math.random() * 0.5 + 0.5;
            this.height = height + Math.random() * 0.2;
            this.angle = Math.random() * Math.PI * 2;
            this.skew = Math.random() * 0.4 - 0.2;
        }

        public void render(BaseStaticBatchRenderer renderer, Region region, GroundRenderer gr)
        {
            double startX = posX + width * Math.cos(angle);
            double endX = posX - width * Math.cos(angle);

            double startY = posY + width * Math.sin(angle);
            double endY = posY - width * Math.sin(angle);

            double xOff = skew * Math.cos(angle + Math.PI / 2);
            double yOff = skew * Math.sin(angle + Math.PI / 2);

            float r = (float) (this.height * 0.6f);
            float g = 0.9f;
            float b = 0.2f;

            float bm = 0.8f;

            double startHeight = gr.getHeightAt(startX, startY);
            double endHeight = gr.getHeightAt(endX, endY);

            float[] startNorm = gr.getNormalAt(startX, startY);
            float[] endNorm = gr.getNormalAt(endX, endY);

            float[] topStartNorm = averageVecs(startNorm, region.fullNormal);
            float[] topEndNorm = averageVecs(endNorm, region.fullNormal);

            renderer.addVertex((float) startX, (float) startY, (float) (startHeight));
            renderer.addTexCoord(0, 1);
            renderer.addColor(r * bm, g * bm, b * bm, 1);
            renderer.addNormal(startNorm);
            renderer.addVertex((float) (startX + xOff), (float) (startY + yOff), (float) (height + startHeight));
            renderer.addTexCoord(0, 0);
            renderer.addColor(r, g, b, 1);
            renderer.addNormal(topStartNorm);
            renderer.addVertex((float) endX, (float) endY, (float) (endHeight));
            renderer.addTexCoord(1, 1);
            renderer.addColor(r * bm, g * bm, b * bm, 1);
            renderer.addNormal(endNorm);

            renderer.addVertex((float) (startX + xOff), (float) (startY + yOff), (float) (height + startHeight));
            renderer.addTexCoord(0, 0);
            renderer.addColor(r, g, b, 1);
            renderer.addNormal(topStartNorm);
            renderer.addVertex((float) endX, (float) endY, (float) (endHeight));
            renderer.addTexCoord(1, 1);
            renderer.addColor(r * bm, g * bm, b * bm, 1);
            renderer.addNormal(endNorm);
            renderer.addVertex((float) (endX + xOff), (float) (endY + yOff), (float) (height + endHeight));
            renderer.addTexCoord(1, 0);
            renderer.addColor(r, g, b, 1);
            renderer.addNormal(topEndNorm);
        }

        public float[] averageVecs(float[] n1, float[] n2)
        {
            float[] n3 = new float[3];
            n3[0] = n1[0] + n2[0];
            n3[1] = n1[1] + n2[1];
            n3[2] = n1[2] + n2[2];

            float size = n3[0] * n3[0] + n3[1] * n3[1] + n3[2] * n3[2];
            n3[0] /= size;
            n3[1] /= size;
            n3[2] /= size;
            return n3;
        }
    }
}
