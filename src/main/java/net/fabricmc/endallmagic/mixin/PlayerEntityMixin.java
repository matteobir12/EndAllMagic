package net.fabricmc.endallmagic.mixin;

import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.spells.Spell;
import net.fabricmc.endallmagic.common.spells.SpellTree;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements MagicUser {
	@Shadow public abstract void sendMessage(Text message, boolean actionBar);

	// @Shadow protected HungerManager hungerManager;
	@Unique private final SpellTree knownSpells = new SpellTree();
	@Unique private Spell activeSpell = null;
	@Unique private long lastCastTime = 0;
	@Unique private int spellTimer = 0;
	@Unique private int maxMana = 20;
	@Unique private final List<Entity> hasHit = new ArrayList<>();

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "createPlayerAttributes", at = @At("RETURN"))
	private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
		info.getReturnValue().add(MANA_COST).add(MANA_REGEN).add(MANA_LOCK);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo info) {
		if(!world.isClient()) {
			if(getCurrentMana() > getMaxMana())
				setMana(getMaxMana());

			if(activeSpell != null) {
				activeSpell.attemptCast(this,world);
			}

			if(spellTimer-- <= 0)
				spellTimer = 0;

			if(world.getTime() >= lastCastTime + 20) {
				int manaCooldown = getManaRegenTimer();

				if(getCurrentMana() < getMaxMana() && world.getTime() % manaCooldown == 0)
					addMana(1);

			}
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	public void readNbt(NbtCompound tag, CallbackInfo info) {
		NbtCompound rootTag = tag.getCompound(EndAllMagic.MOD_ID);
		NbtList listTag = rootTag.getList("KnownSpells", NbtType.STRING);

		for(int i = 0; i < listTag.size(); i++)
			EndAllMagic.SPELL.getOrEmpty(new Identifier(listTag.getString(i))).ifPresent(knownSpells::addSpell);

		dataTracker.set(MANA, rootTag.getInt("Mana"));
		dataTracker.set(SHOW_MANA, rootTag.getBoolean("ShowMana"));
		activeSpell = EndAllMagic.SPELL.get(new Identifier(rootTag.getString("ActiveSpell")));
		lastCastTime = rootTag.getLong("LastCastTime");
		spellTimer = rootTag.getInt("SpellTimer");
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	public void writeNbt(NbtCompound tag, CallbackInfo info) {
		NbtCompound rootTag = new NbtCompound();
		NbtList listTag = new NbtList();

		tag.put(EndAllMagic.MOD_ID, rootTag);
		knownSpells.forEach(spell -> listTag.add(NbtString.of(EndAllMagic.SPELL.getId(spell).toString())));
		rootTag.put("KnownSpells", listTag);
		rootTag.putInt("Mana", dataTracker.get(MANA));
		rootTag.putBoolean("ShowMana", dataTracker.get(SHOW_MANA));
		rootTag.putString("ActiveSpell", activeSpell != null ? EndAllMagic.SPELL.getId(activeSpell).toString() : "");
		rootTag.putLong("LastCastTime", lastCastTime);
		rootTag.putInt("SpellTimer", spellTimer);
	}

	@Inject(method = "initDataTracker", at = @At("HEAD"))
	public void initTracker(CallbackInfo info) {
		dataTracker.startTracking(MANA, MAX_MANA);
		dataTracker.startTracking(SHOW_MANA, false);
	}

	@Override
	public SpellTree getKnownSpells() {
		return knownSpells;
	}

	@Override
	public void setKnownSpell(Spell spell) {
		knownSpells.addSpell(spell);
	}

	@Override
	public int getCurrentMana() {
		return dataTracker.get(MANA);
	}

	@Override
	public int getMaxMana() {
		return maxMana;
	}

	@Override
	public void setMana(int amount) {
		dataTracker.set(MANA, MathHelper.clamp(amount, 0, getMaxMana()));
	}

	@Override
	public void addMana(int amount) {
		setMana(Math.min(getCurrentMana() + amount, getMaxMana()));
	}


	@Override
	public boolean isManaVisible() {
		return dataTracker.get(SHOW_MANA);
	}

	@Override
	public void shouldShowMana(boolean shouldShowMana) {
		dataTracker.set(SHOW_MANA, shouldShowMana);
	}

	@Override
	public void setLastCastTime(long lastCastTime) {
		this.lastCastTime = lastCastTime;
	}

	@Override
	public void setActiveSpell(Spell spell, int timer) {
		this.activeSpell = spell;
		this.spellTimer = timer;
	}

	// @Unique
	// public void castLunge() {
	// 	if(isFallFlying()) {
	// 		if(spellTimer == 10)
	// 			world.playSound(null, getBlockPos(), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 2F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

	// 		if(spellTimer > 0) {
	// 			Vec3d rotation = getRotationVector();
	// 			Vec3d velocity = getVelocity();
	// 			float speed = 0.75F;

	// 			setVelocity(velocity.add(rotation.x * speed + (rotation.x * 1.5D - velocity.x), rotation.y * speed + (rotation.y * 1.5D - velocity.y), rotation.z * speed + (rotation.z * 1.5D - velocity.z)));

	// 			world.getOtherEntities(null, getBoundingBox().expand(2)).forEach(entity -> {
	// 				if(entity != this && entity instanceof LivingEntity && !hasHit.contains(entity)) {
	// 					entity.damage(DamageSource.player((PlayerEntity) (Object) this), 10);
	// 					hasHit.add(entity);
	// 				}
	// 			});

	// 			velocityModified = true;
	// 		}

	// 		if(isOnGround() || spellTimer <= 0) {
	// 			activeSpell = null;
	// 			hasHit.clear();
	// 		}
	// 	}
	// 	else {
	// 		if(spellTimer == 10) {
	// 			setVelocity(0F, 0.75F, 0F);
	// 			world.playSound(null, getBlockPos(), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 2F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
	// 		}

	// 		float adjustedPitch = MathHelper.abs(MathHelper.abs(getPitch() / 90F) - 1);

	// 		if(spellTimer > 0) {
	// 			addVelocity((getRotationVector().x * 0.025F + (getRotationVector().x - getVelocity().x)) * adjustedPitch, 0F, (getRotationVector().z * 0.025F + (getRotationVector().z - getVelocity().z)) * adjustedPitch);
	// 			world.getOtherEntities(null, getBoundingBox().expand(2)).forEach(entity -> {
	// 				if(entity != this && entity instanceof LivingEntity && !hasHit.contains(entity)) {
	// 					entity.damage(DamageSource.player((PlayerEntity) (Object) this), 10);
	// 					hasHit.add(entity);
	// 				}
	// 			});

	// 			velocityModified = true;
	// 		}

	// 		fallDistance = 0;

	// 		if(isOnGround() && spellTimer <= 8) {
	// 			spellTimer = 0;
	// 			world.createExplosion(this, getX(), getY() + 0.5, getZ(), 1, Explosion.DestructionType.NONE);
	// 			activeSpell = null;
	// 			hasHit.clear();
	// 		}
	// 	}
	// }

	// @Unique
	// public void castDreamWarp() {
	// 	ServerPlayerEntity serverPlayer = (ServerPlayerEntity) (Object) this;
	// 	ServerWorld serverWorld = serverPlayer.getServer().getWorld(serverPlayer.getSpawnPointDimension());
	// 	BlockPos spawnPos = serverPlayer.getSpawnPointPosition();
	// 	Vec3d rotation = serverPlayer.getRotationVec(1F);
	// 	Optional<Vec3d> optionalSpawnPoint;
	// 	float spawnAngle = serverPlayer.getSpawnAngle();
	// 	boolean hasSpawnPoint = serverPlayer.isSpawnForced();

	// 	if(serverWorld != null && spawnPos != null)
	// 		optionalSpawnPoint = PlayerEntity.findRespawnPosition(serverWorld, spawnPos, spawnAngle, hasSpawnPoint, true);
	// 	else
	// 		optionalSpawnPoint = Optional.empty();

	// 	if(optionalSpawnPoint.isPresent()) {
	// 		Vec3d spawnPoint = optionalSpawnPoint.get();
	// 		System.out.println(spawnPoint);
	// 		world.playSound(null, getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 2F, 1F);
	// 		serverPlayer.teleport(serverWorld, spawnPoint.x, spawnPoint.y, spawnPoint.z, (float) rotation.x, (float) rotation.y);
	// 		world.playSound(null, getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 2F, 1F);
	// 	}
	// 	else {
	// 		sendMessage(Text.translatable("block.minecraft.spawn.not_valid"), false);
	// 	}

	// 	activeSpell = null;
	// }

	// @Unique
	// public void castTelekinesis() {
	// 	HitResult result = ArcanusHelper.raycast(this, 10F, true);
	// 	Vec3d rotation = getRotationVec(1F);
	// 	double startDivisor = 5D;
	// 	double endDivisor = 15D;

	// 	for(int count = 0; count < 8; count++) {
	// 		Vec3d startPos = getCameraPosVec(1F).add((world.random.nextInt(3) - 1) / startDivisor, (world.random.nextInt(3) - 1) / startDivisor, (world.random.nextInt(3) - 1) / startDivisor);
	// 		Vec3d endPos = result.getPos().add((world.random.nextInt(3) - 1) / endDivisor, (world.random.nextInt(3) - 1) / endDivisor, (world.random.nextInt(3) - 1) / endDivisor);

	// 		ArcanusHelper.drawLine(startPos, endPos, world, 0.5F, (ParticleEffect) ModParticles.TELEKINETIC_SHOCK);
	// 	}

	// 	world.playSound(null, getBlockPos(), ModSoundEvents.TELEKINETIC_SHOCK, SoundCategory.PLAYERS, 2F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

	// 	switch(result.getType()) {
	// 		case ENTITY -> {
	// 			BlockPos pos = ((EntityHitResult) result).getEntity().getBlockPos();
	// 			Box box = new Box(pos);

	// 			world.getOtherEntities(this, box, EntityPredicates.VALID_ENTITY).forEach(target -> {
	// 				if(target instanceof PersistentProjectileEntity projectile)
	// 					projectile.fall();

	// 				target.setVelocity(rotation.multiply(2.5F));
	// 				target.velocityModified = true;
	// 			});
	// 		}
	// 		case BLOCK -> {
	// 			BlockPos pos = ((BlockHitResult) result).getBlockPos();
	// 			Box box = new Box(pos);
	// 			BlockState state = world.getBlockState(pos);
	// 			Block block = state.getBlock();

	// 			if(block instanceof TntBlock) {
	// 				TntBlock.primeTnt(world, pos, this);
	// 				world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);

	// 				world.getEntitiesByClass(TntEntity.class, box, tnt -> tnt.isAlive() && tnt.getCausingEntity() == this).forEach(target -> {
	// 					target.setVelocity(rotation.multiply(2.5F));
	// 					target.velocityModified = true;
	// 				});
	// 			}

	// 			if(block instanceof FallingBlock fallingBlock) {
	// 				FallingBlockEntity target = FallingBlockEntity.spawnFromBlock(world, pos, state);
	// 				fallingBlock.configureFallingBlockEntity(target);
	// 				target.setVelocity(rotation.multiply(2.5F));
	// 				target.velocityModified = true;
	// 				world.spawnEntity(target);
	// 			}
	// 		}
	// 	}

	// 	activeSpell = null;
	// }

	// @Unique
	// public void castHeal() {
	// 	heal(10);
	// 	world.playSound(null, getBlockPos(), ModSoundEvents.HEAL, SoundCategory.PLAYERS, 2F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

	// 	for(int amount = 0; amount < 32; amount++) {
	// 		float offsetX = ((random.nextInt(3) - 1) * random.nextFloat());
	// 		float offsetY = random.nextFloat() * 2F;
	// 		float offsetZ = ((random.nextInt(3) - 1) * random.nextFloat());

	// 		((ServerWorld) world).spawnParticles((ParticleEffect) ModParticles.HEAL, getX() + offsetX, getY() - 0.5 + offsetY, getZ() + offsetZ, 1, 0, 0, 0, 0);
	// 	}

	// 	activeSpell = null;
	// }

	// @Unique
	// public void castDiscombobulate() {
	// 	HitResult result = ArcanusHelper.raycast(this, 4F, true);
	// 	double startDivisor = 5D;
	// 	double endDivisor = 15D;

	// 	for(int count = 0; count < 8; count++) {
	// 		Vec3d startPos = getCameraPosVec(1F).add((world.random.nextInt(3) - 1) / startDivisor, (world.random.nextInt(3) - 1) / startDivisor, (world.random.nextInt(3) - 1) / startDivisor);
	// 		Vec3d endPos = result.getPos().add((world.random.nextInt(3) - 1) / endDivisor, (world.random.nextInt(3) - 1) / endDivisor, (world.random.nextInt(3) - 1) / endDivisor);

	// 		ArcanusHelper.drawLine(startPos, endPos, world, 0.5F, (ParticleEffect) ModParticles.DISCOMBOBULATE);
	// 	}

	// 	if(result.getType() == HitResult.Type.ENTITY) {
	// 		if(((EntityHitResult) result).getEntity() instanceof CanBeDiscombobulated target) {
	// 			target.setDiscombobulated(true);
	// 			target.setDiscombobulatedTimer(160);
	// 		}
	// 	}
	// 	else {
	// 		sendMessage(Text.translatable("spell." + Arcanus.MOD_ID + ".no_target"), false);
	// 	}

	// 	activeSpell = null;
	// }

	// @Unique
	// public void castSolarStrike() {
	// 	HitResult result = ArcanusHelper.raycast(this, 640F, false);

	// 	if(result.getType() != HitResult.Type.MISS) {
	// 		ChunkPos chunkPos = new ChunkPos(new BlockPos(result.getPos()));
	// 		((ServerWorld) world).setChunkForced(chunkPos.x, chunkPos.z, true);
	// 		SolarStrikeEntity solarStrike = new SolarStrikeEntity(this, world);
	// 		solarStrike.setPosition(result.getPos());
	// 		world.spawnEntity(solarStrike);
	// 	}
	// 	else {
	// 		sendMessage(Text.translatable("spell." + Arcanus.MOD_ID + ".no_target"), false);
	// 	}

	// 	activeSpell = null;
	// }

	// @Unique
	// public void castArcaneBarrier() {
	// 	HitResult result = ArcanusHelper.raycast(this, 24F, false);

	// 	if(result.getType() == HitResult.Type.BLOCK) {
	// 		BlockHitResult blockResult = ((BlockHitResult) result);
	// 		Direction side = blockResult.getSide();
	// 		ArcaneBarrierEntity arcaneWall = new ArcaneBarrierEntity((PlayerEntity) (Object) this, world);
	// 		BlockPos pos = blockResult.getBlockPos().add(side.getOffsetX(), side.getOffsetY(), side.getOffsetZ());
	// 		arcaneWall.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
	// 		world.spawnEntity(arcaneWall);
	// 	}
	// 	else {
	// 		sendMessage(Text.translatable("spell." + Arcanus.MOD_ID + ".no_target"), false);
	// 	}

	// 	activeSpell = null;
	// }
}
