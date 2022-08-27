package machimania.machine;

public class ActionEffect extends Action
{
    public Effect effect;

    public ActionEffect(MachineIngame m, int x, int y, Effect e)
    {
        super(m, x, y);

        this.effect = e;
    }

    @Override
    public boolean execute()
    {
        MachineIngame m = this.machine.field.getTileMachine(this.getTargetX(), this.getTargetY());

        if (m == null)
            return false;
        else
            effect.apply(m);

        return true;
    }
}
