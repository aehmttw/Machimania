package machimania;

import machimania.region.NoiseMap;
import machimania.region.Region;

import java.util.ArrayList;

public class World
{
    public static final int load_distance = 16;

    public ArrayList<Region> regions = new ArrayList<>();
    public ArrayList<MachimaniaBattle> battles = new ArrayList<>();

    public SkyRenderer sky = new SkyRenderer();

    public NoiseMap noiseMap16 = new NoiseMap(16, 0);
    public NoiseMap noiseMap4 = new NoiseMap(4, 1);
    public NoiseMap noiseMap1 = new NoiseMap(1, 2);

    public NoiseMap heightMap32 = new NoiseMap(32, 3);
    public NoiseMap heightMap8 = new NoiseMap(8, 4);
    public NoiseMap heightMap2 = new NoiseMap(2, 5);

    public World()
    {
        for (int i = -25; i < 25; i++)
        {
            for (int j = -25; j < 25; j++)
            {
                regions.add(new Region(-4 + 8 * i, -4 + 8 * j, 8, 8));
            }
        }
    }

    public boolean isSolid(int x, int y)
    {
        for (Region r: this.regions)
        {
            if (x >= r.posX && y >= r.posY && x < r.posX + r.sizeX && y < r.posY + r.sizeY)
            {
                return r.tiles[x - r.posX][y - r.posY].isSolid;
            }
        }

        return true;
    }

    public void updateLoadedRegions(int x, int y)
    {
        for (Region r: this.regions)
        {
            if (x >= r.posX - load_distance && x <= r.posX + r.sizeX + load_distance && y >= r.posY - load_distance && y <= r.posY + r.sizeY + load_distance)
                r.load();
            else
                r.unload();
        }
    }

    public Region getRegion(double x, double y)
    {
        for (Region r: this.regions)
        {
            if (r.loaded && r.posX <= x && r.posX + r.sizeX > x && r.posY <= y && r.posY + r.sizeY > y)
            {
                return r;
            }
        }

        return null;
    }

    public void drawTiles(Drawing d)
    {
        this.sky.draw();

        for (Region r: this.regions)
            r.drawTiles(d);
    }
}
