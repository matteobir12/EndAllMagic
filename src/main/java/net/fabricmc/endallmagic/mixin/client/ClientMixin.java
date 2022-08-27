package net.fabricmc.endallmagic.mixin.client;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;
import org.jetbrains.annotations.Nullable;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.fabricmc.endallmagic.items.Staff;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.fabricmc.endallmagic.common.Pattern;
import net.fabricmc.endallmagic.common.spells.Spell;
import net.fabricmc.endallmagic.common.spells.Spells;


public class ClientMixin  {
    
	@Unique private int timer = 0;
	@Unique private final java.util.List<Pattern> pattern = new java.util.ArrayList<>(3);
	@Shadow	@Nullable public ClientPlayerEntity player;

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo info) {
		if(timer == 0 && !pattern.isEmpty())
			pattern.clear();

		if(player == null)
			return;

		if(player.getMainHandStack().getItem() instanceof Staff) {
			if(timer > 0) {
				MutableText hyphen = Text.literal("-").formatted(Formatting.GRAY);

				player.sendMessage((Text.literal(pattern.get(0).toString()).formatted(Formatting.GRAY)).append(hyphen).append((Text.literal(pattern.get(1).toString()).formatted(Formatting.GRAY))).append(hyphen).append((Text.literal(pattern.get(2).toString()).formatted(Formatting.GRAY))), true);

				if(pattern.size() > 3) {
					Spell cast = Spells.lookupSpell(player, pattern);
					if(cast != null) {
							cast.attemptCast();
						}
					

					timer = 0;
				}
			}
			else if(pattern.size() < 3 && unfinishedSpell)
				player.sendMessage(Text.literal(""), true);
		}
		else
			timer = 0;

		if(timer > 0)
			timer--;
	}

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doItemUse()V", ordinal = 0), cancellable = true)
	public void onRightClick(CallbackInfo info) {
		if(player != null && !player.isSpectator() && player.getMainHandStack().getItem() instanceof WandItem) {
			timer = 20;
			unfinishedSpell = true;
			pattern.add(Pattern.RIGHT);
			player.swingHand(Hand.MAIN_HAND);
			player.world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1.1F);
			info.cancel();
		}
	}

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z", ordinal = 0), cancellable = true)
	public void onLeftClick(CallbackInfo info) {
		if(player != null && !player.isSpectator() && player.getMainHandStack().getItem() instanceof WandItem) {
			timer = 20;
			unfinishedSpell = true;
			pattern.add(Pattern.LEFT);
			player.swingHand(Hand.MAIN_HAND);
			player.world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1.3F);
			info.cancel();
		}
	}

	@Override
	public List<Pattern> getPattern() {
		return pattern;
	}

	@Override
	public void setTimer(int value) {
		timer = value;
	}

	@Override
	public void setUnfinishedSpell(boolean value) {
		unfinishedSpell = value;
	}
    
}
