package machimania;

import machimania.machine.Machine;
import machimania.machine.MachineIngame;
import machimania.machine.MachineType;
import machimania.machine.MachineTypeFighter;

public class BattlePlayer
{
    public MachimaniaBattle battle;
    public String name = "Player";
    public int team = 0;
    public int level = (int) (Math.random() * 100);
    public int machineSelected = -1;
    public int machineHover = -1;
    public double xp = Math.random() * getNextLevelXP(this.level);
    public Machine[] moves;
    public MachineType.MoveOption[] selectedMoveOptions;
    public boolean movesFinalized;

    public Machine[] machinesAvailable;

    public BattlePlayer(BattleRuleset rules)
    {
        this.machinesAvailable = new Machine[rules.maxMachines];
        this.moves = new Machine[rules.moves];
        this.selectedMoveOptions = new MachineType.MoveOption[rules.moves];

        for (int i = 0; i < 10; i++)
        {
            this.machinesAvailable[i] = new Machine(new MachineTypeFighter());

            //this.machinesAvailable[i].type.element = MachineElement.elements.get(i % 9);
            //this.machinesAvailable[i].fieldInstance = new MachineIngame(this.machinesAvailable[i], null);

        }
    }

    public void drawNamecard(double posX, double posY)
    {
        Drawing d = Game.game.drawing;

        d.setColor(150, 80, 255);
        d.fillInterfaceRect(posX + 160, posY + 25, 330, 60);

        d.setColor(127, 0, 255);
        d.fillInterfaceRect(posX + 160, posY + 25, 325, 55);

        d.setColor(255, 255, 255);
        d.fillInterfaceRect(posX + 160, posY + 25, 320, 50);

        double circlePos = posX + 25;

        if (team != 0)
            circlePos = posX + 320 - 25;

        double r = 150;
        double g = 80;
        double b = 255;

        d.setColor(r * 0.5, g * 0.5, b * 0.5);
        d.drawXPIcon(circlePos + 1, posY + 26, 45);

        d.setColor(r * 0.75, g * 0.75, b * 0.75);
        d.drawXPIcon(circlePos, posY + 25, 45);

        d.setColor(r, g, b);
        d.drawXPIcon(circlePos, posY + 25, 45 * 0.8);

        d.setInterfaceFontSize(16);

        d.setColor(127, 127, 127);
        d.drawInterfaceText(circlePos + 1, posY + 26.5, level + "");

        d.setColor(255, 255, 255);
        d.drawInterfaceText(circlePos, posY + 25.5, level + "");

        d.setInterfaceFontSize(20);

        double nameY = posY + 12.5;

        if (team != 0)
        {
            nameY = posY + 25;
            d.setInterfaceFontSize(25);
        }

        if (team != 0)
        {
            d.setColor(127, 127, 127);
            d.drawInterfaceText(posX + 160, nameY + 1, this.name);
            d.setColor(0, 0, 0);
            d.drawInterfaceText(posX + 161, nameY, this.name);
        }
        else
        {
            d.setColor(127, 127, 127);
            d.drawInterfaceText(posX + 55, nameY + 1, this.name, false);
            d.setColor(0, 0, 0);
            d.drawInterfaceText(posX + 56, nameY, this.name, false);
        }

        if (team == 0)
        {
            d.fillInterfaceRect(posX + 180, posY + 35, 250, 15);
            d.setColor(80, 80, 80);
            d.fillInterfaceRect(posX + 180, posY + 35, 247, 12);
            d.setColor(r, g, b);
            d.fillInterfaceProgressRect(posX + 180, posY + 35, 247, 12, this.xp / getNextLevelXP(this.level));
            d.setColor(255, 255, 255);
            d.setInterfaceFontSize(13);
            d.drawInterfaceText(posX + 180, posY + 35.5, (int) this.xp + " / " + (int) getNextLevelXP(this.level));
        }
    }

    public void drawMachineNamecards(double posX, double posY)
    {
        for (int i = 0; i < machinesAvailable.length; i++)
        {
            if (machinesAvailable[i] != null)
                machinesAvailable[i].drawNamecard(posX, posY + i * 55, team != 0, this.machineSelected == i, this.machineHover == i, true);
        }
    }

    public static double getNextLevelXP(int level)
    {
        return Machine.getNextLevelXP(level) * 10;
    }

    public void setSelectedMachine(Machine m)
    {
        for (int i = 0; i < this.machinesAvailable.length; i++)
        {
            if (this.machinesAvailable[i] == m)
                this.machineSelected = i;
        }
    }

    public void setHoverMachine(Machine m)
    {
        for (int i = 0; i < this.machinesAvailable.length; i++)
        {
            if (this.machinesAvailable[i] == m)
                this.machineHover = i;
        }
    }
}
