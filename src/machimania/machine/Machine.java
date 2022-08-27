package machimania.machine;

import machimania.BattlePlayer;
import machimania.Drawing;
import machimania.Game;
import machimania.GameField;
import machimania.gui.Button;

import java.util.ArrayList;

public class Machine
{
    public String name = "Machine";
    public MachineType type;

    public double hitpoints;
    public double condition;
    public double battery;

    public double attack;
    public double armor;
    public double corrosiveness;
    public double proofing;
    public double maxHitpoints;
    public double speed;
    public double maxBattery;
    public double batteryConsumption;

    public double xpLeeched;
    public ArrayList<String> descriptionLines;

    public double selectAnimation = 0;

    public MachineIngame fieldInstance;

    public int level = (int) (Math.random() * 100);
    public double xp = Math.random() * getNextLevelXP(this.level);

    public double hitpointsFlashTime = 0;
    public double hitpointsChangeRate = 1;
    public double newHitpoints;

    public double hpFlashR = 0;
    public double hpFlashG = 0;
    public double hpFlashB = 0;

    public double leechedXpChangeRate = 1;
    public double newXpLeeched;
    public double leechedXpFlashTime = 0;

    public double batteryFlashTime = 0;
    public double newBattery;

    public Button delesect = new Button(Game.game.drawing, -1000, -1000, 130, 30, "Cancel", () -> Game.debugPlayer.machineSelected = -1);

    public Button withdraw = new Button(Game.game.drawing, -1000, -1000, 130, 30, "Withdraw", () ->
    {
        this.withdraw();
        Game.debugPlayer.machineSelected = -1;
    });

    public Machine(MachineType type)
    {
        this.type = type;

        this.attack = this.type.baseAttack;
        this.armor = this.type.baseArmor;
        this.corrosiveness = this.type.baseCorrosiveness;
        this.proofing = this.type.baseProofing;
        this.maxHitpoints = this.type.baseHitpoints;
        this.hitpoints = this.maxHitpoints;
        this.newHitpoints = this.hitpoints;
        this.speed = this.type.baseSpeed;
        this.maxBattery = this.type.baseMaxBattery;
        this.batteryConsumption = this.type.baseBatteryConsumption;

        this.condition = Math.random() * 100;
        this.battery = this.maxBattery;
        this.newBattery = this.battery;

        this.xpLeeched = 0;
    }

    public double getDifficulty()
    {
        return Math.pow(this.level + 1, 2) * this.type.getDifficulty();
    }

    public void deploy(GameField f, int x, int y, BattlePlayer player)
    {
        if (f.tileOpen(x, y) && f.getTileMachine(x, y) == null)
        {
            this.fieldInstance = new MachineIngame(this, f, player);
            this.fieldInstance.setPosition(x, y);
        }
    }

    public void withdraw()
    {
        this.fieldInstance.field.tiles[this.fieldInstance.posX][this.fieldInstance.posY].machine = null;
        this.fieldInstance = null;
    }

    public void damage(double damage)
    {
        this.newHitpoints -= damage;
        if (this.newHitpoints < 0)
            this.newHitpoints = 0;

        double change = Math.abs(this.hitpoints - this.newHitpoints);
        this.hitpointsChangeRate = change / (Math.log10(change + 1)) * 2;

        addLeechedXp((this.hitpoints - this.newHitpoints) / this.maxHitpoints * this.getDifficulty());
    }

    public void checkHitpoints()
    {
        if (this.hitpoints < 0)
            this.hitpoints = 0;

        if (this.hitpoints <= 0 && this.fieldInstance != null)
        {
            addLeechedXp(this.getDifficulty());
            this.withdraw();
        }
    }

    public void addLeechedXp(double xp)
    {
        this.leechedXpChangeRate = xp / (Math.log10(xp + 1)) * 4;
        this.newXpLeeched += xp;
    }

    public void update()
    {
        if (this.hitpoints > this.newHitpoints)
        {
            this.hitpoints -= this.hitpointsChangeRate / 100.0 * Game.game.frameFrequency;
            if (this.hitpoints <= this.newHitpoints)
            {
                this.hitpoints = this.newHitpoints;
                this.checkHitpoints();
            }

            this.hitpointsFlashTime = 1;
            this.hpFlashR = 255;
            this.hpFlashG = 80;
            this.hpFlashB = 80;
        }
        else if (this.hitpoints < this.newHitpoints)
        {
            this.hitpoints += this.hitpointsChangeRate / 100.0 * Game.game.frameFrequency;
            if (this.hitpoints >= this.newHitpoints)
            {
                this.hitpoints = this.newHitpoints;
                this.checkHitpoints();
            }

            this.hitpointsFlashTime = 1;
            this.hpFlashR = 80;
            this.hpFlashG = 255;
            this.hpFlashB = 80;
        }
        else
            this.hitpointsFlashTime = Math.max(0, this.hitpointsFlashTime - Game.game.frameFrequency / 25);

        if (this.newXpLeeched > this.xpLeeched)
        {
            this.xpLeeched += this.leechedXpChangeRate / 100.0 * Game.game.frameFrequency;

            if (this.xpLeeched > this.newXpLeeched)
                this.xpLeeched = this.newXpLeeched;

            this.leechedXpFlashTime = 1;
        }
        else
            this.leechedXpFlashTime = Math.max(0, this.leechedXpFlashTime - Game.game.frameFrequency / 25);

        if (this.battery > this.newBattery)
        {
            this.battery -= Game.game.frameFrequency / 20;
            if (this.battery <= this.newBattery)
            {
                this.battery = this.newBattery;
                this.checkHitpoints();
            }

            this.batteryFlashTime = 1;
        }
        else if (this.battery < this.newBattery)
        {
            this.battery += Game.game.frameFrequency / 20;
            if (this.battery >= this.newBattery)
            {
                this.battery = this.newBattery;
                this.checkHitpoints();
            }

            this.batteryFlashTime = 1;
        }
        else
            this.batteryFlashTime = Math.max(0, this.batteryFlashTime - Game.game.frameFrequency / 25);
    }

    public void updateMoveCard()
    {
        this.delesect.update();

        if (fieldInstance != null)
            this.withdraw.update();
    }

    public void drawMove(GameField f, MachineType.MoveOption o)
    {
        this.type.drawMove(f, this, o);
    }

    public void drawNamecard(double posX, double posY, boolean enemy, boolean selected, boolean hover, boolean affectSelect)
    {
        Drawing d = Game.game.drawing;

        if (affectSelect)
        {
            if (enemy)
                posX -= selectAnimation * 0.8;
            else
                posX += selectAnimation * 0.8;
        }

        if (selected || hover)
        {
            if (affectSelect)
                this.selectAnimation += Game.game.frameFrequency;

            d.setColor(255, 255, 255);
            d.fillInterfaceRect(posX + 150, posY + 20, 310, 50);

            if (!selected)
            {
                d.setColor(200, 200, 200);
                d.fillInterfaceRect(posX + 150, posY + 20, 305, 45);
            }
        }
        else if (fieldInstance != null)
        {
            if (affectSelect)
                this.selectAnimation += Game.game.frameFrequency;

            d.setColor(0, 200, 255);
            d.fillInterfaceRect(posX + 150, posY + 20, 310, 50);

            d.setColor(0, 255, 255);
            d.fillInterfaceRect(posX + 150, posY + 20, 305, 45);
        }
        else
        {
            if (affectSelect)
                this.selectAnimation -= Game.game.frameFrequency;

            d.setColor(150, 150, 150);
            if (hitpoints <= 0)
                d.setColor(0, 0, 0);
            d.fillInterfaceRect(posX + 150, posY + 20, 310, 50);

            d.setColor(200, 200, 200);
            if (hitpoints <= 0)
                d.setColor(100, 100, 100);
            d.fillInterfaceRect(posX + 150, posY + 20, 305, 45);
        }

        if (selectAnimation > 25)
            selectAnimation = 25;

        if (selectAnimation < 0)
            selectAnimation = 0;

        double selectMul = 1;

        if (fieldInstance == null)
            selectMul = 0.8;

        if (this.hitpoints <= 0)
            selectMul = 0.6;

        this.drawNamecardBackground(d, posX, posY, selectMul, enemy);

        double circlePos = posX + 20;

        if (enemy)
        {
            circlePos = posX + 300 - 20;
            this.drawXPIcon(d, circlePos, posY + 20, 40, selectMul);
        }
        else
            this.drawConditionCircle(d, circlePos, posY + 20, 36, selectMul);

        this.drawXPLevel(d, circlePos, posY + 20, 16);

        double namePos = posX + 45;

        if (enemy)
            namePos = posX + 300 - 45;

        drawName(d, namePos, posY + 10, 16, selectMul, enemy);

        d.setColor(0, 0, 0);

        double healthOffset = 0;

        if (enemy)
            healthOffset = 35;

        this.drawHitpoints(d, posX + healthOffset + 43, posY + 30, 160, selectMul);

        if (!enemy)
            this.drawBattery(d, posX + 233, posY + 30, 40, selectMul);
        else
            this.drawLeechedXP(d, posX + 10, posY + 10);
    }

    public void drawXPIcon(Drawing d, double posX, double posY, double size, double mul)
    {
        double r = 255 * mul;
        double g = 140 * mul;
        double b = 255 * mul;

        d.setColor(r * 0.5, g * 0.5, b * 0.5);
        d.drawXPIcon(posX + 1, posY + 1, size);

        d.setColor(r * 0.75, g * 0.75, b * 0.75);
        d.drawXPIcon(posX, posY, size);

        d.setColor(r, g, b);
        d.drawXPIcon(posX, posY, size * 0.8);
    }

    public void drawXPLevel(Drawing d, double posX, double posY, double size)
    {
        d.setInterfaceFontSize(size);

        d.setColor(127, 127, 127);
        d.drawInterfaceText(posX + 1, posY + 1, level + "");

        d.setColor(0, 0, 0);
        d.drawInterfaceText(posX, posY + 0, level + "");
    }

    public void drawConditionCircle(Drawing d, double posX, double posY, double size, double mul)
    {
        double r = 0;
        double g = 0;
        double b = 0;

        if (this.condition >= 80)
        {
            g = 255;
            b = 255;
        }
        else if (this.condition >= 60)
            g = 255;
        else if (this.condition >= 40)
        {
            r = 255;
            g = 255;
        }
        else if (this.condition >= 20)
        {
            r = 255;
            g = 128;
        }
        else
            r = 255;

        r *= mul;
        g *= mul;
        b *= mul;

        d.setColor(0, 0, 0);
        d.fillInterfaceOval(posX, posY, size, size);

        d.setColor(r * 0.75, g * 0.75, b * 0.75);
        d.fillInterfacePartialOval(posX, posY, size, size, Math.PI * 1.5, Math.PI * 1.5 + this.condition / 50.0 * Math.PI);

        d.setColor(r, g, b);
        d.fillInterfaceOval(posX, posY, size * 5/6, size * 5/6);
    }

    public void drawConditionPercent(Drawing d, double posX, double posY, double size)
    {
        d.setInterfaceFontSize(size);

        d.setColor(127, 127, 127);
        d.drawInterfaceText(posX + 0.5, posY + 0.5, (int) condition + "%");

        d.setColor(0, 0, 0);
        d.drawInterfaceText(posX, posY + 0, (int) condition + "%");
    }

    public void drawConditionText(Drawing d, double posX, double posY, double size)
    {
        d.setInterfaceFontSize(size);

        String text = " condition";
        if (this.condition >= 80)
            text = "Great" + text;
        else if (this.condition >= 60)
            text = "Good" + text;
        else if (this.condition >= 40)
            text = "Ok" + text;
        else if (this.condition >= 20)
            text = "Poor" + text;
        else
            text = "Bad" + text;

        d.setColor(127, 127, 127);
        d.drawInterfaceText(posX + 1, posY + 1, text, false);

        d.setColor(0, 0, 0);
        d.drawInterfaceText(posX, posY, text, false);
    }

    public void drawName(Drawing d, double posX, double posY, double size, double mul, boolean enemy)
    {
        int invertMul = 1;
        if (enemy)
            invertMul = -1;

        d.setInterfaceFontSize(size);

        d.setColor(127, 127, 127);
        d.drawInterfaceText(posX + 1, posY + 1, name, enemy);

        d.setColor(0, 0, 0);
        d.drawInterfaceText(posX, posY, name, enemy);

        double width = Game.game.window.fontRenderer.getStringSizeX(d.fontSize, name) / d.interfaceScale;

        if (hitpoints <= 0)
        {
            d.setColor(255 * mul / 2, 0, 0);
            d.fillInterfaceRect(posX + 1 + width / 2 * invertMul, posY + 1, width, 2);

            d.setColor(255 * mul, 0, 0);
            d.fillInterfaceRect(posX + width / 2 * invertMul, posY, width, 2);
        }
    }

    public void drawMovesLeft(Drawing d, double posX, double posY, double size)
    {
        d.setInterfaceFontSize(size);

        int moves = (int)(this.battery / this.batteryConsumption);
        String text = "(" + moves + " moves)";
        if (moves == 1)
            text = "(1 move)";

        d.setColor(127, 127, 127);
        d.drawInterfaceText(posX + 1, posY + 1, text, false);

        d.setColor(0, 0, 0);
        d.drawInterfaceText(posX, posY, text, false);
    }

    public void drawHitpoints(Drawing d, double posX, double posY, double width, double mul)
    {
        d.setColor(0, 0, 0);
        d.drawInterfaceImage("stats/health.png", posX + 7, posY, 14, 14);

        d.setColor(this.hpFlashR * this.hitpointsFlashTime, this.hpFlashG * this.hitpointsFlashTime, this.hpFlashB * this.hitpointsFlashTime);
        d.fillInterfaceRect(posX + 17 + width / 2, posY, width + hitpointsFlashTime * 5, 15 + hitpointsFlashTime * 5);

        d.setColor(140 * mul, 140 * mul, 140 * mul);
        d.fillInterfaceRect(posX + 17 + width / 2, posY, width - 3, 12);

        double extra = this.hitpointsFlashTime * 180;

        double hpFrac = this.hitpoints / this.maxHitpoints;
        if (hpFrac >= 0.5)
            d.setColor(extra, 255 * mul + extra, extra);
        else if (hpFrac >= 0.25)
            d.setColor((1 - (hpFrac - 0.25) * 4) * 255 * mul + extra, 255 * mul + extra, extra);
        else
            d.setColor(255 * mul + extra, (hpFrac) * 4 * 255 * mul + extra, extra);

        d.fillInterfaceProgressRect(posX + 17 + width / 2, posY, width - 3, 12, this.hitpoints / this.maxHitpoints);

        d.setColor(0, 0, 0);

        if (hpFrac <= 0.25 && fieldInstance != null)
            d.setColor(255, 255, 255);

        d.setInterfaceFontSize(13);
        d.drawInterfaceText(posX + 17 + width / 2, posY + 1, (int) this.hitpoints + " / " + (int) this.maxHitpoints);
    }

    public void drawBattery(Drawing d, double posX, double posY, double width, double mul)
    {
        d.setColor(0, 0, 0);
        d.drawInterfaceImage("stats/power.png", posX + 7, posY, 18, 18);

        d.setColor(255 * this.batteryFlashTime, 180 * this.batteryFlashTime, 0);
        d.fillInterfaceRect(posX + 17 + width / 2, posY, width, 15);
        d.fillInterfaceRect(posX + 18 + width / 2, posY, width + 2, 10);

        d.setColor(140 * mul, 140 * mul, 140 * mul);
        d.fillInterfaceRect(posX + 17 + width / 2, posY, width - 3, 12);

        if (this.battery / this.maxBattery <= 0.25)
            d.setColor(255 * mul, 120 * mul + 80 * this.batteryFlashTime, 80 * this.batteryFlashTime);
        else
            d.setColor(255 * mul, 235 * mul, 80 * this.batteryFlashTime);

        d.fillInterfaceProgressRect(posX + 17 + width / 2, posY, width - 3, 12, this.battery / this.maxBattery);

        d.setColor(0, 0, 0);
        if (this.battery / this.maxBattery <= 0.25 && fieldInstance != null)
            d.setColor(255, 255, 255);

        d.setInterfaceFontSize(13);
        d.drawInterfaceText(posX + 17 + width / 2, posY + 1, (int) (this.battery * 100 / this.maxBattery) + "%");
    }

    public void drawLeechedXP(Drawing d, double posX, double posY)
    {
        double xpwidth = Game.game.window.fontRenderer.getStringSizeX(d.fontSize, "" + (int) this.xpLeeched) / d.interfaceScale;


        if (this.hitpoints > 0)
            d.setColor(this.leechedXpFlashTime * 255, 255, 255);
        else
            d.setColor(this.leechedXpFlashTime * 255 / 2, 127, 127);

        d.fillInterfaceGlow(posX + 0, posY, 30, 30);
        d.fillInterfaceGlow(posX + 10 + xpwidth / 2, posY, xpwidth * 2, 20);

        d.setColor(0, 150 + this.leechedXpFlashTime * 50, 255);
        d.drawXPIcon(posX + 0.5, posY + 0.5, 15 + this.leechedXpFlashTime * 4);
        d.setColor(this.leechedXpFlashTime * 50, 255, 255);
        d.drawXPIcon(posX, posY, 15 + this.leechedXpFlashTime * 4);
        d.setColor(0, 150 + this.leechedXpFlashTime * 50, 255);
        d.drawXPIcon(posX, posY, 15 * 0.8);

        d.setInterfaceFontSize(12);
        d.setColor(0, 0, 0);
        d.drawInterfaceText(posX + 10.5, posY + 1, "" + (int) this.xpLeeched, false);

        if (this.hitpoints > 0)
            d.setColor(0, 150 + this.leechedXpFlashTime * 50, 255);
        else
            d.setColor(this.leechedXpFlashTime * 50, 255, 255);

        d.drawInterfaceText(posX + 10, posY + 0.5, "" + (int) this.xpLeeched, false);
    }

    public void drawXPBar(Drawing d, double posX, double posY, double width)
    {
        d.fillInterfaceRect(posX + width / 2, posY, width, 15);
        d.setColor(140, 140, 140);
        d.fillInterfaceRect(posX + width / 2, posY, width - 3, 12);
        d.setColor(255, 140, 255);
        d.fillInterfaceProgressRect(posX + width  / 2, posY, width - 3, 12, this.xp / getNextLevelXP(this.level));
        d.setColor(0, 0, 0);
        d.setInterfaceFontSize(13);
        d.drawInterfaceText(posX + width / 2, posY + 0.5, (int) this.xp + " / " + (int) getNextLevelXP(this.level));
    }

    public void drawNamecardBackground(Drawing d, double posX, double posY, double mul, boolean enemy)
    {
        d.setColor(255 * mul, 255 * mul, 255 * mul);

        int invertMul = 1;
        if (enemy)
            invertMul = -1;

        d.fillInterfaceRect(posX + 150, posY + 20, 300, 40);
        d.drawInterfaceGradientImage("/namecards/namecard-" + this.type.element.name + ".png",posX + 150, posY + 20, 300 * invertMul, 40, 0, 0, 1, 1);
    }

    public void drawStat(Drawing d, double posX, double posY, String img, double stat, double size)
    {
        d.setColor(0, 0, 0);
        d.drawInterfaceImage(img, posX - size / 2, posY, size, size);
        d.setInterfaceFontSize(size);

        d.setColor(127, 127, 127);
        d.drawInterfaceText(posX - size - 2.5, posY + 1.5, (int) stat + "", true);
        d.setColor(0, 0, 0);
        d.drawInterfaceText(posX - size - 3, posY + 1, (int) stat + "", true);
    }

    public void drawDivider(Drawing d, double x, double y, double height)
    {
        d.setColor(0, 0, 0, 64);
        d.fillInterfaceRect(x, y, 2, height);
    }

    public void drawMoveCard(Drawing d, double x, double y, boolean selected, boolean hover)
    {
        d.setColor(255, 255, 255);
        d.fillInterfaceRect(x, y, 1380, 160);
        d.setUpscaleImages(true);
        d.drawInterfaceGradientImage("/namecards/namecard-" + this.type.element.name + ".png", x - 90, y, -1200, 160, 0, 0, 1, 1);
        d.setUpscaleImages(false);

        /*if (selected)
            d.setColor(0, 200, 255);
        else
           d.setColor(0, 150, 255);

        //d.outlineInterfaceRect(x, y, 1384, 164, 2);

        //d.setColor(0, 255, 255);
        //d.outlineInterfaceRect(x, y, 1380, 160, 2);*/

        double startX = x - 690;
        double startY = y - 80;

        drawXPIcon(d, startX + 25, startY + 25, 40, 1);
        drawXPLevel(d, startX + 25, startY + 25, 16);
        drawName(d, startX + 50, startY + 15, 20, 1, false);
        drawXPBar(d, startX + 50, startY + 35, 200);
        drawHitpoints(d, startX + 13, startY + 55, 220, 1);
        drawBattery(d, startX + 13, startY + 75, 100, 1);
        drawMovesLeft(d, startX + 150, startY + 75.5, 14);
        drawConditionCircle(d, startX + 25, startY + 105, 36, 1);
        drawConditionPercent(d, startX + 25, startY + 105, 12);
        drawConditionText(d, startX + 50, startY + 105, 16);

        drawDivider(d, startX + 260, startY + 80, 140);

        drawStat(d, startX + 360, startY + 20, "stats/attack.png", attack, 20);
        drawStat(d, startX + 360, startY + 50, "stats/defense.png", armor, 20);
        drawStat(d, startX + 360, startY + 80, "stats/speed.png", speed, 20);
        drawStat(d, startX + 360, startY + 110, "stats/corrosiveness.png", corrosiveness, 20);
        drawStat(d, startX + 360, startY + 140, "stats/proofing.png", proofing, 20);

        drawDivider(d, startX + 370, startY + 80, 140);

        d.setInterfaceFontSize(20);
        d.setColor(127, 127, 127);
        d.drawInterfaceText(startX + 385.5, startY + 20.5, "Machine type: " + this.type.element.name, false);
        d.setColor(0, 0, 0);
        d.drawInterfaceText(startX + 385, startY + 20, "Machine type: " + this.type.element.name, false);

        if (this.descriptionLines == null)
            this.descriptionLines = Game.game.drawing.wrapText("Move: " + this.type.description, 500, 16);

        d.setInterfaceFontSize(16);
        for (int i = 0; i < this.descriptionLines.size(); i++)
        {
            d.drawInterfaceText(startX + 385, startY + 50 + i * 18, this.descriptionLines.get(i), false);
        }

        drawDivider(d, startX + 900, startY + 80, 140);

        d.setInterfaceFontSize(20);
        d.setColor(0, 0, 0);
        d.drawInterfaceText(startX + 910, startY + 20, "Actions", false);

        if (!selected)
        {
            d.setInterfaceFontSize(16);
            d.setColor(127, 127, 127);
            d.drawInterfaceText(startX + 910, startY + 70, "Select machine", false);
            d.drawInterfaceText(startX + 910, startY + 90, "for actions", false);
        }
        else
        {
            this.delesect.posX = startX + 975;
            this.withdraw.posX = this.delesect.posX;

            if (fieldInstance == null)
                this.delesect.posY = startY + 80;
            else
            {
                this.delesect.posY = startY + 100;
                this.withdraw.posY = startY + 60;
                this.withdraw.draw();
            }

            this.delesect.draw();
        }

        //drawDivider(d, startX + 1050, startY + 80, 140);
    }

    public static double getNextLevelXP(int level)
    {
        return 1000 * Math.pow(level + 1, 2);
    }
}
