package net.fabricmc.endallmagic.common.spells;

import java.util.Arrays;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.Pattern;
import net.fabricmc.endallmagic.common.SpellConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class HealSpell extends Spell {
    public HealSpell() {
        pattern.addAll(SpellConfig.WATER_PATTERN);
        pattern.addAll(Arrays.asList(Pattern.RIGHT, Pattern.RIGHT));
        manaCost = 4;

    }

    @Override
    public <T extends LivingEntity & MagicUser> void attemptCast(T entity, World world) {
        entity.heal(5);
        // world.playSound(null, entity.getBlockPos(), ModSoundEvents.HEAL, SoundCategory.PLAYERS, 2F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_CHICKEN_AMBIENT, SoundCategory.PLAYERS, 2F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        entity.setActiveSpell(null, 0);
    }
}
