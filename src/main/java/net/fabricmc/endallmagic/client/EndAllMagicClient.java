package net.fabricmc.endallmagic.client;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.api.client.PageRegistry;
import net.fabricmc.endallmagic.client.gui.AttributesPageLayer;
import net.fabricmc.endallmagic.client.gui.CombatPageLayer;
import net.fabricmc.endallmagic.client.gui.EventFactoryClient;
import net.fabricmc.endallmagic.client.gui.MagicScreen;
import net.fabricmc.endallmagic.client.gui.Page;
import net.fabricmc.endallmagic.common.particles.HealParticle;
import net.fabricmc.endallmagic.common.particles.ModParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EndAllMagicClient implements ClientModInitializer {
	public static final Identifier GUI = new Identifier(EndAllMagic.MOD_ID, "textures/gui/gui.png");
	public static final Identifier ATTRIBUTES_PAGE = new Identifier(EndAllMagic.MOD_ID, "attributes");
	public static final Identifier COMBAT_PAGE = new Identifier(EndAllMagic.MOD_ID, "combat");
	public static final Page INVENTORY = new Page(new Identifier(EndAllMagic.MOD_ID, "inventory"), new Identifier(EndAllMagic.MOD_ID, "textures/gui/inventory.png"), Text.translatable("endallmagic.gui.page.inventory.title"));
	public static KeyBinding keyBinding;
	
	@Override
	public void onInitializeClient() {
		// ClientLoginNetworking.registerGlobalReceiver(NetworkFactory.CONFIG, NetworkFactoryClient::loginQueryReceived); when client logs in recieves config data from server
		// ClientPlayNetworking.registerGlobalReceiver(NetworkFactory.NOTIFY, NetworkFactoryClient::notifiedLevelUp); when client recieves from the server that they leveled up
		
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("endallmagic.key.screen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.inventory"));
		HandledScreens.register(EndAllMagic.MAGIC_SCREEN, MagicScreen::new);
		PageRegistry.registerPage(ATTRIBUTES_PAGE, new Identifier(EndAllMagic.MOD_ID, "textures/gui/attributes.png"), Text.translatable("endallmagic.gui.page.attributes.title"));
		PageRegistry.registerPage(COMBAT_PAGE, new Identifier(EndAllMagic.MOD_ID, "textures/gui/combat.png"), Text.translatable("endallmagic.gui.page.combat.title"));
		PageRegistry.registerLayer(ATTRIBUTES_PAGE, AttributesPageLayer::new);
		PageRegistry.registerLayer(COMBAT_PAGE, CombatPageLayer::new);
		particleFactoryRegistry();
		
		ScreenEvents.AFTER_INIT.register(EventFactoryClient::onScreenInit);
		ClientTickEvents.END_CLIENT_TICK.register(EventFactoryClient::onKeyPressed);
	}
	public void particleFactoryRegistry() {
		ParticleFactoryRegistry.getInstance().register(ModParticles.HEAL, HealParticle.Factory::new);

	}
}
