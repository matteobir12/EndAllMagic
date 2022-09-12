package net.fabricmc.endallmagic.common.spells;

import java.util.Arrays;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.entities.WindBladeEntity;
import net.fabricmc.endallmagic.common.sounds.ModSoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WindBladeSpell extends Spell {
    private final int damage;
    public WindBladeSpell(){ 
        // element
        pattern.addAll(SpellConfig.WIND_PATTERN);
        // pattern
        pattern.addAll(Arrays.asList(Pattern.LEFT,Pattern.LEFT));
        manaCost = 5;
        damage = 5;

    }
    public WindBladeSpell(int damage, int manaCost){
        // element
        pattern.addAll(SpellConfig.WIND_PATTERN);
        // pattern
        pattern.addAll(Arrays.asList(Pattern.LEFT,Pattern.LEFT));
        this.manaCost = manaCost;
        this.damage = damage;

    }
    @Override
    public <T extends LivingEntity & MagicUser> void attemptCast(T entity, World world) {
        Vec3d rotation = entity.getRotationVec(1F);
        rotation.normalize().multiply(4.5F);
        WindBladeEntity windBlade = new WindBladeEntity(entity, world,damage);
        windBlade.setVelocity(entity,entity.getPitch(),entity.getYaw(), entity.getRoll(), 4.5F, 0F);

		world.spawnEntity(windBlade);
		world.playSound(null,entity.getBlockPos(), ModSoundEvents.WIND_BLADE, SoundCategory.PLAYERS, 2F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
    }
}
