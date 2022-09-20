package net.fabricmc.endallmagic.common.spells;

import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.common.MagicUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class StonePlating extends Spell {
    int resistanceLevel;
    int manaCostPerTick;
    public StonePlating(){ 
        // element
        pattern.addAll(SpellConfig.EARTH_PATTERN);
        // pattern
        pattern.addAll(java.util.Arrays.asList(Pattern.LEFT,Pattern.RIGHT));
        manaCost = 0;
        resistanceLevel = 0;
        manaCostPerTick = 1;

    }
    public StonePlating(int speedLevel, int manaCostPerTick){
        // element
        pattern.addAll(SpellConfig.FIRE_PATTERN);
        // pattern
        pattern.addAll(java.util.Arrays.asList(Pattern.LEFT,Pattern.RIGHT));
        this.manaCost = 0;
        this.manaCostPerTick = manaCostPerTick;
        this.resistanceLevel = speedLevel;

    }
    @Override
    public <T extends LivingEntity & MagicUser> void attemptCast(T entity, World world) {
        if (!entity.onTickContains(this)){
            long startTime = world.getTime() % 80;
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 80, resistanceLevel));
            entity.setMana(entity.getCurrentMana()-manaCostPerTick);
            
            entity.addOnTick(this, () -> {
                if (world.getTime() % 80 == startTime){
                    if (entity.getCurrentMana() > 1){
                        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 80, resistanceLevel));
                        entity.setMana(entity.getCurrentMana()-manaCostPerTick);
                    }else{
                        entity.sendMessage(Text.translatable("error." + EndAllMagic.MOD_ID + ".not_enough_mana"));
                        return true;
                        
                    }

                }
                return false;
            });
        } else {
            entity.removeOnTick(this);
        }
    }

}
