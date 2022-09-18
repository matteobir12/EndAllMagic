package net.fabricmc.endallmagic.common.spells;

import java.util.Arrays;

import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.entities.RockWallEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RockWall extends Spell {

    int health;

    public RockWall() { 
        // element
        pattern.addAll(SpellConfig.EARTH_PATTERN);
        // pattern
        pattern.addAll(Arrays.asList(Pattern.LEFT,Pattern.LEFT));
        manaCost = 5;
        health = 10;

    }
    public RockWall(int health, int manaCost){
        // element
        pattern.addAll(SpellConfig.WIND_PATTERN);
        // pattern
        pattern.addAll(Arrays.asList(Pattern.RIGHT,Pattern.LEFT,Pattern.LEFT));
        this.manaCost = manaCost;
        this.health = health;
    }
    @Override
    public <T extends LivingEntity & MagicUser> void attemptCast(T entity, World world) {
        Vec3d rotation = entity.getRotationVec(1F);
        RockWallEntity rockWall = new RockWallEntity(world,entity,health);
        rockWall.setVelocity(0,0,0);
        world.spawnEntity(rockWall);
        world.playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_STONE_PLACE, SoundCategory.PLAYERS, 2F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        
    }
}
