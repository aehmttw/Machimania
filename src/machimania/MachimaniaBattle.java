package machimania;

public class MachimaniaBattle
{
    public GameField field;
    public BattleRuleset rules = new BattleRuleset();
    public BattlePlayer[] players;

    public MachimaniaBattle(int x, int y, int sX, int sY, BattlePlayer[] players)
    {
        field = new GameField(x, y, sX, sY);
        this.players = players;
    }

    public void draw(Drawing d)
    {
        this.field.draw(d);
    }

}
