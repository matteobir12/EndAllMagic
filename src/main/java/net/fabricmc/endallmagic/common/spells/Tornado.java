package net.fabricmc.endallmagic.common.spells;

import java.util.Arrays;

import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.entities.TornadoEntity;
import net.fabricmc.endallmagic.common.sounds.ModSoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Tornado extends Spell {
    float damage;
    int knockup;
    public Tornado(){ 
        // element
        pattern.addAll(SpellConfig.WIND_PATTERN);
        // pattern
        pattern.addAll(Arrays.asList(Pattern.RIGHT,Pattern.LEFT,Pattern.LEFT));
        manaCost = 5;
        damage = 0.5F;

    }
    public Tornado(float damage, int manaCost){
        // element
        pattern.addAll(SpellConfig.WIND_PATTERN);
        // pattern
        pattern.addAll(Arrays.asList(Pattern.RIGHT,Pattern.LEFT,Pattern.LEFT));
        this.manaCost = manaCost;
        this.damage = damage;
    }
    @Override
    public <T extends LivingEntity & MagicUser> void attemptCast(T entity, World world) {
        entity.channelSpell(40, () -> {
            Vec3d rotation = entity.getRotationVec(1F);
            rotation = rotation.normalize().multiply(.5F);
            TornadoEntity tornado = new TornadoEntity(entity,world,damage);
            tornado.setVelocity(rotation.x,rotation.y,rotation.z);
            world.spawnEntity(tornado);
            world.playSound(null, entity.getBlockPos(), ModSoundEvents.WIND_BLADE, SoundCategory.PLAYERS, 2F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            return false;
        });
        
    }
}
