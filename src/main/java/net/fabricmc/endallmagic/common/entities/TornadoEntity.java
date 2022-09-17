package net.fabricmc.endallmagic.common.entities;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import com.google.common.collect.Maps;
import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.ModDamageSource;
import net.fabricmc.endallmagic.common.particles.ModParticles;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// review with server and client in mind
public class TornadoEntity extends Entity {
    
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(TornadoEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

	@Nullable
    private UUID ownerUuid;
    @Nullable 
    private Entity owner;
	private float damage;
    private int damageDelay;
    private final Map<Entity, Integer> affectedEntities = Maps.newHashMap();

    protected TornadoEntity(EntityType<? extends TornadoEntity> entityType, World world) {
        super(entityType, world); 
        EndAllMagic.LOGGER.info("creating");
        this.setNoGravity(true);
		if (damage == 0) damage = 0.5F;
    }
    public TornadoEntity(LivingEntity owner, double x, double y, double z, World world, float damage) {
        this(ModEntities.TORNADO_ENTITY, world);
        this.setPosition(x, y, z);
        this.damage = damage;
		this.owner = owner;
		ownerUuid = owner.getUuid();
    }
    public TornadoEntity(LivingEntity owner, World world, float damage) {
        this(ModEntities.TORNADO_ENTITY, world);
        Vec3d rotation = owner.getRotationVec(1F);
        rotation = rotation.normalize().multiply(1.5F);
        this.setPosition(owner.getX()+rotation.x, owner.getY(), owner.getZ()+rotation.z);
		this.damage = damage;
		this.owner = owner;
		ownerUuid = owner.getUuid();
		
	}



	protected void onEntityHit(Entity entityHit) {
        entityHit.setNoGravity(true);
		entityHit.addVelocity(getVelocity().x/2, .5, getVelocity().y/2);
        entityHit.damage(ModDamageSource.TORNADO_SOURCE, damage);
		if(entityHit instanceof LivingEntity target)
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
        Entity entity = this.getOwner();
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
        super.tick();
        if (isAlive()&&world.getTime()%5==0 && owner instanceof MagicUser){
            MagicUser user = ((MagicUser)owner);
            PlayerEntity player = owner instanceof PlayerEntity ? (PlayerEntity)owner:null;
            boolean isCreative = player!=null && player.isCreative();
            if(isCreative || (user.getCurrentMana() > 0) || (user.getCurrentMana() >= 1)) {
                if(!isCreative) {
                    user.setLastCastTime(player.world.getTime());

                    if(user.getCurrentMana() < 1) {
                        // player.damage(ModDamageSource.MAGIC_BURNOUT, burnoutAmount); // damage if over
                        EndAllMagic.LOGGER.info("not enough mana");
                    }

                    user.addMana(-1);
                }
            }else{
                if (player != null)player.sendMessage(Text.translatable("error." + EndAllMagic.MOD_ID + ".not_enough_mana").formatted(Formatting.RED), false);
                discard();
            }
        }
        Vec3d velocityVec = this.getVelocity();
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            double d = velocityVec.horizontalLength();
            this.setYaw((float)(MathHelper.atan2(velocityVec.x, velocityVec.z) * 57.2957763671875));
            this.setPitch((float)(MathHelper.atan2(velocityVec.y, d) * 57.2957763671875));
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }
        
        Vec3d oldPositionVec = this.getPos();
        Vec3d newPositionVec = oldPositionVec.add(velocityVec);
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
            if (block instanceof LeavesBlock) {
                this.world.breakBlock(bpos, true, this);
                continue;
            }
            if (!state.isAir()) {
                if (bpos.getY()<=box.minY+2){
                    this.setPos(this.getX(), bpos.getY()+1, this.getZ());
                }else{
                    discard();
                }
            }
        }
        this.affectedEntities.entrySet().removeIf(entry -> this.age >= entry.getValue());
        List<LivingEntity> list2 = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
        Set<Entity> entitiesNotInTornado = affectedEntities.keySet();
        for (LivingEntity livingEntity : list2) {
            // double r;
            // double q;
            // double s;
            // float f = this.getRadius();
            // !((s = (q = livingEntity.getX() - this.getX()) * q + (r = livingEntity.getZ() - this.getZ()) * r) <= (double)(f * f)) 
            if (this.affectedEntities.containsKey(livingEntity)) {
                entitiesNotInTornado.remove(livingEntity);
                continue;
            }
            this.affectedEntities.put(livingEntity, this.age + this.damageDelay);
            this.onEntityHit(livingEntity);

        }
        entitiesNotInTornado.forEach(e->e.setNoGravity(false));
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

		if(age > 400)
			kill();

    }

    @Nullable
    public Entity getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld && (entity = ((ServerWorld)this.world).getEntity(this.ownerUuid)) instanceof Entity) {
            this.owner = entity;
        }
        return this.owner;
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
        Entity entity2 = this.getOwner();
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
        return entity.getUuid().equals(this.getOwner().getUuid());
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
    
    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }
}
