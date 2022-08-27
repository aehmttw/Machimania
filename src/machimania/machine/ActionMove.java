package machimania.machine;

public class ActionMove extends Action
{
    public ActionMove(MachineIngame m, int x, int y)
    {
        super(m, x, y);
    }

    @Override
    public boolean execute()
    {
        return this.machine.setPosition(this.getTargetX(), this.getTargetY());
    }
}
