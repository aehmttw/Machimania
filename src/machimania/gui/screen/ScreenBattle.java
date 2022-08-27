package machimania.gui.screen;

import basewindow.BaseWindow;
import basewindow.InputCodes;
import basewindow.transformation.RotationAboutPoint;
import machimania.BattlePlayer;
import machimania.Drawing;
import machimania.Game;
import machimania.MachimaniaBattle;
import machimania.machine.Machine;
import machimania.machine.MachineIngame;
import machimania.machine.MachineType;

import java.util.ArrayList;

public class ScreenBattle extends Screen
{
    public ScreenWorld worldScreen;
    public MachimaniaBattle battle;
    public double timeSinceStart = 0;

    public int mouseTileX;
    public int mouseTileY;

    public boolean dimBackground = false;
    public boolean dimEverything = false;

    public boolean activeHover = false;
    public int selectedMove = 0;
    public int hoverMove = -1;

    public boolean goSelected = false;

    public MachineIngame otherHoverMachine = null;

    public BattlePlayer player;

    public boolean executing = false;
    public ArrayList<Machine> executingMoveMachines = new ArrayList<>();
    public ArrayList<MachineType.MoveOption> executingMoveOptions = new ArrayList<>();

    public ScreenBattle(Drawing d, ScreenWorld world, MachimaniaBattle battle, BattlePlayer viewer)
    {
        super(d);
        this.worldScreen = world;
        this.battle = battle;
        this.player = viewer;

        Game.debugEnemy.machinesAvailable[0].deploy(battle.field, 3, 2, Game.debugEnemy);
    }

    @Override
    public void update()
    {
        this.timeSinceStart += Game.game.frameFrequency;

        double frac = Math.min(1, timeSinceStart / 50);

        this.drawing.gamePosX = (1 - frac) * Game.game.character.posX + frac * (battle.field.posX + battle.field.sizeX / 2.0 - 0.5);
        this.drawing.gamePosY = (1 - frac) * Game.game.character.posY + frac * (battle.field.posY + battle.field.sizeY / 2.0 - 0.5);

        this.drawing.zoom = 200 - 100 * frac;

        worldScreen.perspectiveRotation = new RotationAboutPoint(Game.game.window, 0, -Math.PI / 8 * (1 - frac), 0, 0, 0, -1);

        boolean pending = false;
        for (BattlePlayer p: this.battle.players)
        {
            for (Machine m : p.machinesAvailable)
            {
                if (m != null)
                {
                    m.update();

                    if (m.batteryFlashTime > 0 || m.leechedXpFlashTime > 0 || m.hitpointsFlashTime > 0)
                        pending = true;
                }
            }

            if (!battle.field.effects.isEmpty())
                pending = true;
        }

        if (this.executing)
        {
            if (!pending)
            {
                if (this.executingMoveMachines.isEmpty())
                {
                    this.executing = false;
                }
                else
                {

                    Machine m = this.executingMoveMachines.remove(0);
                    MachineType.MoveOption o = this.executingMoveOptions.remove(0);

                    if (m.fieldInstance == null)
                        m.deploy(this.battle.field, o.x, o.y, this.player);
                    else
                        m.fieldInstance.move(o);
                }
            }

            return;
        }

        this.mouseTileX = (int) Math.round(drawing.getGameMouseX());
        this.mouseTileY = (int) Math.round(drawing.getGameMouseY());

        if (Game.game.window.validPressedKeys.contains(InputCodes.KEY_UP))
        {
            this.player.machineSelected--;
            if (this.player.machineSelected < 0)
            {
                this.player.machineSelected = this.player.machinesAvailable.length - 1;
                this.player.selectedMoveOptions[this.selectedMove] = null;
            }

            Game.game.window.validPressedKeys.remove((Integer) InputCodes.KEY_UP);
        }
        else if (Game.game.window.validPressedKeys.contains(InputCodes.KEY_DOWN))
        {
            this.player.machineSelected++;
            if (this.player.machineSelected >= this.player.machinesAvailable.length)
            {
                this.player.machineSelected = 0;
                this.player.selectedMoveOptions[this.selectedMove] = null;
            }

            Game.game.window.validPressedKeys.remove((Integer) InputCodes.KEY_DOWN);
        }
        else if (Game.game.window.validPressedKeys.contains(InputCodes.KEY_ESCAPE))
        {
            this.player.machineSelected = -1;
            this.player.selectedMoveOptions[this.selectedMove] = null;
        }

        int selX = mouseTileX - battle.field.posX;
        int selY = mouseTileY - battle.field.posY;

        this.player.machineHover = -1;
        this.otherHoverMachine = null;

        activeHover = false;

        boolean moveCardSelected = false;
        this.goSelected = false;

        if (drawing.getInterfaceMouseY() >= drawing.interfaceBoundBottom - 180 &&
                drawing.getInterfaceMouseY() <= drawing.interfaceBoundBottom - 20 &&
                drawing.getInterfaceMouseX() >= centerX - 690 &&
                drawing.getInterfaceMouseX() <= centerX + 690)
        {
            moveCardSelected = true;

            if (this.player.machineSelected >= 0)
                this.player.machinesAvailable[this.player.machineSelected].updateMoveCard();

            if (this.player.machineSelected < 0)
                this.player.selectedMoveOptions[this.selectedMove] = null;

            if (drawing.getInterfaceMouseX() >= centerX + 690 - 160)
            {
                this.goSelected = true;
                if (Game.game.window.validPressedButtons.contains(InputCodes.MOUSE_BUTTON_1))
                {
                    Game.game.window.validPressedButtons.remove((Integer)InputCodes.MOUSE_BUTTON_1);
                    this.executeMoves();
                }
            }
        }
        else if (selX >= 0 && selY >= 0 && selX < battle.field.sizeX && selY < battle.field.sizeY)
        {
            if (this.player.machineSelected >= 0 && this.player.machinesAvailable[this.player.machineSelected] != null &&
                    this.player.machinesAvailable[this.player.machineSelected].fieldInstance != null && Game.game.window.validPressedButtons.contains(InputCodes.MOUSE_BUTTON_1))
            {
                Game.game.window.validPressedButtons.remove((Integer) InputCodes.MOUSE_BUTTON_1);
                Machine m = this.player.machinesAvailable[this.player.machineSelected];

                boolean found = false;
                for (MachineType.MoveOption o: m.type.moveOptions)
                {
                    if (o.isValid(m.fieldInstance) && selX == m.fieldInstance.posX + o.x && selY == m.fieldInstance.posY + o.y)
                    {
                        this.player.selectedMoveOptions[this.selectedMove] = o;
                        this.advanceMove();
                        //m.fieldInstance.move(o);
                        found = true;
                    }
                }

                if (!found)
                {
                    this.player.machineSelected = -1;
                    this.player.selectedMoveOptions[this.selectedMove] = null;
                }
            }

            if (battle.field.tiles[selX][selY].machine != null)
            {
                activeHover = true;

                if (battle.field.tiles[selX][selY].machine.player == this.player)
                {
                    if (Game.game.window.validPressedButtons.contains(InputCodes.MOUSE_BUTTON_1))
                    {
                        Game.game.window.validPressedButtons.remove((Integer) InputCodes.MOUSE_BUTTON_1);
                        this.player.setSelectedMachine(battle.field.tiles[selX][selY].machine.machine);
                    }

                    this.player.setHoverMachine(battle.field.tiles[selX][selY].machine.machine);
                }
                else
                    this.otherHoverMachine = battle.field.tiles[selX][selY].machine;
            }
            else if (this.player.machineSelected >= 0 && this.player.machinesAvailable[this.player.machineSelected] != null
                    && this.player.machinesAvailable[this.player.machineSelected].fieldInstance == null
                    && this.player.machinesAvailable[this.player.machineSelected].hitpoints > 0
                    && Game.game.window.validPressedButtons.contains(InputCodes.MOUSE_BUTTON_1))
            {
                this.player.selectedMoveOptions[this.selectedMove] = new MachineType.MoveOptionDeploy(selX, selY);
                this.advanceMove();
                //this.player.machinesAvailable[this.player.machineSelected].deploy(battle.field, selX, selY, this.player);
                Game.game.window.validPressedButtons.remove((Integer) InputCodes.MOUSE_BUTTON_1);
            }
        }

        if (drawing.getInterfaceMouseX() <= 330 + drawing.interfaceBoundLeft && drawing.getInterfaceMouseX() >= drawing.interfaceBoundLeft )
        {
            int i = (int) ((drawing.getInterfaceMouseY() - 70 - drawing.interfaceBoundTop) / 55);
            if (i >= 0 && i < this.player.machinesAvailable.length)
                this.player.machineHover = i;
        }

        if (!moveCardSelected)
        {
            if (Game.game.window.validPressedButtons.contains(InputCodes.MOUSE_BUTTON_1))
            {
                Game.game.window.validPressedButtons.remove((Integer) InputCodes.MOUSE_BUTTON_1);
                this.player.machineSelected = this.player.machineHover;
                this.player.selectedMoveOptions[this.selectedMove] = null;
            }
        }

        this.hoverMove = -1;

        double mx = drawing.getInterfaceMouseX();
        double my = drawing.getInterfaceMouseY();
        double posY = drawing.interfaceBoundBottom - 200 * frac + 100;

        double spacing = 160.0 / this.battle.rules.moves;
        for (int i = 0; i < this.battle.rules.moves; i++)
        {
            double y = posY - 80 + spacing * (i + 0.5);

            if (my > y - spacing / 2 && my <= y + spacing / 2 && mx > this.centerX - 690 - 80 + 1215 - 85 && mx < this.centerX - 690 - 80 + 1215 + 85)
            {
                hoverMove = i;

                if (Game.game.window.validPressedButtons.contains(InputCodes.MOUSE_BUTTON_1))
                {
                    Game.game.window.validPressedButtons.remove((Integer) InputCodes.MOUSE_BUTTON_1);
                    this.setSelectedMove(hoverMove);
                }
            }
        }
    }

    public void saveMove()
    {
        if (this.player.machineSelected < 0)
            this.player.moves[this.selectedMove] = null;
        else
            this.player.moves[this.selectedMove] = this.player.machinesAvailable[this.player.machineSelected];
    }

    public void setSelectedMove(int move)
    {
        this.saveMove();
        this.selectedMove = move;

        Machine m = this.player.moves[move];
        this.player.machineSelected = -1;
        this.player.setSelectedMachine(m);
    }

    public void advanceMove()
    {
        this.saveMove();

        for (int i = 0; i < this.battle.rules.moves; i++)
        {
            if (this.player.selectedMoveOptions[i] == null)
            {
                this.setSelectedMove(i);
                return;
            }
        }
    }

    public void executeMoves()
    {
        this.executing = true;

        boolean found = true;
        while (found)
        {
            found = false;

            BattlePlayer fastestMachineOwner = null;
            int fastestMachineIndex = -1;
            Machine fastestMachine = null;
            MachineType.MoveOption fastestMove = null;
            double mostSpeed = 0;

            for (BattlePlayer p : this.battle.players)
            {
                for (int i = 0; i < p.moves.length; i++)
                {
                    Machine machine = p.moves[i];
                    MachineType.MoveOption move = p.selectedMoveOptions[i];

                    if (machine == null)
                        continue;

                    double speed = machine.speed;

                    if (machine.fieldInstance == null)
                        speed = Double.MAX_VALUE;

                    if (speed > mostSpeed)
                    {
                        fastestMachine = machine;
                        fastestMove = move;
                        fastestMachineOwner = p;
                        fastestMachineIndex = i;
                        mostSpeed = speed;
                        found = true;
                    }
                }
            }

            if (found)
            {
                this.executingMoveMachines.add(fastestMachine);
                this.executingMoveOptions.add(fastestMove);
                fastestMachineOwner.moves[fastestMachineIndex] = null;
                fastestMachineOwner.selectedMoveOptions[fastestMachineIndex] = null;
            }
        }

        this.selectedMove = 0;
        this.player.machineSelected = -1;

        for (int i = 0; i < this.battle.rules.moves; i++)
        {
            for (BattlePlayer p: this.battle.players)
            {
                p.moves[i] = null;
                p.selectedMoveOptions[i] = null;
            }
        }
    }

    @Override
    public void draw()
    {
        BaseWindow window = Game.game.window;

        window.transformations.clear();
        window.transformations.add(worldScreen.perspectiveRotation);
        //window.transformations.add(new Translation(Game.game.window, 0, 0, -3));
        window.loadPerspective();

        Game.game.world.drawTiles(drawing);

        if (dimBackground)
        {
            drawing.setColor(0, 0, 0, 127);
            drawing.fillInterfaceRect(centerX, centerY, drawing.fullInterfaceWidth, drawing.fullInterfaceHeight);
        }

        dimBackground = false;

        this.battle.draw(drawing);

        if (dimEverything)
        {
            drawing.setColor(0, 0, 0, 127);
            drawing.fillInterfaceRect(centerX, centerY, drawing.fullInterfaceWidth, drawing.fullInterfaceHeight);
        }

        dimEverything = false;

        drawing.setColor(200, 200, 200);

        int selX = mouseTileX - battle.field.posX;
        int selY = mouseTileY - battle.field.posY;

        drawing.setColor(255, 255, 255);
        Game.game.character.draw();

        window.transformations.clear();
        window.loadPerspective();

        double frac = Math.min(1, timeSinceStart / 50);

        for (BattlePlayer p: this.battle.players)
        {
            if (p.team == 0)
            {
                p.drawMachineNamecards(10 + drawing.interfaceBoundLeft - 400 * (1 - frac), 75 + drawing.interfaceBoundTop);
                p.drawNamecard(10 + drawing.interfaceBoundLeft - 400 * (1 - frac), 10 + drawing.interfaceBoundTop);
            }
            else
            {
                p.drawMachineNamecards(-10 + drawing.interfaceBoundRight + 400 * (1 - frac) - 300, 75 + drawing.interfaceBoundTop);
                p.drawNamecard(-10 + drawing.interfaceBoundRight + 400 * (1 - frac) - 320, 10 + drawing.interfaceBoundTop);
            }
        }
        double posY = drawing.interfaceBoundBottom - 200 * frac + 100;
        if (this.player.machineSelected >= 0)
            this.player.machinesAvailable[this.player.machineSelected].drawMoveCard(drawing, this.centerX, posY, true, false);
        else if (this.player.machineHover >= 0)
            this.player.machinesAvailable[this.player.machineHover].drawMoveCard(drawing, this.centerX, posY, false, true);
        else
        {
            drawing.setColor(235, 235, 235);
            drawing.fillInterfaceRect(this.centerX, posY, 1380, 160);
            drawing.setColor(0, 0, 0);
            drawing.setInterfaceFontSize(32);
            drawing.drawInterfaceText(this.centerX - 165, posY, "Select a machine");
        }


        double spacing = 160.0 / this.battle.rules.moves;
        for (int i = 0; i < this.battle.rules.moves; i++)
        {
            double y = posY - 80 + spacing * (i + 0.5);

            if (selectedMove == i)
            {
                drawing.setColor(200, 255, 255);
                drawing.fillInterfaceRect(this.centerX - 670 - 80 + 1210, y, 140, spacing);

                Game.game.window.shapeRenderer.setBatchMode(true, false, false);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185, y - spacing / 2, 0);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 30, y, 0);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 30, y - spacing / 2, 0);

                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185, y + spacing / 2, 0);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 30, y, 0);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 30, y + spacing / 2, 0);
                Game.game.window.shapeRenderer.setBatchMode(false, false, false);

                drawing.setColor(0, 0, 0, 64);
                Game.game.window.shapeRenderer.setBatchMode(true, true, false);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185, y - spacing / 2, 0);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 3, y - spacing / 2, 0);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 33, y, 0);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 30, y, 0);

                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185, y + spacing / 2, 0);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 3, y + spacing / 2, 0);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 33, y, 0);
                drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 30, y, 0);
                Game.game.window.shapeRenderer.setBatchMode(false, true, false);

                double ox = spacing / 60;

                Game.game.window.shapeRenderer.setBatchMode(true, true, false);
                if (i != 0)
                {
                    drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 3, y - spacing / 2, 0);
                    drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 3 + ox, y - spacing / 2 + 1, 0);
                    drawing.addInterfaceVertex(this.centerX + 690 - 161, y - spacing / 2 + 1, 0);
                    drawing.addInterfaceVertex(this.centerX + 690 - 161, y - spacing / 2, 0);
                }

                if (i < this.battle.rules.moves - 1)
                {
                    drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 3, y + spacing / 2, 0);
                    drawing.addInterfaceVertex(this.centerX - 670 + 1215 - 185 + 3 + ox, y + spacing / 2 - 1, 0);
                    drawing.addInterfaceVertex(this.centerX + 690 - 161, y + spacing / 2 - 1, 0);
                    drawing.addInterfaceVertex(this.centerX + 690 - 161, y + spacing / 2, 0);
                }
                Game.game.window.shapeRenderer.setBatchMode(false, true, false);
            }
            else
            {
                if (hoverMove == i)
                    drawing.setColor(230, 255, 255);
                else
                    drawing.setColor(255, 255, 255);

                drawing.fillInterfaceRect(this.centerX - 690 - 80 + 1215, y, 170, spacing);
                drawing.setColor(0, 0, 0, 64);
                drawing.fillInterfaceRect(1061, y, 2, spacing);

                if (i != 0)
                    drawing.fillInterfaceRect(this.centerX - 690 - 80 + 1215 + 0.5, y - spacing / 2 + 0.5, 170 - 3, 1);

                if (i < this.battle.rules.moves - 1)
                    drawing.fillInterfaceRect(this.centerX - 690 - 80 + 1215 + 0.5, y + spacing / 2 - 0.5, 170 - 3, 1);
            }

            drawing.setColor(0, 0, 0);
            drawing.setInterfaceFontSize(20);
            drawing.drawInterfaceText(this.centerX - 690 + 1140, y, "Move " + (i + 1));
        }

        drawing.setColor(255, 255, 255);
        drawing.fillInterfaceRect(this.centerX + 690 - 80, posY, 160, 160);

        drawing.setColor(0, 0, 0, 64);
        drawing.fillInterfaceRect(this.centerX + 690 - 160, posY, 2, 160);

        boolean ready = true;

        for (int i = 0; i < this.battle.rules.moves; i++)
        {
            if (this.player.selectedMoveOptions[i] == null)
            {
                ready = false;
                break;
            }
        }

        if (this.goSelected)
        {
            drawing.setColor(60, 160, 60);
            drawing.fillInterfaceOval(this.centerX + 690 - 80, posY, 140, 140);
            drawing.setColor(100, 255, 100);
            drawing.fillInterfaceOval(this.centerX + 690 - 80, posY, 120, 120);
        }
        else if (ready)
        {
            drawing.setColor(120, 255, 120);
            drawing.fillInterfaceOval(this.centerX + 690 - 80, posY, 140, 140);
            drawing.setColor(180, 255, 180);
            drawing.fillInterfaceOval(this.centerX + 690 - 80, posY, 120, 120);
        }
        else
        {
            drawing.setColor(180, 180, 180);
            drawing.fillInterfaceOval(this.centerX + 690 - 80, posY, 140, 140);
            drawing.setColor(220, 220, 220);
            drawing.fillInterfaceOval(this.centerX + 690 - 80, posY, 120, 120);
        }

        drawing.setColor(0, 0, 0);
        drawing.setInterfaceFontSize(40);
        drawing.drawInterfaceText(this.centerX + 690 - 80, posY, "Go!");

        if (this.player.machineSelected >= 0)
        {
            Machine m = this.player.machinesAvailable[this.player.machineSelected];

            if (m.fieldInstance != null)
            {
                for (MachineType.MoveOption o : m.type.moveOptions)
                {
                    if (this.player.selectedMoveOptions[this.selectedMove] == o)
                        drawing.setColor(255, 200, 0);
                    else
                        drawing.setColor(255, 128, 0);

                    int x = m.fieldInstance.posX + this.battle.field.posX + o.x;
                    int y = m.fieldInstance.posY + this.battle.field.posY + o.y;

                    if (battle.field.tileOpen(m.fieldInstance.posX + o.x, m.fieldInstance.posY + o.y))
                       drawing.fillBox(x, y, 0, 1, 1, 0.02);
                }

                drawing.setColor(200, 200, 200);
                drawing.highlightTile(m.fieldInstance.posX + this.battle.field.posX, m.fieldInstance.posY + this.battle.field.posY, 0, 1, 1, 1);
            }
        }

        for (int i = 0; i < this.battle.rules.moves; i++)
        {
            if (this.player.selectedMoveOptions[i] != null)
            {
                this.player.moves[i].drawMove(this.battle.field, this.player.selectedMoveOptions[i]);
            }
        }

        if (activeHover)
        {
            Machine m;
            boolean enemy = false;

            if (this.player.machineHover >= 0)
                m = this.player.machinesAvailable[this.player.machineHover];
            else
            {
                m = this.otherHoverMachine.machine;
                enemy = true;
            }

            m.drawNamecard(drawing.gameToInterfaceCoordsX(mouseTileX) - 150, drawing.gameToInterfaceCoordsY(mouseTileY) - 100, enemy,
                    !enemy && this.player.machineSelected >= 0 && this.player.machinesAvailable[this.player.machineSelected] == m, true, false);
        }

        drawing.setColor(200, 200, 200);
        if (selX >= 0 && selY >= 0 && selX < battle.field.sizeX && selY < battle.field.sizeY)
            drawing.highlightTile(mouseTileX, mouseTileY, 0, 1, 1, 1);

        if (this.player.machineSelected < 0 && this.player.machineHover >= 0)
        {
            Machine m = this.player.machinesAvailable[this.player.machineHover];
            if (m.fieldInstance == null)
            {
                if (m.hitpoints <= 0)
                {
                    dimEverything = true;
                    drawStatus("Machine is down", true);
                }
                else
                    drawStatus("Click to select the machine", false);
            }
        }

        if (this.player.machineSelected >= 0)
        {
            if (this.player.machinesAvailable[this.player.machineSelected].fieldInstance == null)
            {
                if (this.player.machinesAvailable[this.player.machineSelected].hitpoints <= 0)
                {
                    dimEverything = true;
                    drawStatus("Machine is down", true);
                }
                else if (selX >= 0 && selY >= 0 && selX < battle.field.sizeX && selY < battle.field.sizeY)
                {
                    if (battle.field.tileOpen(selX, selY) && battle.field.getTileMachine(selX, selY) == null)
                        drawStatus("Click to deploy here", mouseTileX, mouseTileY);
                    else
                        drawStatus("This tile is occupied", mouseTileX, mouseTileY);
                }
                else
                {
                    drawing.setColor(255, 255, 255);
                    drawing.highlightTile(battle.field.posX + battle.field.sizeX / 2.0 - 0.5, battle.field.posY + battle.field.sizeY / 2.0 - 0.5, 0, battle.field.sizeX, battle.field.sizeY, 1, false);
                    drawStatus("Select an empty tile", true);
                    dimBackground = true;
                }
            }
            else
            {
                drawStatus("Click an orange tile to select a move", false);
            }
        }
    }

    public void drawStatus(String s, boolean center)
    {
        double y = drawing.interfaceBoundBottom - 205;

        if (center)
            y = centerY;

        drawing.setInterfaceFontSize(32);
        drawing.setColor(255, 255, 255);
        double w = Game.game.window.fontRenderer.getStringSizeX(drawing.fontSize, s) / drawing.interfaceScale;
        drawing.fillInterfaceRect(centerX, y, w + 20, 40);
        drawing.setColor(0, 0, 0);
        drawing.drawInterfaceText(centerX, y, s);
    }

    public void drawStatus(String s, double tx, double ty)
    {
        double x = drawing.gameToInterfaceCoordsX(tx);
        double y = drawing.gameToInterfaceCoordsY(ty + 0.75);

        drawing.setInterfaceFontSize(24);
        drawing.setColor(255, 255, 255);
        double w = Game.game.window.fontRenderer.getStringSizeX(drawing.fontSize, s) / drawing.interfaceScale;
        drawing.fillInterfaceRect(x, y, w + 20, 40);
        drawing.setColor(0, 0, 0);
        drawing.drawInterfaceText(x, y, s);
    }
}
