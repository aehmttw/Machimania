package machimania;

import basewindow.BaseStaticBatchRenderer;
import basewindow.IModel;
import basewindow.Model;
import basewindow.ModelPart;

import java.util.ArrayList;

public class Drawing
{
    public double interfaceWidth = 1400;
    public double interfaceHeight = 900;
    public double interfaceDepth = 1000;

    public double gameWidth = 1400;
    public double gameHeight = 900;
    public double gameDepth = 1000;

    public double fullInterfaceWidth = 1400;
    public double fullInterfaceHeight = 900;

    public double fullGameWidth = 1400;
    public double fullGameHeight = 900;

    public double currentColorR;
    public double currentColorG;
    public double currentColorB;
    public double currentColorA;
    public double currentGlow;

    public double fontSize;

    public double gamePosX = 0;
    public double gamePosY = 0;
    public double gamePosZ;

    public double zoom = 200;

    public double gameScale;
    public double interfaceScale;

    public double interfaceBoundLeft;
    public double interfaceBoundRight;
    public double interfaceBoundTop;
    public double interfaceBoundBottom;

    public double gameMarginX;
    public double gameMarginY;

    public double interfacegameScaleZoom = 1;

    public void updateDimensions()
    {
        double windowWidth = Game.game.window.absoluteWidth;
        double windowHeight = Game.game.window.absoluteHeight;

        this.interfaceScale = this.interfacegameScaleZoom * Math.min(windowWidth / this.interfaceWidth, windowHeight / this.interfaceHeight);
        this.gameScale = this.interfaceScale * zoom;

        this.gameWidth = this.interfaceWidth / this.gameScale * this.interfaceScale;
        this.gameHeight = this.interfaceHeight / this.gameScale * this.interfaceScale;
        this.gameDepth = this.interfaceDepth / this.gameScale * this.interfaceScale;

        this.fullInterfaceWidth = windowWidth / this.interfaceScale;
        this.fullInterfaceHeight = windowHeight / this.interfaceScale;

        this.fullGameWidth = windowWidth / this.gameScale;
        this.fullGameHeight = windowHeight / this.gameScale;

        double xMargin = (windowWidth / this.interfaceScale - this.interfaceWidth) / 2;
        this.interfaceBoundLeft = -xMargin;
        this.interfaceBoundRight = this.interfaceWidth + xMargin;

        double yMargin = (windowHeight / this.interfaceScale - this.interfaceHeight) / 2;
        this.interfaceBoundTop = -yMargin;
        this.interfaceBoundBottom = this.interfaceHeight + yMargin;

        this.gameMarginX = (windowWidth / this.gameScale - this.gameWidth) / 2;
        this.gameMarginY = (windowHeight / this.gameScale - this.gameHeight) / 2;

        Game.game.window.absoluteDepth = this.interfaceScale * this.interfaceDepth;
    }

    public void setColor(double r, double g, double b)
    {
        Game.game.window.setColor(r, g, b);
        this.currentColorR = r;
        this.currentColorG = g;
        this.currentColorB = b;
        this.currentColorA = 255;
        this.currentGlow = 0;
    }

    public void setColor(double r, double g, double b, double a)
    {
        Game.game.window.setColor(r, g, b, a);
        this.currentColorR = r;
        this.currentColorG = g;
        this.currentColorB = b;
        this.currentColorA = a;
        this.currentGlow = 0;
    }

    public void setColor(double r, double g, double b, double a, double glow)
    {
        Game.game.window.setColor(r, g, b, a, glow);
        this.currentColorR = r;
        this.currentColorG = g;
        this.currentColorB = b;
        this.currentColorA = a;
        this.currentGlow = glow;
    }

    public void fillOval(double x, double y, double sizeX, double sizeY)
    {
        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        Game.game.window.shapeRenderer.fillOval(drawX, drawY, drawSizeX, drawSizeY);
    }

    public void fillGlow(double x, double y, double sizeX, double sizeY)
    {
        this.fillGlow(x, y, sizeX, sizeY, false);
    }

    public void fillGlow(double x, double y, double sizeX, double sizeY, boolean shade)
    {
        this.fillGlow(x, y, sizeX, sizeY, shade, false);
    }

    public void fillGlow(double x, double y, double sizeX, double sizeY, boolean shade, boolean light)
    {
        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        Game.game.window.shapeRenderer.fillGlow(drawX, drawY, drawSizeX, drawSizeY, shade, light);
    }

    public void fillOval(double x, double y, double z, double sizeX, double sizeY)
    {
        this.fillOval(x, y, z, sizeX, sizeY, true, true);
    }

    public void fillOval(double x, double y, double z, double sizeX, double sizeY, boolean depthTest, boolean facing)
    {
        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        double dZ = z * gameScale;

        if (Game.game.window.angled && facing)
            Game.game.window.shapeRenderer.fillFacingOval(drawX, drawY, dZ, drawSizeX, drawSizeY, depthTest);
        else
            Game.game.window.shapeRenderer.fillOval(drawX, drawY, dZ, drawSizeX, drawSizeY, depthTest);
    }

    public void fillForcedOval(double x, double y, double z, double sizeX, double sizeY, boolean depthTest, boolean facing)
    {
        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        double dZ = z * gameScale;

        if (Game.game.window.angled && facing)
            Game.game.window.shapeRenderer.fillFacingOval(drawX, drawY, dZ, drawSizeX, drawSizeY, depthTest);
        else
            Game.game.window.shapeRenderer.fillOval(drawX, drawY, dZ, drawSizeX, drawSizeY, depthTest);
    }

    public void fillGlow(double x, double y, double z, double sizeX, double sizeY)
    {
        this.fillGlow(x, y, z, sizeX, sizeY, true, true, false);
    }

    public void fillGlow(double x, double y, double z, double sizeX, double sizeY, boolean shade)
    {
        this.fillGlow(x, y, z, sizeX, sizeY, true, true, shade);
    }

    public void fillGlow(double x, double y, double z, double sizeX, double sizeY, boolean depthTest, boolean facing)
    {
        this.fillGlow(x, y, z, sizeX, sizeY, depthTest, facing, false);
    }

    public void fillGlow(double x, double y, double z, double sizeX, double sizeY, boolean depthTest, boolean facing, boolean shade)
    {
        this.fillGlow(x, y, z, sizeX, sizeY, depthTest, facing, shade, false);
    }

    public void fillGlow(double x, double y, double z, double sizeX, double sizeY, boolean depthTest, boolean facing, boolean shade, boolean light)
    {
        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        double dZ = z * gameScale;

        if (Game.game.window.angled && facing)
            Game.game.window.shapeRenderer.fillFacingGlow(drawX, drawY, dZ, drawSizeX, drawSizeY, depthTest, shade, light);
        else
            Game.game.window.shapeRenderer.fillGlow(drawX, drawY, dZ, drawSizeX, drawSizeY, depthTest, shade, light);
    }

    public void fillForcedGlow(double x, double y, double z, double sizeX, double sizeY, boolean depthTest, boolean facing, boolean shade, boolean light)
    {
        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        double dZ = z * gameScale;

        if (Game.game.window.angled && facing)
            Game.game.window.shapeRenderer.fillFacingGlow(drawX, drawY, dZ, drawSizeX, drawSizeY, depthTest, shade, light);
        else
            Game.game.window.shapeRenderer.fillGlow(drawX, drawY, dZ, drawSizeX, drawSizeY, depthTest, shade, light);
    }

    public void fillForcedOval(double x, double y, double sizeX, double sizeY)
    {
        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        Game.game.window.shapeRenderer.fillOval(drawX, drawY, drawSizeX, drawSizeY);
    }

    public void fillBox(double x, double y, double z, double sizeX, double sizeY, double sizeZ)
    {
        fillBox(x, y, z, sizeX, sizeY, sizeZ, (byte) 0);
    }

    /**
     * Options byte:
     * <p>
     * 0: default
     * <p>
     * +1 hide behind face
     * +2 hide front face
     * +4 hide bottom face
     * +8 hide top face
     * +16 hide left face
     * +32 hide right face
     * <p>
     * +64 draw on top
     */
    public void fillBox(double x, double y, double z, double sizeX, double sizeY, double sizeZ, byte options)
    {
        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);
        double drawZ = z * gameScale;

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = sizeX * gameScale;
        double drawSizeY = sizeY * gameScale;
        double drawSizeZ = sizeZ * gameScale;

        Game.game.window.shapeRenderer.fillBox(drawX, drawY, drawZ, drawSizeX, drawSizeY, drawSizeZ, options);
    }

    public void highlightTile(double x, double y, double z, double sizeX, double sizeY, double sizeZ)
    {
        this.highlightTile(x, y, z, sizeX, sizeY, sizeZ, true);
    }

    public void highlightTile(double x, double y, double z, double sizeX, double sizeY, double sizeZ, boolean center)
    {
        Game.game.window.shapeRenderer.setBatchMode(true, true, false, true, false);

        if (center)
        {
            Game.game.window.setColor(currentColorR, currentColorG, currentColorB);
            addVertex(x - sizeX / 2, y - sizeY / 2, z);
            addVertex(x + sizeX / 2, y - sizeY / 2, z);
            addVertex(x + sizeX / 2, y + sizeY / 2, z);
            addVertex(x - sizeX / 2, y + sizeY / 2, z);
        }

        double mul = 0.75;

        Game.game.window.setColor(currentColorR * mul, currentColorG * mul, currentColorB * mul);
        addVertex(x - sizeX / 2, y - sizeY / 2, z);
        addVertex(x + sizeX / 2, y - sizeY / 2, z);
        Game.game.window.setColor(0, 0, 0);
        addVertex(x + sizeX / 2, y - sizeY / 2, z + sizeZ);
        addVertex(x - sizeX / 2, y - sizeY / 2, z + sizeZ);

        Game.game.window.setColor(currentColorR * mul, currentColorG * mul, currentColorB * mul);
        addVertex(x + sizeX / 2, y - sizeY / 2, z);
        addVertex(x + sizeX / 2, y + sizeY / 2, z);
        Game.game.window.setColor(0, 0, 0);
        addVertex(x + sizeX / 2, y + sizeY / 2, z + sizeZ);
        addVertex(x + sizeX / 2, y - sizeY / 2, z + sizeZ);

        Game.game.window.setColor(currentColorR * mul, currentColorG * mul, currentColorB * mul);
        addVertex(x - sizeX / 2, y + sizeY / 2, z);
        addVertex(x + sizeX / 2, y + sizeY / 2, z);
        Game.game.window.setColor(0, 0, 0);
        addVertex(x + sizeX / 2, y + sizeY / 2, z + sizeZ);
        addVertex(x - sizeX / 2, y + sizeY / 2, z + sizeZ);

        Game.game.window.setColor(currentColorR * mul, currentColorG * mul, currentColorB * mul);
        addVertex(x - sizeX / 2, y - sizeY / 2, z);
        addVertex(x - sizeX / 2, y + sizeY / 2, z);
        Game.game.window.setColor(0, 0, 0);
        addVertex(x - sizeX / 2, y + sizeY / 2, z + sizeZ);
        addVertex(x - sizeX / 2, y - sizeY / 2, z + sizeZ);

        Game.game.window.setColor(currentColorR, currentColorG, currentColorB);

        Game.game.window.shapeRenderer.setBatchMode(false, true, true, true);
    }

    public void fillRect(double x, double y, double z, double sizeX, double sizeY)
    {
        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        Game.game.window.shapeRenderer.fillRect(drawX, drawY, drawSizeX, drawSizeY);
    }

    public void drawImage(String img, double x, double y, double sizeX, double sizeY)
    {
        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        Game.game.window.shapeRenderer.drawImage(drawX, drawY, drawSizeX, drawSizeY, "/images/" + img, false);
        Game.game.window.shapeRenderer.drawImage(drawX, drawY, drawSizeX, drawSizeY, "/images/" + img, false);
    }

    public void drawImage(double rotation, String img, double x, double y, double sizeX, double sizeY)
    {
        double drawX = gameToAbsoluteX(x, 0);
        double drawY = gameToAbsoluteY(y, 0);

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        Game.game.window.shapeRenderer.drawImage(drawX, drawY, drawSizeX, drawSizeY, "/images/" + img, rotation, false);
    }

    public void drawImage(String img, double x, double y, double z, double sizeX, double sizeY)
    {
        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        double drawZ = z * gameScale;

        Game.game.window.shapeRenderer.drawImage(drawX, drawY, drawZ, drawSizeX, drawSizeY, "/images/" + img, false);
    }

    public void drawImage(double rotation, String img, double x, double y, double z, double sizeX, double sizeY)
    {
        double drawX = gameToAbsoluteX(x, 0);
        double drawY = gameToAbsoluteY(y, 0);

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = (sizeX * gameScale);
        double drawSizeY = (sizeY * gameScale);

        double drawZ = z * gameScale;

        Game.game.window.shapeRenderer.drawImage(drawX, drawY, drawZ, drawSizeX, drawSizeY, "/images/" + img, rotation, false);
    }

    public void drawModel(IModel m, double x, double y, double width, double height, double angle)
    {
        double drawX = gameToAbsoluteX(x, 0);
        double drawY = gameToAbsoluteY(y, 0);

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = width * gameScale;
        double drawSizeY = height * gameScale;

        m.draw(drawX, drawY, drawSizeX, drawSizeY, angle);
    }

    public void drawModel(IModel m, double x, double y, double z, double width, double height, double depth, double angle)
    {
        double drawX = gameToAbsoluteX(x, 0);
        double drawY = gameToAbsoluteY(y, 0);
        double drawZ = z * gameScale;

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = width * gameScale;
        double drawSizeY = height * gameScale;
        double drawSizeZ = depth * gameScale;

        m.draw(drawX, drawY, drawZ, drawSizeX, drawSizeY, drawSizeZ, angle, 0, 0, true);
    }

    public void drawInterfaceModel(IModel m, double x, double y, double width, double height, double angle)
    {
        double drawX = (interfaceScale * x + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * y + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (width * interfaceScale);
        double drawSizeY = (height * interfaceScale);

        m.draw(drawX, drawY, drawSizeX, drawSizeY, angle);
    }

    public void drawModel(IModel m, double x, double y, double z, double width, double height, double depth, double yaw, double pitch, double roll)
    {
        double drawX = gameToAbsoluteX(x, 0);
        double drawY = gameToAbsoluteY(y, 0);
        double drawZ = z * gameScale;

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = width * gameScale;
        double drawSizeY = height * gameScale;
        double drawSizeZ = depth * gameScale;

        m.draw(drawX, drawY, drawZ, drawSizeX, drawSizeY, drawSizeZ, yaw, pitch, roll, true);
    }

    public void drawBatch(BaseStaticBatchRenderer b, double x, double y, double z, double width, double height, double depth, double yaw, double pitch, double roll, boolean depthTest, boolean depthWrite)
    {
        double drawX = gameToAbsoluteX(x, 0);
        double drawY = gameToAbsoluteY(y, 0);
        double drawZ = z * gameScale;

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = width * gameScale;
        double drawSizeY = height * gameScale;
        double drawSizeZ = depth * gameScale;

        b.draw(drawX, drawY, drawZ, drawSizeX, drawSizeY, drawSizeZ, yaw, pitch, roll, depthTest, depthWrite);
    }

    public void drawBatch(BaseStaticBatchRenderer b, double x, double y, double z, double width, double height, double depth, boolean depthTest, boolean depthWrite)
    {
        double drawX = gameToAbsoluteX(x, 0);
        double drawY = gameToAbsoluteY(y, 0);
        double drawZ = z * gameScale;

        if (isOutOfBounds(drawX, drawY))
            return;

        double drawSizeX = width * gameScale;
        double drawSizeY = height * gameScale;
        double drawSizeZ = depth * gameScale;

        b.draw(drawX, drawY, drawZ, drawSizeX, drawSizeY, drawSizeZ, 0, 0, 0, depthTest, depthWrite);
    }
    
    public void fillInterfaceOval(double x, double y, double sizeX, double sizeY)
    {
        double drawX = (interfaceScale * (x - sizeX / 2) + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * (y - sizeY / 2) + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (sizeX * interfaceScale);
        double drawSizeY = (sizeY * interfaceScale);

        Game.game.window.shapeRenderer.fillOval(drawX, drawY, drawSizeX, drawSizeY);
    }

    public void fillInterfacePartialOval(double x, double y, double sizeX, double sizeY, double start, double end)
    {
        double drawX = (interfaceScale * (x - sizeX / 2) + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * (y - sizeY / 2) + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (sizeX * interfaceScale);
        double drawSizeY = (sizeY * interfaceScale);

        Game.game.window.shapeRenderer.fillPartialOval(drawX, drawY, drawSizeX, drawSizeY, start, end);
    }

    public void fillInterfaceOval(double x, double y, double z, double sizeX, double sizeY)
    {
        double drawX = (interfaceScale * (x - sizeX / 2) + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * (y - sizeY / 2) + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (sizeX * interfaceScale);
        double drawSizeY = (sizeY * interfaceScale);
        double drawZ = z * interfaceScale;

        Game.game.window.shapeRenderer.fillOval(drawX, drawY, drawZ, drawSizeX, drawSizeY, false);
    }


    public void fillInterfaceGlow(double x, double y, double sizeX, double sizeY)
    {
        double drawX = (interfaceScale * (x - sizeX / 2) + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * (y - sizeY / 2) + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (sizeX * interfaceScale);
        double drawSizeY = (sizeY * interfaceScale);

        Game.game.window.shapeRenderer.fillGlow(drawX, drawY, drawSizeX, drawSizeY);
    }

    public void fillInterfaceGlow(double x, double y, double z, double sizeX, double sizeY)
    {
        double drawX = (interfaceScale * (x - sizeX / 2) + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * (y - sizeY / 2) + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (sizeX * interfaceScale);
        double drawSizeY = (sizeY * interfaceScale);
        double drawZ = interfaceScale * z;

        Game.game.window.shapeRenderer.fillGlow(drawX, drawY, drawZ, drawSizeX, drawSizeY, false);
    }

    public void fillInterfaceGlow(double x, double y, double sizeX, double sizeY, boolean shade)
    {
        double drawX = (interfaceScale * (x - sizeX / 2) + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * (y - sizeY / 2) + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (sizeX * interfaceScale);
        double drawSizeY = (sizeY * interfaceScale);

        Game.game.window.shapeRenderer.fillGlow(drawX, drawY, drawSizeX, drawSizeY, shade);
    }

    public void fillInterfaceRect(double x, double y, double sizeX, double sizeY)
    {
        double drawX = (interfaceScale * (x - sizeX / 2) + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * (y - sizeY / 2) + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (sizeX * interfaceScale);
        double drawSizeY = (sizeY * interfaceScale);

        Game.game.window.shapeRenderer.fillRect(drawX, drawY, drawSizeX, drawSizeY);
    }

    public void outlineInterfaceRect(double x, double y, double sizeX, double sizeY, double thickness)
    {
        double drawX = (interfaceScale * (x - sizeX / 2) + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * (y - sizeY / 2) + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (sizeX * interfaceScale);
        double drawSizeY = (sizeY * interfaceScale);
        double drawThickness = thickness * interfaceScale;

        Game.game.window.shapeRenderer.fillRect(drawX, drawY, drawSizeX, drawThickness);
        Game.game.window.shapeRenderer.fillRect(drawX, drawY + drawThickness, drawThickness, drawSizeY - drawThickness * 2);
        Game.game.window.shapeRenderer.fillRect(drawX, drawY + drawSizeY - drawThickness, drawSizeX, drawThickness);
        Game.game.window.shapeRenderer.fillRect(drawX + drawSizeX - drawThickness, drawY + drawThickness, drawThickness, drawSizeY - drawThickness * 2);
    }


    public void fillInterfaceProgressRect(double x, double y, double sizeX, double sizeY, double progress)
    {
        double drawX = (interfaceScale * (x - sizeX / 2) + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * (y - sizeY / 2) + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (sizeX * interfaceScale * progress);
        double drawSizeY = (sizeY * interfaceScale);

        Game.game.window.shapeRenderer.fillRect(drawX, drawY, drawSizeX, drawSizeY);
    }

    public void drawInterfaceImage(String img, double x, double y, double sizeX, double sizeY)
    {
        double drawX = (interfaceScale * (x - sizeX / 2) + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * (y - sizeY / 2) + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (sizeX * interfaceScale);
        double drawSizeY = (sizeY * interfaceScale);

        Game.game.window.shapeRenderer.drawImage(drawX, drawY, drawSizeX, drawSizeY, "/images/" + img, false);
    }

    public void drawInterfaceGradientImage(String img, double x, double y, double sizeX, double sizeY, double a1, double a2, double a3, double a4)
    {
        double drawX = (interfaceScale * (x - sizeX / 2) + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * (y - sizeY / 2) + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawSizeX = (sizeX * interfaceScale);
        double drawSizeY = (sizeY * interfaceScale);

        Game.game.window.shapeRenderer.drawGradientImage(drawX, drawY, drawSizeX, drawSizeY, 0, 0, 1, 1, a1, a2, a3, a4, "/images/" + img, false);
    }

    public void drawText(double x, double y, String text)
    {
        double sizeX = Game.game.window.fontRenderer.getStringSizeX(this.fontSize, text) / gameScale;
        double sizeY = Game.game.window.fontRenderer.getStringSizeY(this.fontSize, text) / gameScale;

        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);

        Game.game.window.fontRenderer.drawString(drawX, drawY, this.fontSize, this.fontSize, text);
    }

    public void drawText(double x, double y, double z, String text)
    {
        double sizeX = Game.game.window.fontRenderer.getStringSizeX(this.fontSize, text) / gameScale;
        double sizeY = Game.game.window.fontRenderer.getStringSizeY(this.fontSize, text) / gameScale;

        double drawX = gameToAbsoluteX(x, sizeX);
        double drawY = gameToAbsoluteY(y, sizeY);
        double drawZ = z * gameScale;

        Game.game.window.fontRenderer.drawString(drawX, drawY, drawZ, this.fontSize, this.fontSize, text);
    }

    public void drawInterfaceText(double x, double y, String text)
    {
        double sizeX = Game.game.window.fontRenderer.getStringSizeX(this.fontSize, text);
        double sizeY = Game.game.window.fontRenderer.getStringSizeY(this.fontSize, text);

        double drawX = (interfaceScale * x - sizeX / 2 + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * y - sizeY / 2 + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);

        Game.game.window.fontRenderer.drawString(drawX, drawY, this.fontSize, this.fontSize, text);
    }

    public void drawXPIcon(double x, double y, double isize)
    {
        this.drawXPIcon(x, y, isize, false);
    }

    public void drawXPIcon(double x, double y, double isize, boolean glow)
    {
        double size = isize / 2;

        Game.game.window.shapeRenderer.setBatchMode(true, false, false, glow);

        double c = 20;
        for (int i = 0; i < c; i++)
        {
            addInterfaceVertex(x, y, 0);

            double ox = Math.cos(i / c * Math.PI * 2);
            double oy = Math.sin(i / c * Math.PI * 2);
            double ox2 = Math.cos((i + 1) / c * Math.PI * 2);
            double oy2 = Math.sin((i + 1) / c * Math.PI * 2);

            double rad = size;
            double rad2 = size * 0.8;

            if (i % 2 == 0)
            {
                rad2 = rad;
                rad *= 0.8;
            }

            addInterfaceVertex(x + ox * rad, y + oy * rad, 0);
            addInterfaceVertex(x + ox2 * rad2, y + oy2 * rad2, 0);
        }
        Game.game.window.shapeRenderer.setBatchMode(false, false, false, glow);
    }

    public void drawInterfaceText(double x, double y, String text, boolean rightAligned)
    {
        double sizeX = Game.game.window.fontRenderer.getStringSizeX(this.fontSize, text);
        double sizeY = Game.game.window.fontRenderer.getStringSizeY(this.fontSize, text);

        double offX = sizeX;

        if (!rightAligned)
            offX = 0;

        double drawX = (interfaceScale * x - offX + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * y - sizeY / 2 + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        Game.game.window.fontRenderer.drawString(drawX, drawY, this.fontSize, this.fontSize, text);
    }

    public void drawUncenteredInterfaceText(double x, double y, String text)
    {
        double drawX = (interfaceScale * x + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * y + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        Game.game.window.fontRenderer.drawString(drawX, drawY, this.fontSize, this.fontSize, text);
    }

    public void addVertex(double x, double y, double z)
    {
        double drawX = gameToAbsoluteX(x, 0);
        double drawY = gameToAbsoluteY(y, 0);
        double drawZ = z * gameScale;

        Game.game.window.addVertex(drawX, drawY, drawZ);
    }

    public void addInterfaceVertex(double x, double y, double z)
    {
        double drawX = (interfaceScale * x + Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2);
        double drawY = (interfaceScale * y + Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2);
        double drawZ = z * gameScale;

        Game.game.window.addVertex(drawX, drawY, drawZ);
    }

    public void setLighting(double light, double shadow)
    {
        Game.game.window.setLighting(light, Math.max(1, light), shadow, Math.max(1, shadow));
    }

    public void setGameFontSize(double size)
    {
        this.fontSize = size / 36.0 * gameScale;
    }

    public void setInterfaceFontSize(double size)
    {
        this.fontSize = size / 36.0 * interfaceScale;
    }

    public void drawTooltip(ArrayList<String> text)
    {
        double x = getInterfaceMouseX();
        double y = getInterfaceMouseY();

        int xPadding = 16;
        int yPadding = 8;

        setInterfaceFontSize(14);

        int sizeX = 0;
        for (String s : text)
        {
            sizeX = Math.max(sizeX, (int) Math.round(Game.game.window.fontRenderer.getStringSizeX(fontSize, s) / this.interfaceScale) + xPadding);
        }

        double sizeY = 14;

        if (x + sizeX + xPadding * 2 - 14 > this.interfaceWidth)
            x -= sizeX + xPadding * 2 - 14;

        if (y + sizeY + yPadding * 2 * text.size() > this.interfaceHeight)
            y -= sizeY + yPadding * 2 * text.size();

        double drawX = x + sizeX / 2.0 + xPadding;
        double drawY = y + sizeY / 2.0 + yPadding * text.size();

        setColor(0, 0, 0, 127);
        fillInterfaceRect(drawX - 7, drawY, sizeX + xPadding * 2 - 14, sizeY + yPadding * 2 * text.size());
        fillInterfaceRect(drawX - 7, drawY, sizeX + xPadding * 2 - 14 - 10, sizeY + yPadding * 2 * text.size() - 10);

        setColor(255, 255, 255);
        for (int i = 0; i < text.size(); i++)
        {
            drawUncenteredInterfaceText(x + xPadding, y + 2 + yPadding * (2 * i + 1), text.get(i));
        }
    }

    public double interfaceToGameCoordsX(double x)
    {
        return x * interfaceScale / gameScale + gamePosX - fullGameWidth / 2 + gameMarginX;
    }

    public double interfaceToGameCoordsY(double y)
    {
        return y * interfaceScale / gameScale + gamePosY - fullGameHeight / 2 + gameMarginY;
    }

    public double gameToInterfaceCoordsX(double x)
    {
        return (x - gameMarginX + fullGameWidth / 2 - gamePosX) * gameScale / interfaceScale;
    }

    public double gameToInterfaceCoordsY(double y)
    {
        return (y - gameMarginY + fullGameHeight / 2 - gamePosY) * gameScale / interfaceScale;
    }

    public double getGameMouseX()
    {
        return interfaceToGameCoordsX(getInterfaceMouseX());
    }

    public double getGameMouseY()
    {
        return interfaceToGameCoordsY(getInterfaceMouseY());
    }

    public double getInterfaceMouseX()
    {
        return (Game.game.window.absoluteMouseX - Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2) / interfaceScale;
    }

    public double getInterfaceMouseY()
    {
        return (Game.game.window.absoluteMouseY - Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2) / interfaceScale;
    }

    public double getInterfacePointerX(double x)
    {
        return (x - Math.max(0, Game.game.window.absoluteWidth - this.interfaceWidth * interfaceScale) / 2) / interfaceScale;
    }

    public double getInterfacePointerY(double y)
    {
        return (y - Math.max(0, Game.game.window.absoluteHeight - this.interfaceHeight * interfaceScale) / 2) / interfaceScale;
    }

    public double gameToAbsoluteX(double x, double sizeX)
    {
        return gameScale * (x - sizeX / 2 - this.gamePosX + this.fullGameWidth / 2);
    }

    public double gameToAbsoluteY(double y, double sizeY)
    {
        return gameScale * (y - sizeY / 2 - this.gamePosY + this.fullGameHeight / 2);
    }

    public boolean isOutOfBounds(double drawX, double drawY)
    {
        return false;
    }
//        int dist = 300;
//        return drawX - dist * gameScale > Game.game.window.absoluteWidth || drawX + dist * gameScale < 0 || drawY - dist * gameScale > Game.game.window.absoluteHeight || drawY + dist * gameScale < 0;
//    }

    public ArrayList<String> wrapText(String msg, double max, double fontSize)
    {
        this.setInterfaceFontSize(fontSize);

        ArrayList<String> lines = new ArrayList<>();
        StringBuilder l = new StringBuilder();

        boolean first = true;
        for (String s : msg.split(" "))
        {
            if (Game.game.window.fontRenderer.getStringSizeX(this.fontSize, l + " " + s) / this.interfaceScale <= max)
            {
                if (!first)
                    l.append(" ");

                l.append(s);
            }
            else if (Game.game.window.fontRenderer.getStringSizeX(this.fontSize, s) / this.interfaceScale > max)
            {
                if (!first)
                    l.append(" ");

                for (char c : s.toCharArray())
                {
                    if (Game.game.window.fontRenderer.getStringSizeX(this.fontSize, l.toString() + c) / this.interfaceScale > max)
                    {
                        lines.add(l.toString());
                        l = new StringBuilder();
                    }

                    l.append(c);
                }
            }
            else
            {
                lines.add(l.toString());
                l = new StringBuilder();
                l.append(s);
            }

            first = false;
        }

        if (l.length() > 0)
            lines.add(l.toString());

        return lines;
    }

    public void setUpscaleImages(boolean upscaleImages)
    {
        Game.game.window.setUpscaleImages(upscaleImages);
    }

    public ModelPart createModelPart()
    {
        return Game.game.window.createModelPart();
    }

    public Model createModel(String dir)
    {
        return new Model(Game.game.window, Game.game.fileManager, dir);
    }
}
