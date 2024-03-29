package net.fabricmc.endallmagic.mixin.client;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.fabricmc.endallmagic.items.Staff;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
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

@Mixin(MinecraftClient.class)
public class ClientMixin implements ClientUtils {
	@Unique private int spellTimer = 0;
	@Unique private int dashTimer = 0;
	@Unique private int lastKeyPressed = -1;
	@Unique private final java.util.List<Pattern> pattern = new java.util.ArrayList<>(8);
	@Shadow	@Nullable public ClientPlayerEntity player;

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo info) {
		if(spellTimer == 0 && !pattern.isEmpty())
			pattern.clear();

		if(player == null)
			return;

		if(player.getMainHandStack().getItem() instanceof Staff || player.getMainHandStack().getItem() == Items.STICK) {
			if(spellTimer > 0) {
				if (!pattern.isEmpty()){
					MutableText hyphen = Text.literal("-").formatted(Formatting.GRAY);
					MutableText text = Text.literal(pattern.get(0).toString());
					for (int i =1;i<pattern.size();i++) text.append(hyphen).append(pattern.get(i).toString()).formatted(Formatting.GRAY);
					player.sendMessage(text, true);
				}
				if(pattern.size() > 3) {
					oshi.util.tuples.Pair<Spell,Boolean> p = SpellConfig.ENABLED_SPELLS.getSpell(pattern); // is rightclick on a different thread? if so potential bug?
					if(Boolean.TRUE.equals(p.getB())){
						if(p.getA() != null) {
								ClientNetworking.castSpellSend(EndAllMagic.SPELL.getRawId(p.getA()));
								pattern.clear();
								spellTimer = 0;
						}
					}else{
						player.sendMessage(Text.translatable("error." + EndAllMagic.MOD_ID + ".unknown_spell").formatted(Formatting.RED), true);
						pattern.clear();
						spellTimer = 0;
					}
				}
			}
			else if(pattern.size() < 3 && !pattern.isEmpty())
				player.sendMessage(Text.literal(""), true);
		}
		else
			spellTimer = 0;

		if(spellTimer > 0)
			spellTimer--;
		
		if (dashTimer > 0)
			dashTimer--;
		if (((MagicUser)player).getWindDash()){
			MinecraftClient client = MinecraftClient.getInstance();
			List<Boolean> f = Arrays.asList(client.options.forwardKey.isPressed(),client.options.backKey.isPressed(),client.options.leftKey.isPressed(),client.options.rightKey.isPressed(),client.options.jumpKey.isPressed());
			for (int i=0;i<f.size(); i++){
				if (Boolean.TRUE.equals(f.get(i))) {
					if (dashTimer < 14 && dashTimer > 0 && i==lastKeyPressed) {
						((MagicUser)player).windDashDirection(i);
						lastKeyPressed = -1;
						dashTimer = 0;
					}else {
						lastKeyPressed = i;
					}
					dashTimer = 15;

				}
			}
		}
	}

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doItemUse()V", ordinal = 0), cancellable = true)
	public void onRightClick(CallbackInfo info) {
		if(player != null && !player.isSpectator() && (player.getMainHandStack().getItem() instanceof Staff || player.getMainHandStack().getItem() == Items.STICK)) {
			spellTimer = 20;
			pattern.add(Pattern.RIGHT);
			player.swingHand(Hand.MAIN_HAND);
			player.world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1.1F);
			info.cancel();
		}
	}

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z", ordinal = 0), cancellable = true)
	public void onLeftClick(CallbackInfo info) {
		if(player != null && !player.isSpectator() && (player.getMainHandStack().getItem() instanceof Staff || player.getMainHandStack().getItem() == Items.STICK)) {
			spellTimer = 20;
			pattern.add(Pattern.LEFT);
			player.swingHand(Hand.MAIN_HAND);
			player.world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1.3F);
			info.cancel();
		}
	}
	public void setTimer(int value) {
		spellTimer = value;
	}
}
