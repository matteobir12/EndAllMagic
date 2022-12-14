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
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MagicDetailsPageLayer extends PageLayer {
	private static Supplier<Float> scaleX = () -> EndAllMagic.getConfig().textScaleX();
	private static Supplier<Float> scaleY = () -> EndAllMagic.getConfig().textScaleY();
	private static float scaleZ = 0.75F;
	private int xOffset = 210;
	private int yOffset = 210;
	
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
		
		matrices.pop();
		
		COMPONENTS.forEach(component -> component.renderTooltip(this.client.player, this::renderTooltip, matrices, this.textRenderer, this.x, this.y, mouseX, mouseY, scaleX.get(), scaleY.get()));
	}
	
	@Override
	public void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		if (xOffset < 256 && yOffset < 256){
			RenderSystem.setShaderTexture(0, EndAllMagicClient.MDTA);
			this.drawTexture(matrices, this.x + 7, this.y + 7, xOffset, yOffset, 255-xOffset > 163 ? 163: 255-xOffset, 255-yOffset>153 ? 153 : 255-yOffset);
		}
		if (xOffset+163 > 255 && yOffset < 256){
			RenderSystem.setShaderTexture(0, EndAllMagicClient.MDTB);
			this.drawTexture(matrices, this.x + 7 + xOffset -256, this.y + 7, xOffset<163?0:163-xOffset, yOffset, xOffset -256 > 163 ? 163: xOffset -256,255-yOffset>153 ? 153 : 255-yOffset);
		}
		if (xOffset < 256 && yOffset+153 > 255){
			RenderSystem.setShaderTexture(0, EndAllMagicClient.MDTC);
			this.drawTexture(matrices, this.x + 7, this.y + 7 + yOffset -256, xOffset, yOffset-256>153?yOffset-256-153:0, backgroundWidth,backgroundHeight);
		}
		if (xOffset+163 > 255&& yOffset+153 > 255){
			RenderSystem.setShaderTexture(0, EndAllMagicClient.MDTD);
			this.drawTexture(matrices, this.x + 18, this.y + 35, xOffset, yOffset, backgroundWidth,backgroundHeight);
		}



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
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		EndAllMagic.LOGGER.info("dragged");
		xOffset+=deltaX;
		xOffset = MathHelper.clamp(xOffset, 0, 512-163);
		yOffset+=deltaY;
		yOffset = MathHelper.clamp(yOffset, 0, 512-153);
		EndAllMagic.LOGGER.info(xOffset +" "+  yOffset);
		return true;
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
	static{
	}
}
