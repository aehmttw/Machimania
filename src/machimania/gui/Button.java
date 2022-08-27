package machimania.gui;

import basewindow.InputCodes;
import basewindow.InputPoint;
import machimania.Drawing;
import machimania.Game;

import java.util.ArrayList;

public class Button
{
	public Drawing drawing;

	public Runnable function;
	public double posX;
	public double posY;
	public double sizeX;
	public double sizeY;
	public String text;

	public double customFontSize = -1;

	public boolean selected = false;
	public boolean infoSelected = false;
	public boolean enabled = true;
	public boolean enableHover = false;
	public ArrayList<String> hoverText;

	public double disabledColR = 200;
	public double disabledColG = 200;
	public double disabledColB = 200;

	public double unselectedColR = 255;
	public double unselectedColG = 255;
	public double unselectedColB = 255;

	public double selectedColR = 200;
	public double selectedColG = 255;
	public double selectedColB = 255;

	public double textColR = 0;
	public double textColG = 0;
	public double textColB = 0;

	public boolean fullInfo;

	public Button(Drawing d, double x, double y, double sX, double sY, String text, Runnable f)
	{
		this.function = f;

		this.posX = x;
		this.posY = y;
		this.sizeX = sX;
		this.sizeY = sY;
		this.text = text;

		this.drawing = d;
	}

	public Button(Drawing d, double x, double y, double sX, double sY, String text, Runnable f, String hover)
	{
		this(d, x, y, sX, sY, text, f);
		this.enableHover = true;
		this.hoverText = d.wrapText(hover, 300, 12);
	}

	public Button(Drawing d, double x, double y, double sX, double sY, String text)
	{
		this.posX = x;
		this.posY = y;
		this.sizeX = sX;
		this.sizeY = sY;
		this.text = text;

		this.enabled = false;

		this.drawing = d;
	}

	public Button(Drawing d, double x, double y, double sX, double sY, String text, String hover)
	{
		this(d, x, y, sX, sY, text);
		this.enableHover = true;
		this.hoverText = d.wrapText(hover, 300, 12);
	}

	public void draw()
	{
		if (this.customFontSize < 0)
			drawing.setInterfaceFontSize(this.sizeY * 0.6);
		else
			drawing.setInterfaceFontSize(this.customFontSize);

		drawing.setColor(this.disabledColR, this.disabledColG, this.disabledColB);
		drawing.fillInterfaceRect(posX, posY, sizeX, sizeY);

		if (enabled)
		{
			if (!selected)
				drawing.setColor(this.unselectedColR, this.unselectedColG, this.unselectedColB);
			else
				drawing.setColor(this.selectedColR, this.selectedColG, this.selectedColB);

			drawing.fillInterfaceRect(posX, posY, sizeX - 6, sizeY - 6);
		}

		drawing.setColor(this.textColR, this.textColG, this.textColB);
		drawing.drawInterfaceText(posX, posY, text);

		if (enableHover)
		{

			if ((infoSelected || (selected && fullInfo)) && !Game.game.window.touchscreen)
			{
				if (!fullInfo)
				{
					drawing.setColor(0, 0, 255);
					drawing.fillInterfaceOval(this.posX + this.sizeX / 2 - this.sizeY / 2, this.posY, this.sizeY * 3 / 4, this.sizeY * 3 / 4);
					drawing.setColor(255, 255, 255);
					drawing.drawInterfaceText(this.posX + 1 + this.sizeX / 2 - this.sizeY / 2, this.posY, "i");
				}

				drawing.drawTooltip(this.hoverText);
			}
			else if (!fullInfo)
			{
				drawing.setColor(0, 150, 255);
				drawing.fillInterfaceOval(this.posX + this.sizeX / 2 - this.sizeY / 2, this.posY, this.sizeY * 3 / 4, this.sizeY * 3 / 4);
				drawing.setColor(255, 255, 255);
				drawing.drawInterfaceText(this.posX + 1 + this.sizeX / 2 - this.sizeY / 2, this.posY, "i");
			}
		}
	}

	public void update()
	{
		if (!Game.game.window.touchscreen)
		{
			double mx = drawing.getInterfaceMouseX();
			double my = drawing.getInterfaceMouseY();

			boolean handled = checkMouse(mx, my, Game.game.window.validPressedButtons.contains(InputCodes.MOUSE_BUTTON_1));

			if (handled)
				Game.game.window.validPressedButtons.remove((Integer) InputCodes.MOUSE_BUTTON_1);
		}
		else
		{
			for (int i: Game.game.window.touchPoints.keySet())
			{
				InputPoint p = Game.game.window.touchPoints.get(i);

				if (p.tag.equals(""))
				{
					double mx = drawing.getInterfacePointerX(p.x);
					double my = drawing.getInterfacePointerY(p.y);

					boolean handled = checkMouse(mx, my, p.valid);

					if (handled)
						p.tag = "button";
				}
			}
		}
	}

	public boolean checkMouse(double mx, double my, boolean valid)
	{
		boolean handled = false;

		if (Game.game.window.touchscreen)
		{
			sizeX += 20;
			sizeY += 20;
		}

		selected = (mx > posX - sizeX/2 && mx < posX + sizeX/2 && my > posY - sizeY/2  && my < posY + sizeY/2);
		infoSelected = (mx > posX + sizeX/2 - sizeY && mx < posX + sizeX/2 && my > posY - sizeY/2  && my < posY + sizeY/2);

		if (selected && valid)
		{
			if (enabled)
			{
				handled = true;

				function.run();
				selected = false;
			}
		}

		if (Game.game.window.touchscreen)
		{
			sizeX -= 20;
			sizeY -= 20;
		}

		return handled;
	}
}