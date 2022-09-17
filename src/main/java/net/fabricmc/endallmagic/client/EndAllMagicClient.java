package net.fabricmc.endallmagic.client;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.api.client.PageRegistry;
import net.fabricmc.endallmagic.client.entities.TornadoEntityRenderer;
import net.fabricmc.endallmagic.client.entities.WindBladeEntityRenderer;
import net.fabricmc.endallmagic.client.gui.EventFactoryClient;
import net.fabricmc.endallmagic.client.gui.pages.InnateAbliltiesPageLayer;
import net.fabricmc.endallmagic.client.gui.pages.MagicDetailsPageLayer;
import net.fabricmc.endallmagic.client.gui.pages.MagicScreen;
import net.fabricmc.endallmagic.client.gui.pages.Page;
import net.fabricmc.endallmagic.common.entities.ModEntities;
import net.fabricmc.endallmagic.common.network.ClientNetworking;
import net.fabricmc.endallmagic.common.network.ServerNetworking;
import net.fabricmc.endallmagic.common.particles.HealParticle;
import net.fabricmc.endallmagic.common.particles.ModParticles;
import net.fabricmc.endallmagic.common.particles.WindBladeParticle;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EndAllMagicClient implements ClientModInitializer {
	public static final Identifier GUI = new Identifier(EndAllMagic.MOD_ID, "textures/gui/gui.png");
	public static final Identifier MDTA = new Identifier(EndAllMagic.MOD_ID, "textures/gui/magic_details_tree_a.png");
	public static final Identifier MDTB = new Identifier(EndAllMagic.MOD_ID, "textures/gui/magic_details_tree_b.png");
	public static final Identifier MDTC = new Identifier(EndAllMagic.MOD_ID, "textures/gui/magic_details_tree_c.png");
	public static final Identifier MDTD = new Identifier(EndAllMagic.MOD_ID, "textures/gui/magic_details_tree_d.png");
	public static final Identifier NO_AFF_BACKGROUND = new Identifier(EndAllMagic.MOD_ID, "textures/gui/pick_affinity_background.png");
	public static final Identifier MAGIC_DETAILS = new Identifier(EndAllMagic.MOD_ID, "details");
	public static final Identifier INNATE_ABLILITIES = new Identifier(EndAllMagic.MOD_ID, "abilities");
	public static final Page INVENTORY = new Page(new Identifier(EndAllMagic.MOD_ID, "inventory"), new Identifier(EndAllMagic.MOD_ID, "textures/gui/inventory.png"), Text.translatable("endallmagic.gui.page.inventory.title"));
	public static KeyBinding keyBinding;
	
	@Override
	public void onInitializeClient() {
		// ClientLoginNetworking.registerGlobalReceiver(NetworkFactory.CONFIG, NetworkFactoryClient::loginQueryReceived); when client logs in recieves config data from server
		ClientPlayNetworking.registerGlobalReceiver(ServerNetworking.ADDSPELL, ClientNetworking::receiveKnownSpell);
		
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("endallmagic.key.screen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.inventory"));
		HandledScreens.register(EndAllMagic.MAGIC_SCREEN, MagicScreen::new);
		PageRegistry.registerPage(MAGIC_DETAILS, new Identifier(EndAllMagic.MOD_ID, "textures/gui/magic_details.png"), Text.translatable("endallmagic.gui.page.magic_details.title"));
		PageRegistry.registerPage(INNATE_ABLILITIES, new Identifier(EndAllMagic.MOD_ID, "textures/gui/attributes.png"), Text.translatable("endallmagic.gui.page.innate.title"));
		PageRegistry.registerLayer(MAGIC_DETAILS, MagicDetailsPageLayer::new);
		PageRegistry.registerLayer(INNATE_ABLILITIES, InnateAbliltiesPageLayer::new);
		particleFactoryRegistry();
		entityRendererRegistry();
		MagicHud.clientEvents();
		
		ScreenEvents.AFTER_INIT.register(EventFactoryClient::onScreenInit);
		ClientTickEvents.END_CLIENT_TICK.register(EventFactoryClient::onKeyPressed);
	}
	public void particleFactoryRegistry() {
		ParticleFactoryRegistry.getInstance().register(ModParticles.HEAL, HealParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.WIND_BLADE, WindBladeParticle.Factory::new);

	}
	public void entityRendererRegistry() {
		EntityRendererRegistry.register(ModEntities.WIND_BLADE_ENTITY, WindBladeEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.TORNADO_ENTITY, TornadoEntityRenderer::new);
	}
}
