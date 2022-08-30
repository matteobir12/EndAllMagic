package net.fabricmc.endallmagic.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.endallmagic.client.PageRegistryImpl;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * This is where pages and pagelayers are registered.
 * 
 * @author CleverNucleus
 *
 */
@Environment(EnvType.CLIENT)
public final class PageRegistry {
	
	/**
	 * Registers a page and tab to the PlayerEx screen.
	 * @param pageId unique - should be modid:name.
	 * @param texture the location of the background texture for the page.
	 * @param title the title of the page.
	 * @param icon the page's tab icon.
	 */
	public static void registerPage(final Identifier pageId, final Identifier icon, final Identifier texture, final Text title) {
		PageRegistryImpl.addPage(pageId, icon, texture, title);
	}
	
	/**
	 * Registers a page and tab, but with the default background.
	 * @param pageId
	 * @param title
	 * @param icon
	 */
	public static void registerPage(final Identifier pageId, final Identifier icon, final Text title) {
		PageRegistryImpl.addPage(pageId, icon, title);
	}
	
	/**
	 * Registers a page layer to a page of the same input pageId.
	 * @param pageId
	 * @param builder
	 */
	public static void registerLayer(final Identifier pageId, PageLayer.Builder builder) {
		PageRegistryImpl.addLayer(pageId, builder);
	}
}
