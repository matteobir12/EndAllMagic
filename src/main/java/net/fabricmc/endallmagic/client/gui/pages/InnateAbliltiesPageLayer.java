package net.fabricmc.endallmagic.client.gui.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.api.client.PageLayer;
import net.fabricmc.endallmagic.api.client.RenderComponent;
import net.fabricmc.endallmagic.client.EndAllMagicClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class InnateAbliltiesPageLayer extends PageLayer {
	private static Supplier<Float> scaleX = () -> EndAllMagic.getConfig().textScaleX();
	private static Supplier<Float> scaleY = () -> EndAllMagic.getConfig().textScaleY();
	private static float scaleZ = 0.75F;
	
	private static final List<RenderComponent> COMPONENTS = new ArrayList<>();
	
	public InnateAbliltiesPageLayer(HandledScreen<?> parent, ScreenHandler handler, PlayerInventory inventory, Text title) {
		super(parent, handler, inventory, title);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		matrices.push();
		matrices.scale(scaleX.get(), scaleY.get(), scaleZ);
		
		COMPONENTS.forEach(component -> component.renderText(this.client.player, matrices, this.textRenderer, this.x, this.y, scaleX.get(), scaleY.get()));
		
		this.textRenderer.draw(matrices, Text.translatable("endallmagic.gui.page.combat.text.melee").formatted(Formatting.DARK_GRAY), (this.x + 21) / scaleX.get(), (this.y + 26) / scaleY.get(), 4210752);
		this.textRenderer.draw(matrices, Text.translatable("endallmagic.gui.page.combat.text.defense").formatted(Formatting.DARK_GRAY), (this.x + 21) / scaleX.get(), (this.y + 92) / scaleY.get(), 4210752);
		this.textRenderer.draw(matrices, Text.translatable("endallmagic.gui.page.combat.text.ranged").formatted(Formatting.DARK_GRAY), (this.x + 105) / scaleX.get(), (this.y + 26) / scaleY.get(), 4210752);
		this.textRenderer.draw(matrices, (Text.translatable("endallmagic.gui.page.combat.text.melee")).formatted(Formatting.DARK_GRAY), (this.x + 21) / scaleX.get(), (this.y + 26) / scaleY.get(), 4210752);
		this.textRenderer.draw(matrices, (Text.translatable("endallmagic.gui.page.combat.text.defense")).formatted(Formatting.DARK_GRAY), (this.x + 21) / scaleX.get(), (this.y + 92) / scaleY.get(), 4210752);
		this.textRenderer.draw(matrices, (Text.translatable("endallmagic.gui.page.combat.text.ranged")).formatted(Formatting.DARK_GRAY), (this.x + 105) / scaleX.get(), (this.y + 26) / scaleY.get(), 4210752);
		
		matrices.pop();
		
		COMPONENTS.forEach(component -> component.renderTooltip(this.client.player, this::renderTooltip, matrices, this.textRenderer, this.x, this.y, mouseX, mouseY, scaleX.get(), scaleY.get()));
	}
	
	@Override
	public void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, EndAllMagicClient.GUI);
		this.drawTexture(matrices, this.x + 9, this.y + 24, 244, 9, 9, 9);
		this.drawTexture(matrices, this.x + 9, this.y + 90, 226, 18, 9, 9);
		this.drawTexture(matrices, this.x + 93, this.y + 24, 235, 18, 9, 9);
	}
}
