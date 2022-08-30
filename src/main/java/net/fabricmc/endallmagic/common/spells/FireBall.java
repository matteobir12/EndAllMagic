package net.fabricmc.endallmagic.common.spells;

import java.util.Arrays;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.SpellConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireBall extends Spell {
    private final int damage;
    public FireBall(){ 
        // element
        pattern.addAll(SpellConfig.FIRE_PATTERN);
        // pattern
        pattern.addAll(Arrays.asList(Pattern.LEFT,Pattern.LEFT));
        manaCost = 4;
        damage = 5;

    }
    public FireBall(int damage, int manaCost){
        // element
        pattern.addAll(SpellConfig.FIRE_PATTERN);
        // pattern
        pattern.addAll(Arrays.asList(Pattern.LEFT,Pattern.LEFT));
        this.manaCost = manaCost;
        this.damage = damage;

    }
    @Override
    public <T extends LivingEntity & MagicUser> void attemptCast(T entity, World world) {
        Vec3d rotation = entity.getRotationVec(1F);
        rotation.normalize().multiply(4.5F);
        FireballEntity fireBall = new FireballEntity(world, entity, rotation.x, rotation.y, rotation.z, damage);

        world.spawnEntity(fireBall);
        world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 2F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        entity.setActiveSpell(null, 0);
    }
}
