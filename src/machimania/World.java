package machimania;

import machimania.region.NoiseMap;
import machimania.region.Region;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class World
{
    public static final int load_distance = 40;

    public ArrayList<Region> regions = new ArrayList<>();
    public ArrayList<MachimaniaBattle> battles = new ArrayList<>();

    public SkyRenderer sky = new SkyRenderer();

    public NoiseMap noiseMap16 = new NoiseMap(16, 0);
    public NoiseMap noiseMap4 = new NoiseMap(4, 1);
    public NoiseMap noiseMap1 = new NoiseMap(1, 2);

    public NoiseMap heightMap256 = new NoiseMap(256, 6);
    public NoiseMap heightMap32 = new NoiseMap(32, 3);
    public NoiseMap heightMap8 = new NoiseMap(8, 4);
    public NoiseMap heightMap2 = new NoiseMap(2, 5);

    public LinkedList<Region> loadedRegions = new LinkedList<>();
    public LinkedList<Region> loadPendingRegions = new LinkedList<>();
    public LinkedList<Region> unloadedRegions = new LinkedList<>();
    public LinkedList<Region> unloadPendingRegions = new LinkedList<>();

    public World()
    {
        for (int i = -250; i < 250; i++)
        {
            for (int j = -250; j < 250; j++)
            {
                regions.add(new Region(-4 + 8 * i, -4 + 8 * j, 8, 8));
            }
        }

        unloadedRegions.addAll(this.regions);
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
        if (!this.loadPendingRegions.isEmpty())
        {
            for (int i = 0; i < 1; i++)
            {
                double nearestDist = Double.MAX_VALUE;
                Region nearest = null;

                for (Region r : this.loadPendingRegions)
                {
                    double dist = Math.pow(x - (r.posX + r.sizeX / 2.0), 2) + Math.pow(y - (r.posY + r.sizeY / 2.0), 2);

                    if (dist < nearestDist)
                    {
                        nearest = r;
                        nearestDist = dist;
                    }
                }

                if (nearest != null)
                {
                    this.loadPendingRegions.remove(nearest);

                    if (x >= nearest.posX - load_distance && x <= nearest.posX + nearest.sizeX + load_distance && y >= nearest.posY - load_distance && y <= nearest.posY + nearest.sizeY + load_distance)
                    {
                        nearest.load();
                        this.loadedRegions.add(nearest);
                    }
                    else
                    {
                        this.unloadedRegions.add(nearest);
                    }
                }
            }
        }

        if (!this.unloadPendingRegions.isEmpty())
        {
            for (Region r: this.unloadPendingRegions)
            {
                r.unload();
                this.unloadedRegions.add(r);
            }

           this.unloadPendingRegions.clear();
        }

        Iterator<Region> unloadedIrritator = this.unloadedRegions.iterator();
        while (unloadedIrritator.hasNext())
        {
            Region r = unloadedIrritator.next();
            if (x >= r.posX - load_distance && x <= r.posX + r.sizeX + load_distance && y >= r.posY - load_distance && y <= r.posY + r.sizeY + load_distance)
            {
                unloadedIrritator.remove();
                loadPendingRegions.add(r);
            }
        }

        Iterator<Region> loadedIrritator = this.loadedRegions.iterator();
        while (loadedIrritator.hasNext())
        {
            Region r = loadedIrritator.next();
            if (!(x >= r.posX - load_distance && x <= r.posX + r.sizeX + load_distance && y >= r.posY - load_distance && y <= r.posY + r.sizeY + load_distance))
            {
                loadedIrritator.remove();
                unloadPendingRegions.add(r);
            }
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

        for (Region r: this.loadedRegions)
            r.drawTiles(d);
    }
}
