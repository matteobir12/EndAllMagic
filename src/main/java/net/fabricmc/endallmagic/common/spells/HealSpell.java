package net.fabricmc.endallmagic.common.spells;

import java.util.Arrays;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.Pattern;
import net.fabricmc.endallmagic.common.SpellConfig;
import net.fabricmc.endallmagic.common.particles.ModParticles;
import net.fabricmc.endallmagic.common.sounds.ModSoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
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
        world.playSound(null, entity.getBlockPos(), ModSoundEvents.HEAL, SoundCategory.PLAYERS, 2F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        for(int amount = 0; amount < 32; amount++) {
			float offsetX = ((random.nextInt(3) - 1) * random.nextFloat());
			float offsetY = random.nextFloat() * 2F;
			float offsetZ = ((random.nextInt(3) - 1) * random.nextFloat());

			((ServerWorld) world).spawnParticles((ParticleEffect) ModParticles.HEAL, entity.getX() + offsetX, entity.getY() - 0.5 + offsetY, entity.getZ() + offsetZ, 1, 0, 0, 0, 0);
		}
        entity.setActiveSpell(null, 0);
    }
}
