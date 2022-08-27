package machimania.machine;

import machimania.Game;
import machimania.ParticleEffect;

public class EffectDamage extends Effect
{
    public double damage;
    public MachineElement element;

    public EffectDamage(double damage, MachineElement elementType)
    {
        this.damage = damage;
        this.element = elementType;
    }

    @Override
    public void apply(MachineIngame m)
    {
        double dmg = damage;

        if (dmg < m.machine.armor)
            dmg *= (dmg / m.machine.armor);

        double mul = 1 + element.getMultiplierAgainst(m.machine.type.element);

        ParticleEffect e = ParticleEffect.createNewEffect(m.getAbsolutePosX(), m.getAbsolutePosY(),  1, ParticleEffect.EffectType.damageIndicator);
        e.radius = -damage * mul;
        e.size = 1;
        e.colR = element.colorR;
        e.colG = element.colorG;
        e.colB = element.colorB;
        m.field.effects.add(e);

        m.machine.damage(dmg * mul);
    }
}
