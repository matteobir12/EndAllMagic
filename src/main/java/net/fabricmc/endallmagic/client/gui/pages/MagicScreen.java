package net.fabricmc.endallmagic.client.gui.pages;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.api.client.PageLayer;
import net.fabricmc.endallmagic.client.EndAllMagicClient;
import net.fabricmc.endallmagic.client.PageRegistryImpl;
import net.fabricmc.endallmagic.client.gui.MagicScreenData;
import net.fabricmc.endallmagic.client.gui.MagicScreenFactory;
import net.fabricmc.endallmagic.client.gui.widget.TabButtonWidget;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.network.ClientNetworking;
import net.fabricmc.endallmagic.common.network.ServerNetworking;
import net.fabricmc.endallmagic.common.spells.SpellConfig.Affinity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MagicScreen extends AbstractInventoryScreen<MagicScreenFactory.Handler> {
	private int tab = 0;
	
	public MagicScreen(MagicScreenFactory.Handler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
		this.tab = screenHandler.pageId;
		this.getPages().forEach(page -> this.addLayers(page, screenHandler, playerInventory, text));
	}
	
	private List<Page> getPages() {
		return ((MagicScreenData)this).pages();
	}
	
	private void addLayers(Page page, MagicScreenFactory.Handler screenhandler, PlayerInventory playerInventory, Text text) {
		for(PageLayer.Builder builder : PageRegistryImpl.findPageLayers(page.id())) {
			page.addLayer(builder.build(this, screenhandler, playerInventory, text));
		}
	}
	
	private Page currentPage() {
		int index = MathHelper.clamp(this.tab, 0, this.getPages().size() - 1);
		return this.getPages().get(index);
	}
	
	private void forEachButton(Consumer<ButtonWidget> consumer) {
		this.children().stream().filter(e -> e instanceof ButtonWidget).forEach(e -> consumer.accept((ButtonWidget)e));
	}
	
	private void forEachTab(Consumer<TabButtonWidget> consumer) {
		this.children().stream().filter(e -> e instanceof TabButtonWidget).forEach(e -> consumer.accept((TabButtonWidget)e));
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(EndAllMagicClient.keyBinding.matchesKey(keyCode, scanCode)) {
			this.close();
		}
		
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.currentPage().forEachLayer(layer -> layer.render(matrices, mouseX, mouseY, delta));
		this.forEachButton(button -> button.renderTooltip(matrices, mouseX, mouseY));
	}
	
	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		int u = this.x;
		int v = (this.height - this.backgroundHeight) / 2;
		
		RenderSystem.setShaderTexture(0, this.currentPage().texture());
		this.drawTexture(matrices, u + 6, v + 6, 0, 0, this.backgroundWidth - 12, this.backgroundWidth - 12);
		
		RenderSystem.setShaderTexture(0, EndAllMagicClient.GUI);
		this.drawTexture(matrices, u, v, 0, 0, this.backgroundWidth, this.backgroundWidth);
		this.currentPage().forEachLayer(layer -> layer.drawBackground(matrices, delta, mouseX, mouseY));
		this.forEachButton(button -> button.render(matrices, mouseX, mouseY, delta));
	}
	
	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		this.textRenderer.draw(matrices, this.currentPage().title().copy().formatted(Formatting.DARK_GRAY),this.titleX, (this.titleY + 2), 4210752);
	}
	
	@Override
	protected void init() {
		super.init();
		this.clearChildren();
		this.addDrawableChild(new TabButtonWidget(this, EndAllMagicClient.INVENTORY, 0, 0, -28, true,ClientNetworking::openInventoryScreen));
		
		for(int i = 0; i < this.getPages().size(); i++) {
			Page page = this.getPages().get(i);
			int j = i + 1;
			int u = ((j % 5) * 29) + (j < 6 ? 0 : 3);
			int v = j < 6 ? -28 : 162;
			
			this.addDrawableChild(new TabButtonWidget(this, page, j, u, v, true, btn -> {
				TabButtonWidget button = (TabButtonWidget)btn;
				this.tab = button.index() - 1;

				this.forEachTab(tab -> tab.active = true);
				button.active = false;
				this.init();
			}));
		}
		
		this.forEachTab(tab -> {
			if(tab.index() - 1 == this.tab) {
				tab.active = false;
			}
		});
		
		this.currentPage().forEachLayer(layer -> {
			layer.init(this.client, this.width, this.height);
			layer.children().stream().filter(ButtonWidget.class::isInstance).forEach(e -> this.addDrawableChild((ButtonWidget)e));
		});
	}
	public void setTab(int tab) {
		this.tab = tab;
	}
}