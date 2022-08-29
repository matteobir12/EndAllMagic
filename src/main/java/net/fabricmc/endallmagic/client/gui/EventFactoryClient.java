package net.fabricmc.endallmagic.client.gui;

import java.util.List;

import net.fabricmc.endallmagic.client.EndAllMagicClient;
import net.fabricmc.endallmagic.client.gui.widget.TabButtonWidget;
import net.fabricmc.endallmagic.common.ClientToServer;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public final class EventFactoryClient {
	public static void onScreenInit(MinecraftClient client, Screen screen, int width, int height) {
		if(screen instanceof InventoryScreen) {
			HandledScreen<?> handledScreen = (HandledScreen<?>)screen;
			MagicScreenData screenData = (MagicScreenData)screen;
			
			if(Screens.getButtons(screen) != null) {
				Screens.getButtons(screen).add(new TabButtonWidget(handledScreen, EndAllMagicClient.INVENTORY, 0, 0, -28, false, btn -> {}));
				List<Page> pages = screenData.pages();
				
				for(int i = 0; i < pages.size(); i++) {
					Page page = pages.get(i);
					int j = i + 1;
					int u = ((j % 5) * 29) + (j < 6 ? 0 : 3);
					int v = j < 6 ? -28 : 162;
					
					Screens.getButtons(screen).add(new TabButtonWidget(handledScreen, page, j, u, v, true, btn -> ClientToServer.openAttributesScreen(j - 1)));
				}
			}
		}
	}
	
	public static void onKeyPressed(MinecraftClient client) {
		while(EndAllMagicClient.keyBinding.wasPressed()) {
			if(client.currentScreen == null && !client.interactionManager.hasRidingInventory()) {
				ClientToServer.openAttributesScreen(0);
			}
		}
	}
}
