package machimania.region;

import java.util.Random;

public class NoiseMap
{
    protected int cacheSize = 1024;
    protected double[][] noiseGridCache = new double[cacheSize][cacheSize];
    protected int[][] noiseGridX = new int[cacheSize][cacheSize];
    protected int[][] noiseGridY = new int[cacheSize][cacheSize];
    protected Random random = new Random();

    protected double scale = 1;
    public int seed = 0;

    public NoiseMap(double scale, int seed)
    {
        this.seed = seed;
        this.scale = scale;

        for (int i = 0; i < cacheSize; i++)
        {
            for (int j = 0; j < cacheSize; j++)
            {
                noiseGridX[i][j] = i + 1;
                noiseGridY[i][j] = j + 1;
            }
        }
    }

    public double getNoiseValue(int x, int y)
    {
        int xc = Math.floorMod(x, cacheSize);
        int yc = Math.floorMod(y, cacheSize);

        if (!(noiseGridX[xc][yc] == x && noiseGridY[xc][yc] == y))
        {
            random.setSeed(f(x + seed + f(y)));
            noiseGridCache[xc][yc] = random.nextDouble();
            noiseGridX[xc][yc] = x;
            noiseGridY[xc][yc] = y;
        }

        return noiseGridCache[xc][yc];
    }

    public double get(double x, double y)
    {
        x /= scale;
        y /= scale;

        int x0 = (int) Math.round(x - 1.5);
        int x1 = (int) Math.round(x - 0.5);
        int x2 = (int) Math.round(x + 0.5);
        int x3 = (int) Math.round(x + 1.5);

        double xv0 = getYValue(x0, y);
        double xv1 = getYValue(x1, y);
        double xv2 = getYValue(x2, y);
        double xv3 = getYValue(x3, y);

        return cubicInterpolation(xv0, xv1, xv2, xv3, x - x1);
    }

    public double getYValue(int x, double y)
    {
        int y0 = (int) Math.round(y - 1.5);
        int y1 = (int) Math.round(y - 0.5);
        int y2 = (int) Math.round(y + 0.5);
        int y3 = (int) Math.round(y + 1.5);

        double frac = y - y1;

        return cubicInterpolation(getNoiseValue(x, y0), getNoiseValue(x, y1), getNoiseValue(x, y2), getNoiseValue(x, y3), frac);
    }

    public double cubicInterpolation(double v1, double v2, double v3, double v4, double frac)
    {
        double r = 0;
        r += v1 * (-0.5 * Math.pow(frac, 3) + Math.pow(frac, 2) - 0.5 * frac);
        r += v2 * (1.5 * Math.pow(frac, 3) - 2.5 * Math.pow(frac, 2) + 1);
        r += v3 * (-1.5 * Math.pow(frac, 3) + 2 * Math.pow(frac, 2) + 0.5 * frac);
        r += v4 * (0.5 * Math.pow(frac, 3) - 0.5 * Math.pow(frac, 2));
        return r;
    }

    protected static int f(int i)
    {
        return 1664525 * i + 1013904223;
    }
}
