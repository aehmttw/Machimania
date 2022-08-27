package machimania.machine;

public abstract class Action
{
    public MachineIngame machine;
    public int x;
    public int y;

    public Action(MachineIngame m, int x, int y)
    {
        this.machine = m;
        this.x = x;
        this.y = y;
    }

    public int getTargetX()
    {
        return machine.posX + x;
    }

    public int getTargetY()
    {
        return machine.posY + y;
    }

    public abstract boolean execute();
}
