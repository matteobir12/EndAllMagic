package net.fabricmc.endallmagic.mixin;

import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.network.ServerNetworking;
import net.fabricmc.endallmagic.common.spells.Spell;
import net.fabricmc.endallmagic.common.spells.SpellConfig;
import net.fabricmc.endallmagic.common.spells.SpellTree;
import net.fabricmc.endallmagic.common.spells.SpellConfig.Affinity;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.fabricmc.endallmagic.EndAllMagic.DataTrackers.*;
import static net.fabricmc.endallmagic.EndAllMagic.MagicEntityAttributes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements MagicUser {
	@Shadow public abstract void sendMessage(Text message, boolean actionBar);

	@Unique private final SpellTree knownSpells = new SpellTree();
	@Unique private Spell activeSpell = null;
	@Unique private long lastCastTime = 0;
	@Unique private int spellTimer = 0;
	@Unique private int manaRegenTimer=120;
	@Unique private boolean mitigateFireDamage=false;
	@Unique private Map<Spell,OnTick> onTicks = new HashMap<>();
	@Unique private final List<Entity> hasHit = new ArrayList<>();
	@Unique private int channelSpellTimer = 0;
	@Unique private OnTick channeledSpell;
	@Unique private boolean windDashEnable = false;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "createPlayerAttributes", at = @At("RETURN"))
	private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
		info.getReturnValue().add(MANA_COST).add(MANA_REGEN);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo info) {
		if(!world.isClient()) {
			if(getCurrentMana() > getMaxMana())
				setMana(getMaxMana());

			if(activeSpell != null) {
				activeSpell.attemptCast(this,world);
				activeSpell = null;
			}

			if(spellTimer-- <= 0)
				spellTimer = 0;
			
			if(channelSpellTimer > 0){
				channelSpellTimer--;
				if (channelSpellTimer == 0){
					if (channeledSpell!=null) channeledSpell.tick();
					channeledSpell = null;
				} else if (channelSpellTimer+1 % 10 == 0){
					sendMessage(Text.translatable("spell." + EndAllMagic.MOD_ID + ".channel_timer_remaining").append(": "+(channelSpellTimer/20)),true);
				}
			}
			
			if(world.getTime() >= lastCastTime + 20) {
				int manaCooldown = getManaRegenTimer();

				if(getCurrentMana() < getMaxMana() && world.getTime() % (manaCooldown/getLevel()) == 0)
					addMana(1);

			}

			Iterator<OnTick> i = onTicks.values().iterator();
			while(i.hasNext())
				if (i.next().tick()){
					i.remove();
					EndAllMagic.LOGGER.info("stoppin");
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
		int lvl = rootTag.getInt("Level");
		dataTracker.set(LEVEL, lvl > 0? lvl :1);
		dataTracker.set(AFFINITY, rootTag.getInt("Affinity"));
		dataTracker.set(EARTH_EXP, rootTag.getInt("EarthExp"));
		dataTracker.set(FIRE_EXP, rootTag.getInt("FireExp"));
		dataTracker.set(WIND_EXP, rootTag.getInt("WindExp"));
		dataTracker.set(WATER_EXP, rootTag.getInt("WaterExp"));
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
		rootTag.putInt("Level", dataTracker.get(LEVEL));
		rootTag.putInt("Affinity", dataTracker.get(AFFINITY));
		rootTag.putBoolean("ShowMana", dataTracker.get(SHOW_MANA));
		rootTag.putInt("EarthExp", dataTracker.get(EARTH_EXP));
		rootTag.putInt("FireExp", dataTracker.get(FIRE_EXP));
		rootTag.putInt("WindExp", dataTracker.get(WIND_EXP));
		rootTag.putInt("WaterExp", dataTracker.get(WATER_EXP));
		rootTag.putString("ActiveSpell", activeSpell != null ? EndAllMagic.SPELL.getId(activeSpell).toString() : "");
		rootTag.putLong("LastCastTime", lastCastTime);
		rootTag.putInt("SpellTimer", spellTimer);
	}

	@Inject(method = "initDataTracker", at = @At("HEAD"))
	public void initTracker(CallbackInfo info) {
		dataTracker.startTracking(LEVEL, 1);
		dataTracker.startTracking(MANA, getMaxMana());
		dataTracker.startTracking(SHOW_MANA, false);
		dataTracker.startTracking(AFFINITY, SpellConfig.Affinity.NONE.ordinal());
		dataTracker.startTracking(EARTH_EXP, 0);
		dataTracker.startTracking(FIRE_EXP, 0);
		dataTracker.startTracking(WIND_EXP, 0);
		dataTracker.startTracking(WATER_EXP, 0);
		
	}

	@ModifyVariable(method = "applyDamage", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/entity/player/PlayerEntity;getHealth()F"), ordinal = 0, argsOnly = true)
	private float modifyDamage(float amount, DamageSource source) {
		if (!world.isClient && getCurrentMana() > 0 && mitigateFireDamage && source.isFire()) {
			if (amount > getCurrentMana()){
				Float newDmg = amount-getCurrentMana();
				setMana(0);
				sendMessage(Text.translatable("error." + EndAllMagic.MOD_ID + ".not_enough_mana"));
				cancelChannel();
				return newDmg;
			}else{
				setMana((int)(getCurrentMana()-amount));
				return 0;
			}
		}
		cancelChannel();
		return amount;
	}
	@Override
	public SpellTree getKnownSpells() {
		return knownSpells;
	}

	@Override
	public void setKnownSpell(Spell spell) {
		knownSpells.addSpell(spell);
		if (!world.isClient)
			ServerNetworking.sendKnownSpell(this, spell);
	}

	@Override
	public int getCurrentMana() {
		return dataTracker.get(MANA);
	}

	@Override
	public int getMaxMana() {
		return getLevel() * 20;
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
	public int getManaRegenTimer() {
		return manaRegenTimer;
	}

	@Override
	public boolean isManaVisible() {
		return dataTracker.get(SHOW_MANA);
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
	
	@Override
	public void setLevel(int level) {
		dataTracker.set(LEVEL, level);
		
	}
	@Override
	public int getLevel() {
		return dataTracker.get(LEVEL);
	}
	@Override
	public void setAffinity(Affinity affinity) {
		dataTracker.set(AFFINITY, affinity.ordinal());
	}
	@Override
	public Affinity getAffinity() {
		return SpellConfig.Affinity.values()[dataTracker.get(AFFINITY)];
	}
	@Override
	public void addOnTick(Spell s, OnTick t) {
		onTicks.put(s, t);
		
	}
	@Override
	public void removeOnTick(Spell s) {
		onTicks.remove(s);
		
	}
	@Override
	public boolean onTickContains(Spell s) {
		return onTicks.containsKey(s);
	}
	@Override
	public void toggleManaFireRes() {
		mitigateFireDamage = !mitigateFireDamage;
	}

	@Override
	public void channelSpell(int timer, OnTick spell) {
		channelSpellTimer = timer;
		channeledSpell = spell;
		addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, timer, 3));
		
	}
	public void cancelChannel(){
		sendMessage(Text.translatable("error." + EndAllMagic.MOD_ID + ".channel_cancelled"));
		channelSpellTimer = 0;
		channeledSpell = null;
	}

	@Override
	public void toggleWindDash() {
		EndAllMagic.LOGGER.info("toggling " + world.isClient());
		windDashEnable = !windDashEnable;
		if (!world.isClient) ServerNetworking.sendWindDashToggle(this);
	}
	@Override
	public void windDashDirection(int direction) {
		Vec3d rotation = getRotationVec(1F);
		Vec3d rotat = new Vec3d(rotation.x, 0, rotation.z);
		rotat = rotat.normalize().multiply(0.5F);
		velocityModified = true;
		switch (direction) {
			case 0:
				EndAllMagic.LOGGER.info("dashing straight");
				addVelocity(rotat.x, 0, rotat.z);
			break;
			case 1:
				addVelocity(-rotat.x, 0, -rotat.z);
			break;
			case 2:
				addVelocity(-rotat.z, 0, rotat.x);
			break;
			case 3:
				addVelocity(rotat.z, 0, -rotat.x);
			break;
			case 4:
				addVelocity(0, 0.5, 0);
			break;
			case 5:
			break;
			default:
				EndAllMagic.LOGGER.info("got " + direction);

		}

	}

	@Override
	public boolean getWindDash() {
		return windDashEnable;
	}
}
