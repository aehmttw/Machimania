package machimania.machine;

import java.util.ArrayList;
import java.util.HashMap;

public class MachineElement
{
    public static ArrayList<MachineElement> elements = new ArrayList<>();

    public static MachineElement physical = new MachineElement("physical", "elements/physical.png", 220, 220, 220);
    public static MachineElement fire = new MachineElement("fire", "elements/fire.png", 255, 100, 0);
    public static MachineElement water = new MachineElement("water", "elements/water.png", 0, 127, 255);
    public static MachineElement ice = new MachineElement("ice", "elements/ice.png", 100, 255, 255);
    public static MachineElement electric = new MachineElement("electric", "elements/electric.png", 255, 255, 0);
    public static MachineElement plant = new MachineElement("plant", "elements/plant.png", 75, 200, 0);
    public static MachineElement magnetic = new MachineElement("magnetic", "elements/magnetic.png", 230, 50, 170);
    public static MachineElement corrosive = new MachineElement("corrosive", "elements/corrosive.png", 100, 60, 255);
    public static MachineElement virus = new MachineElement("virus", "elements/virus.png", 150, 0, 200);

    public String name;
    public double colorR;
    public double colorG;
    public double colorB;
    public String icon;
    public boolean dark;

    public HashMap<MachineElement, Double> interactions = new HashMap<>();

    public static double[][] typeChart = new double[][]
            {
                    {0, 0, 0.5, 0.5, -0.5, 0, -0.5, 0, 0.5},
                    {0.5, -0.5, -1, 1, 0.5, 1, -0.5, 0.5, -0.5},
                    {-0.5, 1, -0.5, -0.5, 1, -1, 0, 0.5, 0},
                    {0, -1, 0.5, 0.5, -1, 1, -1, 1, -0.5},
                    {0, 0.5, 1, -0.5, -1, -0.5, -0.5, 0, 1},
                    {0.5, -1, 1, -1, 0.5, 0, 0, -0.5, 0.5},
                    {0, 0.5, -1, -1, 0, -1, 1, -0.5, 1},
                    {0.5, 0.5, -1, -0.5, 1, 0.5, -0.5, -1, 1},
                    {-0.5, 1, 0.5, 1, -0.5, 0, 1, 0.5, -1}
            };

    public MachineElement(String name, String icon, double r, double g, double b)
    {
        this.name = name;
        this.icon = icon;
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;

        elements.add(this);
    }

    public double getMultiplierAgainst(MachineElement e)
    {
        if (this.interactions.get(e) != null)
            return this.interactions.get(e);
        else
            return 1;
    }

    public static void initialize()
    {
        setupTypeChart(typeChart, new MachineElement[]{physical, fire, water, ice, electric, plant, magnetic, corrosive, virus});
    }

    public static void setupTypeChart(double[][] typeChart, MachineElement[] types)
    {
        for (int a = 0; a < typeChart.length; a++)
        {
            for (int d = 0; d < typeChart.length; d++)
            {
                types[a].interactions.put(types[d], typeChart[a][d]);
            }
        }
    }
}
