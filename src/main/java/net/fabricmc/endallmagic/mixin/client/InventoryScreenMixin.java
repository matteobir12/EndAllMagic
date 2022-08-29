package net.fabricmc.endallmagic.mixin.client;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.endallmagic.client.gui.widget.TabButtonWidget;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;

@Mixin(InventoryScreen.class)
abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
	private InventoryScreenMixin(PlayerEntity player, Text text) {
		super(player.playerScreenHandler, player.getInventory(), text);
	}
	
	private void endallmagic_forEachTab(Consumer<TabButtonWidget> consumer) {
		this.children().stream().filter(e -> e instanceof TabButtonWidget).forEach(e -> consumer.accept((TabButtonWidget)e));
	}
	
	@Inject(method = "render", at = @At("TAIL"))
	private void endallmagic_render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
		this.endallmagic_forEachTab(tab -> tab.renderTooltip(matrices, mouseX, mouseY));
	}
}
