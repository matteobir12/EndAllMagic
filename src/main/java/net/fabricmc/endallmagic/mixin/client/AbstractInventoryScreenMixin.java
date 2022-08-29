package net.fabricmc.endallmagic.mixin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.endallmagic.client.EndAllMagicClient;
import net.fabricmc.endallmagic.client.PageRegistryImpl;
import net.fabricmc.endallmagic.client.gui.MagicScreenData;
import net.fabricmc.endallmagic.client.gui.Page;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(AbstractInventoryScreen.class)
abstract class AbstractInventoryScreenMixin<T extends ScreenHandler> extends HandledScreen<T> implements MagicScreenData {
	
	@Unique
	private List<Page> endallmagic_pages = new ArrayList<>();
	
	private AbstractInventoryScreenMixin(T handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}
	
	private boolean filter(Map.Entry<Identifier, Supplier<Page>> entry) {
		Identifier key = entry.getKey();
		return !(key.equals(EndAllMagicClient.ATTRIBUTES_PAGE) || key.equals(EndAllMagicClient.COMBAT_PAGE));
	}
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void endallmagic_init(T screenHandler, PlayerInventory playerInventory, Text text, CallbackInfo info) {
		this.endallmagic_pages.add(0, PageRegistryImpl.findPage(EndAllMagicClient.ATTRIBUTES_PAGE));
		this.endallmagic_pages.add(1, PageRegistryImpl.findPage(EndAllMagicClient.COMBAT_PAGE));
		PageRegistryImpl.pages().entrySet().stream().filter(this::filter).map(Map.Entry::getValue).forEach(page -> this.endallmagic_pages.add(page.get()));
	}
	
	@Override
	public int getX() {
		return this.x;
	}
	
	@Override
	public int getY() {
		return this.y;
	}
	
	@Override
	public List<Page> pages() {
		return this.endallmagic_pages;
	}
}
