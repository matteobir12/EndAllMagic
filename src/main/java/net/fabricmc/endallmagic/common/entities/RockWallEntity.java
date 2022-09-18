package net.fabricmc.endallmagic.common.entities;

import java.util.List;

import net.fabricmc.endallmagic.EndAllMagic;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

public class RockWallEntity extends LivingEntity {
    private int maxHealth;

    protected RockWallEntity(EntityType<? extends RockWallEntity> entityType, World world) {
        super(entityType, world); 
        EndAllMagic.LOGGER.info("creating");
        this.setNoGravity(true);
        maxHealth = maxHealth ==0 ? 20 : maxHealth;
    }
    public RockWallEntity(World world,LivingEntity owner) {
        this(ModEntities.ROCK_WALL_ENTITY, world);
        this.setPosition(owner.getX(), owner.getEyeY()-.5, owner.getZ());
        
    }
    public RockWallEntity(World world,LivingEntity owner,int health) {
        this(world,owner);
        this.maxHealth = health;
        
    }
    public RockWallEntity(double x, double y, double z, World world) {
        this(ModEntities.ROCK_WALL_ENTITY, world);
        this.setPosition(x, y, z);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return List.of();
    }
    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot var1) {
        return null;
    }

    public DefaultAttributeContainer.Builder createRockWallAttributes() {
        EndAllMagic.LOGGER.info(maxHealth+"");
        return MobEntity.createMobAttributes().add(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH, 3.0);
    }

    @Override
    public void equipStack(EquipmentSlot var1, ItemStack var2) { }
}
