package machimania.machine;

import machimania.Drawing;
import machimania.Game;

public class MachineTypeFighter extends MachineType
{
    public MachineTypeFighter()
    {
        super("fighter", MachineElement.elements.get((int) (Math.random() * MachineElement.elements.size())), 10, 10, 0, 0, 100, 10, 10, 0.5);

        this.description = "The machine moves one tile in any direction. If it runs into any other machine, it instead deals damage to that machine.";
        this.moveOptions.add(new MoveOption(1, 0));
        this.moveOptions.add(new MoveOption(-1, 0));
        this.moveOptions.add(new MoveOption(0, 1));
        this.moveOptions.add(new MoveOption(0, -1));
    }

    @Override
    public void executeMove(MachineIngame m, int x, int y)
    {
        if (new ActionMove(m, x, y).execute())
            return;

        new ActionEffect(m, x, y, this.getDamageEffect()).execute();
    }

    @Override
    public void drawMachineMove(MachineIngame m, MoveOption o)
    {
        Drawing d = Game.game.drawing;
        d.setColor(255, 255, 0);
        double posX = d.gameToInterfaceCoordsX(m.getAbsolutePosX() + o.x / 2.0);
        double posY = d.gameToInterfaceCoordsY(m.getAbsolutePosY() + o.y / 2.0);

        double sX = 30;
        double sY = 30;

        if (Math.abs(o.x) > 0)
            sX = d.gameScale / d.interfaceScale * Math.abs(o.x) - 40;

        if (Math.abs(o.y) > 0)
            sY = d.gameScale / d.interfaceScale * Math.abs(o.y) - 40;

        d.fillInterfaceRect(posX, posY, sX, sY);
        d.fillInterfaceRect(d.gameToInterfaceCoordsX(m.getAbsolutePosX() + o.x), d.gameToInterfaceCoordsY(m.getAbsolutePosY() + o.y), 40, 40);
    }
}
