package net.fabricmc.endallmagic.mixin.client;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.jetbrains.annotations.Nullable;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.fabricmc.endallmagic.items.Staff;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.client.ClientUtils;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.network.ClientNetworking;
import net.fabricmc.endallmagic.common.spells.Pattern;
import net.fabricmc.endallmagic.common.spells.Spell;
import net.fabricmc.endallmagic.common.spells.SpellConfig;
import net.fabricmc.endallmagic.common.spells.SpellConfig.Affinity;

@Mixin(MinecraftClient.class)
public class ClientMixin implements ClientUtils {
	@Unique private int timer = 0;
	@Unique private Affinity affinity = Affinity.NONE;
	@Unique private final java.util.List<Pattern> pattern = new java.util.ArrayList<>(8);
	@Shadow	@Nullable public ClientPlayerEntity player;

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo info) {
		if(timer == 0 && !pattern.isEmpty())
			pattern.clear();

		if(player == null)
			return;

		if(player.getMainHandStack().getItem() instanceof Staff || player.getMainHandStack().getItem() == Items.STICK) {
			if(timer > 0) {
				if (!pattern.isEmpty()){
					MutableText hyphen = Text.literal("-").formatted(Formatting.GRAY);
					MutableText text = Text.literal(pattern.get(0).toString());
					for (int i =1;i<pattern.size();i++) text.append(hyphen).append(pattern.get(i).toString()).formatted(Formatting.GRAY);
					player.sendMessage(text, true);
				}
				if(pattern.size() > 3) {
					oshi.util.tuples.Pair<Spell,Boolean> p = ((MagicUser)player).getKnownSpells().getSpell(pattern);
					if(Boolean.TRUE.equals(p.getB())){
						if(p.getA() != null) {
								ClientNetworking.castSpellSend(EndAllMagic.SPELL.getRawId(p.getA()));
						}
					}
					else{
						pattern.clear();
						timer = 0;
					}
				}
			}
			else if(pattern.size() < 3 && !pattern.isEmpty())
				player.sendMessage(Text.literal(""), true);
		}
		else
			timer = 0;

		if(timer > 0)
			timer--;
	}

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doItemUse()V", ordinal = 0), cancellable = true)
	public void onRightClick(CallbackInfo info) {
		if(player != null && !player.isSpectator() && (player.getMainHandStack().getItem() instanceof Staff || player.getMainHandStack().getItem() == Items.STICK)) {
			timer = 20;
			pattern.add(Pattern.RIGHT);
			player.swingHand(Hand.MAIN_HAND);
			player.world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1.1F);
			info.cancel();
		}
	}

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z", ordinal = 0), cancellable = true)
	public void onLeftClick(CallbackInfo info) {
		if(player != null && !player.isSpectator() && (player.getMainHandStack().getItem() instanceof Staff || player.getMainHandStack().getItem() == Items.STICK)) {
			timer = 20;
			pattern.add(Pattern.LEFT);
			player.swingHand(Hand.MAIN_HAND);
			player.world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1.3F);
			info.cancel();
		}
	}

	public java.util.List<Pattern> getPattern() {
		return pattern;
	}

	public void setTimer(int value) {
		timer = value;
	}
	public void setAffinity(Affinity affinity){
		EndAllMagic.LOGGER.info("sdd");
		this.affinity = affinity;

	}
	public Affinity getAffinity(){
		return affinity;
	}
    
}
