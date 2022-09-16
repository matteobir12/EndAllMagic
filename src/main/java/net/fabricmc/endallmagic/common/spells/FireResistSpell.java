package net.fabricmc.endallmagic.common.spells;

import java.util.Arrays;
import net.fabricmc.endallmagic.common.MagicUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class FireResistSpell extends Spell {

    public FireResistSpell(){ 
        // element
        pattern.addAll(SpellConfig.FIRE_PATTERN);
        // pattern
        pattern.addAll(Arrays.asList(Pattern.LEFT,Pattern.RIGHT));
        manaCost = 0;

    }
    public FireResistSpell(int manaCost){
        // element
        pattern.addAll(SpellConfig.FIRE_PATTERN);
        // pattern
        pattern.addAll(Arrays.asList(Pattern.LEFT,Pattern.RIGHT));
        this.manaCost = manaCost;
    }
    @Override
    public <T extends LivingEntity & MagicUser> void attemptCast(T entity, World world) {
        entity.toggleManaFireRes();
    }
}
