package machimania;

import basewindow.BaseStaticBatchRenderer;

public class SkyRenderer
{
    public BaseStaticBatchRenderer batchRenderer;

    public SkyRenderer()
    {
        this.batchRenderer = Game.game.window.createStaticBatchRenderer(Game.game.window.shaderBase, false, "/images/environment/sky.png", false, 36);

        this.batchRenderer.addVertex(-200.0f, -200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0, 0);
        this.batchRenderer.addVertex(-200.0f, 200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0, 0.5f);
        this.batchRenderer.addVertex(200.0f, -200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.25f, 0);

        this.batchRenderer.addVertex(-200.0f, 200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0, 0.5f);
        this.batchRenderer.addVertex(200.0f, -200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.25f, 0);
        this.batchRenderer.addVertex(200.0f, 200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.25f, 0.5f);


        this.batchRenderer.addVertex(-200.0f, -200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.25f, 1);
        this.batchRenderer.addVertex(-200.0f, 200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0, 1);
        this.batchRenderer.addVertex(-200.0f, -200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.25f, 0.5f);

        this.batchRenderer.addVertex(-200.0f, 200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0, 1);
        this.batchRenderer.addVertex(-200.0f, -200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.25f, 0.5f);
        this.batchRenderer.addVertex(-200.0f, 200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0, 0.5f);


        this.batchRenderer.addVertex(-200.0f, -200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.5f, 1);
        this.batchRenderer.addVertex(200.0f, -200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.25f, 1);
        this.batchRenderer.addVertex(-200.0f, -200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.5f, 0.5f);

        this.batchRenderer.addVertex(200.0f, -200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.25f, 1);
        this.batchRenderer.addVertex(-200.0f, -200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.5f, 0.5f);
        this.batchRenderer.addVertex(200.0f, -200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.25f, 0.5f);


        this.batchRenderer.addVertex(200.0f, -200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.75f, 1);
        this.batchRenderer.addVertex(200.0f, 200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.5f, 1);
        this.batchRenderer.addVertex(200.0f, -200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.75f, 0.5f);

        this.batchRenderer.addVertex(200.0f, 200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.5f, 1);
        this.batchRenderer.addVertex(200.0f, -200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.75f, 0.5f);
        this.batchRenderer.addVertex(200.0f, 200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.5f, 0.5f);


        this.batchRenderer.addVertex(-200.0f, 200.0f, -200.0f);
        this.batchRenderer.addTexCoord(1, 1);
        this.batchRenderer.addVertex(200.0f, 200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.75f, 1);
        this.batchRenderer.addVertex(-200.0f, 200.0f, 200.0f);
        this.batchRenderer.addTexCoord(1, 0.5f);

        this.batchRenderer.addVertex(200.0f, 200.0f, -200.0f);
        this.batchRenderer.addTexCoord(0.75f, 1);
        this.batchRenderer.addVertex(-200.0f, 200.0f, 200.0f);
        this.batchRenderer.addTexCoord(1, 0.5f);
        this.batchRenderer.addVertex(200.0f, 200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.75f, 0.5f);


        this.batchRenderer.addVertex(-200.0f, -200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.25f, 0);
        this.batchRenderer.addVertex(-200.0f, 200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.25f, 0.5f);
        this.batchRenderer.addVertex(200.0f, -200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.5f, 0);

        this.batchRenderer.addVertex(-200.0f, 200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.25f, 0.5f);
        this.batchRenderer.addVertex(200.0f, -200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.5f, 0);
        this.batchRenderer.addVertex(200.0f, 200.0f, 200.0f);
        this.batchRenderer.addTexCoord(0.5f, 0.5f);

        this.batchRenderer.stage();
    }

    public void draw()
    {
        Game.game.drawing.setColor(255, 255, 255);

        if (!Game.game.window.drawingShadow)
            Game.game.drawing.drawBatch(this.batchRenderer, Game.game.character.posX, Game.game.character.posY, 0, 1, 1, 1, false, false);
    }
}
