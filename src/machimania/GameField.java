package machimania;

import machimania.machine.Effect;
import machimania.machine.MachineIngame;

import java.util.ArrayList;
import java.util.HashSet;

public class GameField
{
    public int posX;
    public int posY;

    public int sizeX;
    public int sizeY;

    public Tile[][] tiles;

    public HashSet<ParticleEffect> effects = new HashSet<>();

    public GameField(int posX, int posY, int sizeX, int sizeY)
    {
        this.posX = posX;
        this.posY = posY;

        this.sizeX = sizeX;
        this.sizeY = sizeY;

        this.tiles = new Tile[sizeX][sizeY];

        for (int i = 0; i < tiles.length; i++)
        {
            for (int j = 0; j < tiles[i].length; j++)
            {
                tiles[i][j] = new Tile();
            }
        }
    }

    public boolean tileOpen(int x, int y)
    {
        if (x < 0 || y < 0 || x >= tiles.length || y >= tiles[0].length)
            return false;

        return tiles[x][y].enabled;
    }

    public MachineIngame getTileMachine(int x, int y)
    {
        if (!tileOpen(x, y))
            return null;
        else
            return tiles[x][y].machine;
    }

    public void draw(Drawing d)
    {
        for (int i = 0; i < tiles.length; i++)
        {
            for (int j = 0; j < tiles[i].length; j++)
            {
                if ((i + j) % 2 == 0)
                    d.setColor(255, 0, 0);
                else
                    d.setColor(200, 0, 0);

                if (tileOpen(i, j))
                    d.fillBox(posX + i, posY + j, 0, 1, 1, 0.01);

                MachineIngame m = tiles[i][j].machine;
                if (m != null)
                {
                    if (m.player.team == 0)
                        d.setColor(255, 255, 255);
                    else
                        d.setColor(255, 200, 255);

                    d.fillBox(posX + i, posY + j, 0, 0.8, 0.8, 0.8);
                }
            }
        }

        ArrayList<ParticleEffect> removeEffects = new ArrayList<>();
        for (ParticleEffect e: this.effects)
        {
            e.update();
            e.draw();

            if (e.state == ParticleEffect.State.removed)
                removeEffects.add(e);
        }

        removeEffects.forEach(effects::remove);
        ParticleEffect.recycleEffects.addAll(removeEffects);
    }

    public static class Tile
    {
        public boolean enabled = true;
        public MachineIngame machine = null;
    }
}
