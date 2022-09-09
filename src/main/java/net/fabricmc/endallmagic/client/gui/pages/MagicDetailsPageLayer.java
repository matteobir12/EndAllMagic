package net.fabricmc.endallmagic.client.gui.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.api.client.PageLayer;
import net.fabricmc.endallmagic.api.client.RenderComponent;
import net.fabricmc.endallmagic.client.EndAllMagicClient;
import net.fabricmc.endallmagic.client.gui.widget.ScreenButtonWidget;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.network.ClientNetworking;
import net.fabricmc.endallmagic.common.spells.SpellConfig;
import net.fabricmc.endallmagic.common.spells.SpellConfig.Affinity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MagicDetailsPageLayer extends PageLayer {
	private static Supplier<Float> scaleX = () -> EndAllMagic.getConfig().textScaleX();
	private static Supplier<Float> scaleY = () -> EndAllMagic.getConfig().textScaleY();
	private static float scaleZ = 0.75F;
	
	private static final List<RenderComponent> COMPONENTS = new ArrayList<>();
	private static final List<Identifier> AFF_BUTTON_KEYS = ImmutableList.of(SpellConfig.affinityToId(Affinity.FIRE),SpellConfig.affinityToId(Affinity.WIND),SpellConfig.affinityToId(Affinity.EARTH),SpellConfig.affinityToId(Affinity.WATER));
	private static final List<Identifier> BUTTON_KEYS = ImmutableList.of();
	private static final List<ScreenButtonWidget> AFF_BUTTONS = new ArrayList<>();

	
	public MagicDetailsPageLayer(HandledScreen<?> parent, ScreenHandler handler, PlayerInventory inventory, Text title) {
		super(parent, handler, inventory, title);
	}

	
	private void forEachScreenButton(Consumer<ScreenButtonWidget> consumer) {
		this.children().stream().filter(e -> e instanceof ScreenButtonWidget).forEach(e -> consumer.accept((ScreenButtonWidget)e));
	}
	
	private void nodeButtonPressed(ButtonWidget buttonIn) {
		ScreenButtonWidget button = (ScreenButtonWidget)buttonIn;
	}
	
	private void buttonTooltip(ButtonWidget buttonIn, MatrixStack matrices, int mouseX, int mouseY) {
		ScreenButtonWidget button = (ScreenButtonWidget)buttonIn;
		Text tooltip = (Text.translatable("endallmagic.gui.page.affinities")).formatted(Formatting.GRAY);
		this.renderTooltip(matrices, tooltip, mouseX, mouseY);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		matrices.push();
		matrices.scale(scaleX.get(), scaleY.get(), scaleZ);
		
		COMPONENTS.forEach(component -> component.renderText(this.client.player, matrices, this.textRenderer, this.x, this.y, scaleX.get(), scaleY.get()));
		
		this.textRenderer.draw(matrices, Text.translatable("endallmagic.gui.page.attributes.text.details").formatted(Formatting.DARK_GRAY), (this.x + 105) / scaleX.get(), (this.y + 26) / scaleY.get(), 4210752);
		this.textRenderer.draw(matrices, Text.translatable("endallmagic.gui.page.attributes.text.details").formatted(Formatting.DARK_GRAY), (this.x + 105) / scaleX.get(), (this.y + 81) / scaleY.get(), 4210752);
		
		matrices.pop();
		
		COMPONENTS.forEach(component -> component.renderTooltip(this.client.player, this::renderTooltip, matrices, this.textRenderer, this.x, this.y, mouseX, mouseY, scaleX.get(), scaleY.get()));
	}
	
	@Override
	public void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, EndAllMagicClient.GUI);
		this.drawTexture(matrices, this.x + 9, this.y + 35, 226, 0, 9, 9);
		this.drawTexture(matrices, this.x + 9, this.y + 123, 235, 0, 9, 9);
		this.drawTexture(matrices, this.x + 93, this.y + 24, 226, 9, 9, 9);
		this.drawTexture(matrices, this.x + 93, this.y + 79, 235, 9, 9, 9);
			
		if(((MagicUser)this.client.player).getAffinity() == SpellConfig.Affinity.NONE){
			RenderSystem.setShaderTexture(0, EndAllMagicClient.NO_AFF_BACKGROUND);
			this.drawTexture(matrices, x+18, y+40, 0, 0, 157, 49);
		}
		this.forEachScreenButton(button -> {
			Identifier key = button.key();
			if(BUTTON_KEYS.contains(key)) {
				button.active = true;
			}
		});
	}
	
	@Override
	protected void init() {
		super.init();
		if (((MagicUser)this.client.player).getAffinity() == SpellConfig.Affinity.NONE){
			for (int i = 0; i < AFF_BUTTON_KEYS.size();i++) {
				AFF_BUTTONS.add(new ScreenButtonWidget(this.parent, 8+(10*i), y+50, 204, 0, 11, 10, AFF_BUTTON_KEYS.get(i), this::affinityButtonPressed, this::buttonTooltip));
				this.addDrawableChild(AFF_BUTTONS.get(i));
			}
		}
	}
	private void affinityButtonPressed(ButtonWidget buttonIn) {
		ScreenButtonWidget button = (ScreenButtonWidget)buttonIn;
		Affinity affinity = SpellConfig.idToAffinity(button.key());
		((MagicUser)(this.client.player)).setAffinity(affinity);
		ClientNetworking.updateAffinitySend(affinity);
		AFF_BUTTONS.forEach(this::remove);
		init();
		// switch screen
	}
}
