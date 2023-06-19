package machimania;

import machimania.region.GroundRendererLowDetail;
import machimania.region.NoiseMap;
import machimania.region.Region;
import machimania.region.RegionMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class World
{
    public static final int load_distance_high_detail = 60;
    public static final int load_distance_low_detail = 2400;

    public RegionMap regions = new RegionMap();
    public ArrayList<MachimaniaBattle> battles = new ArrayList<>();

    public SkyRenderer sky = new SkyRenderer();

    public NoiseMap noiseMap16 = new NoiseMap(16, 0);
    public NoiseMap noiseMap4 = new NoiseMap(4, 1);
    public NoiseMap noiseMap1 = new NoiseMap(1, 2);

    public NoiseMap heightMap2048 = new NoiseMap(2048, 7);
    public NoiseMap heightMap256 = new NoiseMap(256, 6);
    public NoiseMap heightMap32 = new NoiseMap(32, 3);
    public NoiseMap heightMap8 = new NoiseMap(8, 4);
    public NoiseMap heightMap2 = new NoiseMap(2, 5);
    public NoiseMap heightMap1 = new NoiseMap(1, 7);

    public LinkedHashSet<Region> loadedRegions = new LinkedHashSet<>();
    public LinkedHashSet<Region> loadPendingRegions = new LinkedHashSet<>();
    public LinkedHashSet<Region> unloadPendingRegions = new LinkedHashSet<>();

    public LinkedHashSet<Region> loadedRegionsLowDetail = new LinkedHashSet<>();
    public LinkedHashSet<Region> loadPendingRegionsLowDetail = new LinkedHashSet<>();
    public LinkedHashSet<Region> unloadPendingRegionsLowDetail = new LinkedHashSet<>();


    public World()
    {

    }

    public boolean isSolid(int x, int y)
    {
        Region r = regions.getRegion(x, y);
        return r.tiles[x - r.posX][y - r.posY].isSolid;
    }

    public void updateLoadedRegions(int x, int y, boolean lowDetail)
    {
        LinkedHashSet<Region> loaded;
        LinkedHashSet<Region> loadPending;
        LinkedHashSet<Region> unloadPending;
        int loadDist;

        if (!lowDetail)
        {
            loaded = this.loadedRegions;
            loadPending = this.loadPendingRegions;
            unloadPending = this.unloadPendingRegions;
            loadDist = load_distance_high_detail;
        }
        else
        {
            loaded = this.loadedRegionsLowDetail;
            loadPending = this.loadPendingRegionsLowDetail;
            unloadPending = this.unloadPendingRegionsLowDetail;
            loadDist = load_distance_low_detail;
        }

        if (!loadPending.isEmpty())
        {
            long time = System.currentTimeMillis();
            while (loadPending.size() > 0 && System.currentTimeMillis() < time + 10)
            {
                double nearestDist = Double.MAX_VALUE;
                Region nearest = null;

                for (Region r : loadPending)
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
                    loadPending.remove(nearest);

                    if (nearestDist < loadDist * loadDist)
                    {
                        if (lowDetail)
                            nearest.loadLowDetail();
                        else
                            nearest.load();

                        loaded.add(nearest);
                    }
                }
            }
        }

        if (!unloadPending.isEmpty())
        {
            for (Region r: unloadPending)
            {
                if (lowDetail)
                    r.unloadLowDetail();
                else
                    r.unload();
            }

           unloadPending.clear();
        }

        for (int i = -loadDist; i < loadDist; i += RegionMap.region_size)
        {
            for (int j = -loadDist; j < loadDist; j += RegionMap.region_size)
            {
                Region r = regions.getRegion((int) (i + Game.game.character.posX), (int) (j + Game.game.character.posY));
                boolean l = (r.loaded && !lowDetail) || (r.loadedLowDetail && lowDetail);
                double distSq = distSqToPlayer(r.posX + r.sizeX / 2.0, r.posY + r.sizeY / 2.0);
                if (!l && distSq < loadDist * loadDist)
                {
                    int detail = GroundRendererLowDetail.getLoadDetail(Math.sqrt(distSq));
                    int grid = (int) Math.pow(2, detail) / RegionMap.region_size;

                    if (grid <= 0 || Math.floorMod(Math.floorDiv(r.posX, RegionMap.region_size), grid) == 0 && Math.floorMod(Math.floorDiv(r.posY, RegionMap.region_size), grid) == 0)
                        loadPending.add(r);
                }
            }
        }

        Iterator<Region> loadedIrritator = loaded.iterator();
        while (loadedIrritator.hasNext())
        {
            Region r = loadedIrritator.next();
            if (!(distSqToPlayer(r.posX + r.sizeX / 2.0, r.posY + r.sizeY / 2.0) < loadDist * loadDist))
            {
                loadedIrritator.remove();

                double distSq = distSqToPlayer(r.posX + r.sizeX / 2.0, r.posY + r.sizeY / 2.0);
                int detail = GroundRendererLowDetail.getLoadDetail(Math.sqrt(distSq));
                int grid = (int) Math.pow(2, detail) / RegionMap.region_size;

                if (!(grid <= 0 || Math.floorMod(r.posX / RegionMap.region_size, grid) == 0 && Math.floorMod(r.posY / RegionMap.region_size, grid) == 0))
                    unloadPending.add(r);
            }
        }
    }

    public Region getRegion(double x, double y)
    {
        return regions.getRegion((int) x, (int) y);
    }

    public void drawTiles(Drawing d)
    {
        this.sky.draw();

        for (Region r: this.loadedRegions)
            r.drawTiles(d);

        for (Region r: this.loadedRegionsLowDetail)
        {
            if (!r.loaded)
                r.drawTiles(d);
        }
    }

    public void drawTilesTransparent(Drawing d)
    {
        for (Region r: this.loadedRegions)
            r.drawTilesTransparent(d);
    }

    public double distSqToPlayer(double x, double y)
    {
        return Math.pow(x - Game.game.character.posX, 2) + Math.pow(y - Game.game.character.posY, 2);
    }
}
