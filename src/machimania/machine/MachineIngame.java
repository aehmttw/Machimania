package machimania.machine;

import machimania.BattlePlayer;
import machimania.GameField;

public class MachineIngame
{
    public GameField field;
    public Machine machine;
    public BattlePlayer player;

    public int posX = -1;
    public int posY = -1;

    public MachineIngame(Machine m, GameField f, BattlePlayer player)
    {
        this.field = f;
        this.machine = m;
        this.player = player;
    }

    public boolean setPosition(int x, int y)
    {
        if (!this.field.tileOpen(x, y) || this.field.getTileMachine(x, y) != null)
            return false;

        if (this.posX >= 0 && this.posY >= 0)
            this.field.tiles[this.posX][this.posY].machine = null;

        this.field.tiles[x][y].machine = this;
        this.posX = x;
        this.posY = y;

        return true;
    }

    public void move(MachineType.MoveOption o)
    {
        if (this.machine.newBattery < this.machine.batteryConsumption)
            return;

        this.machine.newBattery -= this.machine.batteryConsumption;
        this.machine.type.executeMove(this, o.x, o.y);
    }

    public boolean tileOpen(int x, int y)
    {
        return this.field.tileOpen(this.posX + x, this.posY + y);
    }

    public MachineIngame getTileMachine(int x, int y)
    {
        return this.field.getTileMachine(this.posX + x, this.posY + y);
    }

    public int getAbsolutePosX()
    {
        return this.posX + this.field.posX;
    }

    public int getAbsolutePosY()
    {
        return this.posY + this.field.posY;
    }
}
