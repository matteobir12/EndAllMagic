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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.common.Networking;
import net.fabricmc.endallmagic.common.Pattern;
import net.fabricmc.endallmagic.common.spells.Spell;
import net.fabricmc.endallmagic.common.spells.SpellTree;

@Mixin(MinecraftClient.class)
public class ClientMixin  {
	@Unique private SpellTree knownSpells = new SpellTree();
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
				MutableText text;
				for (Pattern p: pattern ) text.append(p.toString()).formatted(Formatting.GRAY).append(hyphen);
				if (!pattern.isEmpty()) player.sendMessage(text, true);

				if(pattern.size() > 3) {
					oshi.util.tuples.Pair<Spell,Boolean> p = knownSpells.getSpell(pattern);
					if(Boolean.TRUE.equals(p.getB())){
						if(p.getA() !=null) {
							Networking.send(EndAllMagic.SPELL.getRawId(p.getA()));
						}
						
					}
					else{
						pattern.clear();
						timer = 0;
					}
					

					timer = 0;
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
		EndAllMagic.LOGGER.info("right Clicking");
		if(player != null && !player.isSpectator() && player.getMainHandStack().getItem() instanceof Staff) {
			EndAllMagic.LOGGER.info("with staff");
			timer = 20;
			pattern.add(Pattern.RIGHT);
			player.swingHand(Hand.MAIN_HAND);
			player.world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1.1F);
			info.cancel();
		}
	}

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z", ordinal = 0), cancellable = true)
	public void onLeftClick(CallbackInfo info) {
		EndAllMagic.LOGGER.info("left Clicking");
		if(player != null && !player.isSpectator() && player.getMainHandStack().getItem() instanceof Staff) {
			EndAllMagic.LOGGER.info("with staff");
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
    
}
