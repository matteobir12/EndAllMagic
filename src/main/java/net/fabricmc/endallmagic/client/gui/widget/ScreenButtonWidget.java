package net.fabricmc.endallmagic.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.endallmagic.client.EndAllMagicClient;
import net.fabricmc.endallmagic.client.gui.MagicScreenData;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ScreenButtonWidget extends ButtonWidget {
	public static final Identifier EMPTY_KEY = new Identifier("endallmagic:empty");
	private HandledScreen<?> parent;
	private final Identifier key;
	private int u, v, dx, dy;
	public boolean alt;
	
	public ScreenButtonWidget(HandledScreen<?> parent, int x, int y, int u, int v, int width, int height, Identifier key, PressAction pressAction, TooltipSupplier tooltipSupplier) {
		super(x, y, width, height, Text.empty(), pressAction, tooltipSupplier);
		
		this.parent = parent;
		this.key = key;
		this.u = u;
		this.v = v;
		this.dx = x;
		this.dy = y;
		this.alt = false;
	}
	
	public ScreenButtonWidget(HandledScreen<?> parent, int x, int y, int u, int v, int width, int height, PressAction pressAction, TooltipSupplier tooltipSupplier) {
		this(parent, x, y, u, v, width, height, EMPTY_KEY, pressAction, tooltipSupplier);
	}
	
	public ScreenButtonWidget(HandledScreen<?> parent, int x, int y, int u, int v, int width, int height, PressAction pressAction) {
		this(parent, x, y, u, v, width, height, EMPTY_KEY, pressAction, EMPTY);
	}
	
	public Identifier key() {
		return this.key;
	}
	
	@Override
	public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
		if(this.isHovered()) {
			this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
		}
	}
	
	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MagicScreenData handledScreen = (MagicScreenData)this.parent;
		this.x = handledScreen.getX() + this.dx;
		this.y = handledScreen.getY() + this.dy;
		
		RenderSystem.setShaderTexture(0, EndAllMagicClient.GUI);
		RenderSystem.disableDepthTest();
		
		int i = this.u;
		int j = this.v;
		
		if(this.alt) {
			i += this.width;
		}
		
		if(this.active) {
			if(this.isHovered()) {
				j += this.height;
			}
		} else {
			j += (2 * this.height);
		}
		
		this.drawTexture(matrices, this.x, this.y, i, j, this.width, this.height);
		
		RenderSystem.enableDepthTest();
	}
}
