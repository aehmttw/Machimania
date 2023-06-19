package machimania.region;

import java.util.HashMap;

public class RegionMap
{
    public HashMap<Integer, HashMap<Integer, Region>> regions = new HashMap<>();
    public static final int region_size = 32;

    public Region getRegion(int i, int j)
    {
        int x = (int) Math.floor(i * 1.0 / region_size);
        int y = (int) Math.floor(j * 1.0 / region_size);

        if (regions.get(x) == null)
            regions.put(x, new HashMap<>());

        if (regions.get(x).get(y) == null)
            regions.get(x).put(y, new Region(-region_size / 2 + region_size * x, -region_size / 2 + region_size * y, region_size, region_size));

        return regions.get(x).get(y);
    }
}
