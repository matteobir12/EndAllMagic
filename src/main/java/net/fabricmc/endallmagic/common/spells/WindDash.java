package net.fabricmc.endallmagic.common.spells;

import net.fabricmc.endallmagic.common.MagicUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class WindDash extends Spell{
    public WindDash(){ 
        // element
        pattern.addAll(SpellConfig.WIND_PATTERN);
        // pattern
        pattern.addAll(java.util.Arrays.asList(Pattern.RIGHT,Pattern.RIGHT,Pattern.LEFT));
        manaCost = 0;


    }
    public WindDash(int speedLevel, int manaCostPerTick){
        // element
        pattern.addAll(SpellConfig.FIRE_PATTERN);
        // pattern
        pattern.addAll(java.util.Arrays.asList(Pattern.LEFT,Pattern.RIGHT));
        this.manaCost = 0;

    }
    @Override
    public <T extends LivingEntity & MagicUser> void attemptCast(T entity, World world) {
        entity.toggleWindDash();

    }
}
