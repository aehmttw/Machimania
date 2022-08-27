package machimania.machine;

public class MachineStat
{
    public String name;

    public double baseValue;
    public int level;

    public double value;

    public MachineStat(String name, double baseValue)
    {
        this.name = name;
        this.baseValue = baseValue;
    }

    public void setLevel(int level)
    {
        this.level = level;
        this.value = value * Math.pow(1.1, level);
    }
}
