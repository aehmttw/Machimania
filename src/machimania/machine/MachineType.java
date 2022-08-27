package machimania.machine;

import machimania.Drawing;
import machimania.Game;
import machimania.GameField;

import java.util.ArrayList;

public abstract class MachineType
{
    public final String name;

    public MachineElement element;

    public double baseAttack;
    public double baseArmor;
    public double baseCorrosiveness;
    public double baseProofing;
    public double baseHitpoints;
    public double baseSpeed;
    public double baseMaxBattery;
    public double baseBatteryConsumption;
    public ArrayList<MoveOption> moveOptions = new ArrayList<>();

    public String description;

    public MachineType(String name, MachineElement element, double attack, double armor, double corrosiveness, double proofing, double hitpoints, double speed, double maxPower, double powerConsumption)
    {
        this.name = name;
        this.element = element;
        this.baseAttack = attack;
        this.baseArmor = armor;
        this.baseCorrosiveness = corrosiveness;
        this.baseProofing = proofing;
        this.baseHitpoints = hitpoints;
        this.baseSpeed = speed;
        this.baseMaxBattery = maxPower;
        this.baseBatteryConsumption = powerConsumption;
    }

    public static class MoveOption
    {
        public int x;
        public int y;

        public MoveOption(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public boolean isValid(MachineIngame m)
        {
            return m.tileOpen(this.x, this.y);
        }
    }

    public static class MoveOptionDeploy extends MoveOption
    {
        public int x;
        public int y;

        public MoveOptionDeploy(int x, int y)
        {
            super(x, y);
        }

        public boolean isValid(MachineIngame m)
        {
            return m.field.tileOpen(this.x, this.y);
        }
    }

    public abstract void executeMove(MachineIngame m, int x, int y);

    public void drawMove(GameField f, Machine m, MoveOption o)
    {
        double size = 1 * Game.game.drawing.gameScale / Game.game.drawing.interfaceScale;

        if (o instanceof MoveOptionDeploy)
        {
            double x = Game.game.drawing.gameToInterfaceCoordsX(o.x + f.posX);
            double y = Game.game.drawing.gameToInterfaceCoordsY(o.y + f.posY);

            Game.game.drawing.setColor(255, 255, 0, 127);
            Game.game.drawing.outlineInterfaceRect(x, y, size, size, size / 8);

            Game.game.drawing.setColor(255, 255, 0);
            Game.game.drawing.fillInterfaceRect(x, y - 60, 30, 60);
            Game.game.window.shapeRenderer.setBatchMode(true, false, false);
            Game.game.drawing.addInterfaceVertex(x - 30, y - 30, 0);
            Game.game.drawing.addInterfaceVertex(x + 30, y - 30, 0);
            Game.game.drawing.addInterfaceVertex(x, y, 0);
            Game.game.window.shapeRenderer.setBatchMode(false, false, false);

            Game.game.drawing.setInterfaceFontSize(24);
            Game.game.drawing.setColor(255, 255, 255);
            Game.game.drawing.drawInterfaceText(x, y + 20, m.name);
        }
        else
        {
            this.drawMachineMove(m.fieldInstance, o);

            double x = Game.game.drawing.gameToInterfaceCoordsX(m.fieldInstance.posX + o.x + f.posX);
            double y = Game.game.drawing.gameToInterfaceCoordsY(m.fieldInstance.posY + o.y + f.posY);

            Game.game.drawing.setColor(255, 255, 0, 127);
            Game.game.drawing.outlineInterfaceRect(x, y, size, size, size / 8);
        }
    }

    public abstract void drawMachineMove(MachineIngame m, MoveOption o);

    public EffectDamage getDamageEffect()
    {
        return new EffectDamage(this.baseAttack, this.element);
    }

    public double getDifficulty()
    {
        return baseAttack + baseArmor + baseCorrosiveness + baseProofing + baseHitpoints + baseSpeed + baseMaxBattery / baseBatteryConsumption * 10;
    }
}
