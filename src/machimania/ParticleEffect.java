package machimania;

import java.util.LinkedList;

public class ParticleEffect
{
    public enum EffectType {piece, cube, stun, snow, damageIndicator, exclamation}

    public enum State {live, removed, recycle}

    public double posX;
    public double posY;
    public double posZ;

    public double vX;
    public double vY;
    public double vZ;

    public EffectType type;
    public double age = 0;
    public double colR;
    public double colG;
    public double colB;

    public boolean force = false;
    public boolean enableGlow = true;
    public double glowR;
    public double glowG;
    public double glowB;

    public double maxAge = 100;
    public double size;
    public double radius;
    public double angle;
    public double distance;

    protected static LinkedList<ParticleEffect> recycleEffects = new LinkedList<>();

    public int drawLayer = 7;

    public State state = State.live;

    public static ParticleEffect createNewEffect(double x, double y, double z, EffectType type)
    {
        while (recycleEffects.size() > 0)
        {
            ParticleEffect e = recycleEffects.remove();

            if (e.state == State.recycle)
            {
                e.refurbish();
                e.initialize(x, y, z, type);
                return e;
            }
        }

        ParticleEffect e = new ParticleEffect();
        e.initialize(x, y, z, type);
        return e;
    }

    public static ParticleEffect createNewEffect(double x, double y, EffectType type, double age)
    {
        return ParticleEffect.createNewEffect(x, y, 0, type, age);
    }

    public static ParticleEffect createNewEffect(double x, double y, double z, EffectType type, double age)
    {
        ParticleEffect e = ParticleEffect.createNewEffect(x, y, z, type);
        e.age = age;
        return e;
    }

    public static ParticleEffect createNewEffect(double x, double y, EffectType type)
    {
        return ParticleEffect.createNewEffect(x, y, 0, type);
    }

    /**
     * Use Effect.createNewEffect(double x, double y, Effect.EffectType type) instead of this because it can refurbish and reuse old effects
     */
    protected ParticleEffect()
    {

    }

    protected void initialize(double x, double y, double z, EffectType type)
    {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.type = type;

        if (type == EffectType.piece)
        {
            this.maxAge = Math.random() * 100 + 50;
            this.size = 0.1;
        }
        else if (type == EffectType.cube)
        {
            this.maxAge = Math.random() * 100 + 50;
            this.force = true;
        }
        else if (type == EffectType.stun)
        {
            this.angle += Math.PI * 2 * Math.random();
            this.maxAge = 80 + Math.random() * 40;
            this.size = 0.01 * (Math.random() * 5 + 5);
            this.distance = Math.random() * 50 + 25;
        }
        else if (type == EffectType.snow)
        {
            this.maxAge = Math.random() * 100 + 50;
            this.size = 0.01 * ((Math.random() * 4 + 2) * 10);
        }
        else if (type == EffectType.damageIndicator)
            this.maxAge = 150;
        else if (type == EffectType.exclamation)
            this.maxAge = 50;
    }

    protected void refurbish()
    {
        this.posX = 0;
        this.posY = 0;
        this.posZ = 0;
        this.vX = 0;
        this.vY = 0;
        this.vZ = 0;
        this.type = null;
        this.age = 0;
        this.colR = 0;
        this.colG = 0;
        this.colB = 0;
        this.glowR = 0;
        this.glowG = 0;
        this.glowB = 0;
        this.maxAge = Math.random() * 100 + 50;
        this.size = 0;
        this.angle = 0;
        this.distance = 0;
        this.radius = 0;
        this.enableGlow = true;
        this.drawLayer = 7;
        this.state = State.live;
        this.force = false;
    }

    public void draw()
    {
        if (this.maxAge > 0 && this.maxAge < this.age)
            return;

        Drawing drawing = Game.game.drawing;

        if (this.type == EffectType.piece)
        {
            double size = (this.size * (1 - this.age / this.maxAge));
            drawing.setColor(this.colR, this.colG, this.colB, 255, 0.5);
            drawing.fillOval(this.posX, this.posY, this.posZ, size, size);
        }
        else if (this.type == EffectType.snow)
        {
            double size2 = 0.15 * (0.1 * (1 - this.age / this.maxAge));
            drawing.setColor(this.colR, this.colG, this.colB);

            drawing.fillOval(this.posX, this.posY, this.posZ, size2, size2);
        }
        else if (this.type == EffectType.cube)
        {
            double size = (12.5 * (1 - this.age / this.maxAge));
            drawing.setColor(this.colR, this.colG, this.colB);

            drawing.fillBox(this.posX, this.posY, this.posZ, size, size, size);
        }
        else if (this.type == EffectType.damageIndicator)
        {
            double a = Math.min(25, this.maxAge - this.age) * 2.55 * 4;
            drawing.setColor(255, 255, 255, a);

            drawing.setGameFontSize(0.24 * this.size);

            String text = "+" + (int) this.radius;

            if (this.radius <= 0)
                text = "" + (int) this.radius;

            drawing.setGameFontSize(0.24 * this.size);

            double a2 = Math.pow(a / 255, 4) * 255;
            if ((this.colR + this.colG + this.colB) / 3 < 160)
                drawing.setColor(this.colR + 100, this.colG + 100, this.colB + 100, a2, 0.5);
            else
                drawing.setColor(this.colR - 100, this.colG - 100, this.colB - 100, a2, 0.5);

            drawing.drawText(this.posX - 0.01 * a / 255, this.posY, (this.posZ - 0.05) + this.age / 100.0, text);
            drawing.drawText(this.posX + 0.01 * a / 255, this.posY, (this.posZ - 0.05) + this.age / 100.0, text);
            drawing.drawText(this.posX, this.posY - 0.01 * a / 255, (this.posZ - 0.05) + this.age / 100.0, text);
            drawing.drawText(this.posX, this.posY + 0.01 * a / 255, (this.posZ - 0.05) + this.age / 100.0, text);

            drawing.setColor(this.colR, this.colG, this.colB, a, 0.5);
            drawing.drawText(this.posX, this.posY, this.posZ + this.age / 100.0, text);
        }
    }

    public void drawGlow()
    {
        if (this.maxAge > 0 && this.maxAge < this.age)
            return;

        Drawing drawing = Game.game.drawing;

        if (this.type == EffectType.piece)
        {
            double size = (0.1 * (1 - this.age / this.maxAge));

            drawing.setColor(this.colR - this.glowR, this.colG - this.glowG, this.colB - this.glowB, 127, 1);
            drawing.fillGlow(this.posX, this.posY, this.posZ, size * 8, size * 8);
        }
        else if (this.type == EffectType.snow)
        {
            double size = this.size * (1 + this.age / this.maxAge);
            drawing.setColor(this.colR, this.colG, this.colB, (1 - this.age / this.maxAge) * 255);

            drawing.fillGlow(this.posX, this.posY, this.posZ, size, size, true);
        }
    }

    public void update()
    {
        this.posX += this.vX * Game.game.frameFrequency;
        this.posY += this.vY * Game.game.frameFrequency;
        this.posZ += this.vZ * Game.game.frameFrequency;

        if (this.maxAge >= 0)
            this.age += Game.game.frameFrequency;

        if (this.maxAge > 0 && this.age > this.maxAge && this.state == State.live)
            this.state = State.removed;
    }
}
