package machimania.machine;

import machimania.GameField;

public abstract class Effect
{
    public void apply(GameField f, int x, int y)
    {
        if (x > 0 && y > 0 && x < f.tiles.length && y < f.tiles[0].length)
        {
            if (f.tiles[x][y].machine != null)
                this.apply(f.tiles[x][y].machine);
        }
    }

    public abstract void apply(MachineIngame m);
}
