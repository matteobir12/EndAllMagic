package net.fabricmc.endallmagic.common.entities;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.common.ModDamageSource;
import net.fabricmc.endallmagic.common.particles.ModParticles;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class TornadoEntity extends Entity {

	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(TornadoEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

	@Nullable
    private UUID ownerUuid;
    @Nullable 
    private Entity owner;
	private float damage;


    protected TornadoEntity(EntityType<? extends TornadoEntity> entityType, World world) {
        super(entityType, world);
        EndAllMagic.LOGGER.info("creating");
		setNoGravity(true);
		if (damage == 0) damage = 0.5F;
    }

    protected TornadoEntity(EntityType<? extends TornadoEntity> type, double x, double y, double z, World world) {
        this(type, world);
        this.setPosition(x, y, z);
    }

    public TornadoEntity(LivingEntity owner, World world,float damage) {
        this(ModEntities.TORNADO_ENTITY, owner.getX(), owner.getEyeY() - 0.1f, owner.getZ(), world);
		this.damage = damage;
		this.owner = owner;
		ownerUuid = owner.getUuid();
		
	}

	protected void onEntityHit(EntityHitResult entityHitResult) {
        entityHitResult.getEntity().setNoGravity(true);
		entityHitResult.getEntity().addVelocity(getVelocity().x, .6, getVelocity().y);
        entityHitResult.getEntity().damage(ModDamageSource.TORNADO_SOURCE, damage);
		if(entityHitResult.getEntity() instanceof LivingEntity target)
			target.timeUntilRegen = 0;
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		//if(!world.isClient && (inGround || isNoClip()) && shake <= 0)
	}

    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }
	@Override
    public Packet<?> createSpawnPacket() {
        Entity entity = owner;
        return new EntitySpawnS2CPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        Entity entity = this.world.getEntityById(packet.getEntityData());
        if (entity != null) {
            owner = entity;
        }
    }

	@Override
    public void tick() {
        BlockPos blockPos;
        super.tick();
        Vec3d velocityVec = this.getVelocity();
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            double d = velocityVec.horizontalLength();
            this.setYaw((float)(MathHelper.atan2(velocityVec.x, velocityVec.z) * 57.2957763671875));
            this.setPitch((float)(MathHelper.atan2(velocityVec.y, d) * 57.2957763671875));
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }
		blockPos = this.getBlockPos();
        
        Vec3d oldPositionVec = this.getPos();
        Vec3d newPositionVec;
        HitResult hitResult = this.world.raycast(new RaycastContext(oldPositionVec, newPositionVec = oldPositionVec.add(velocityVec), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            newPositionVec = hitResult.getPos();
        }
        while (!this.isRemoved()) {
            EntityHitResult entityHitResult = this.getEntityCollision(oldPositionVec, newPositionVec);
            if (entityHitResult != null) {
                hitResult = entityHitResult;
            }
            if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                if (entity instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).shouldDamagePlayer((PlayerEntity)entity)) {
                    hitResult = null;
                    entityHitResult = null;
                }
            }
            if (hitResult != null) {
                if (hitResult.getType() == Type.ENTITY) {
                    onEntityHit((EntityHitResult)hitResult);
                }
                this.velocityDirty = true;
            }
            if (entityHitResult == null) break;
            hitResult = null;
        }
        velocityVec = this.getVelocity();
        double l = velocityVec.horizontalLength();
        this.setYaw((float)(MathHelper.atan2(velocityVec.x, velocityVec.z) * 57.2957763671875));
        this.setPitch((float)(MathHelper.atan2(velocityVec.y, l) * 57.2957763671875));
        if (!this.hasNoGravity()) {
            Vec3d vec3d4 = this.getVelocity();
            this.setVelocity(vec3d4.x, vec3d4.y - 0.05f, vec3d4.z);
        }
        this.setPosition(newPositionVec);
        this.checkBlockCollision();

        Box box = this.getBoundingBox().expand(0.2);
        for (BlockPos bpos : BlockPos.iterate(MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ), MathHelper.floor(box.maxX), MathHelper.floor(box.maxY), MathHelper.floor(box.maxZ))) {
            BlockState state = this.world.getBlockState(bpos);
            Block block = state.getBlock();
            if (!(block instanceof LeavesBlock)) continue;
            this.world.breakBlock(blockPos, true, this);
        }
		

		if(!world.isClient()) {
			for(int count = 0; count < 32; count++) {
				double x = getX() + (world.random.nextInt(9) - 1) / 8D;
				double y = getY() + 0.2F + (world.random.nextInt(25) - 1) / 24D;
				double z = getZ() + (world.random.nextInt(9) - 1) / 8D;
				double deltaX = (world.random.nextInt(9) - 1) * world.random.nextDouble();
				double deltaY = (world.random.nextInt(25) - 1) * world.random.nextDouble();
				double deltaZ = (world.random.nextInt(9) - 1) * world.random.nextDouble();

				PlayerLookup.tracking(this).forEach(player -> ((ServerWorld) world).spawnParticles(player, (ParticleEffect) ModParticles.WIND_BLADE, true, x, y, z, 1, deltaX, deltaY, deltaZ, 0.1));
			}
		}

		if(age > 40)
			kill();

    }

    @Override
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 10.0;
        if (Double.isNaN(d)) {
            d = 1.0;
        }
        return distance < (d *= 64.0 * Entity.getRenderDistanceMultiplier()) * d;
    }

    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(this.world, this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit);
    }

	protected boolean canHit(Entity entity) {
        if (entity.isSpectator() || !entity.isAlive() || !entity.canHit()) {
            return false;
        }
        Entity entity2 = owner;
        return entity2 == null || !entity2.isConnectedThroughVehicle(entity);
    }

	@Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.ownerUuid !=null) {
            nbt.putUuid("Owner", this.ownerUuid);
        }
    }

	@Override
    protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM, ItemStack.EMPTY);
	}

    protected boolean isOwner(Entity entity) {
        return entity.getUuid().equals(owner.getUuid());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
        }
    }

	protected ItemStack asItemStack() {
		return ItemStack.EMPTY;
	}
    
}
