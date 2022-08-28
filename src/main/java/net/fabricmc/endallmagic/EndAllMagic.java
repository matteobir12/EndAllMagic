package net.fabricmc.endallmagic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.endallmagic.common.Networking;
import net.fabricmc.endallmagic.common.spells.Spell;
import net.fabricmc.endallmagic.items.Staff;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.integration.EndAllMagicConfig;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class EndAllMagic implements ModInitializer {
	// custom reg public static final Registry<Spell> SPELL = createRegistry("spell", Spell.class);

	public static final String MOD_ID = "endallmagic";

	public static final Registry<Spell> SPELL = FabricRegistryBuilder.createSimple(Spell.class, new Identifier(MOD_ID, "spell")).buildAndRegister();

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Staff STAFF = new Staff(new FabricItemSettings().group(ItemGroup.MISC));
	@Override
	public void onInitialize() {
		AutoConfig.register(EndAllMagicConfig.class, JanksonConfigSerializer::new);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "staff"), STAFF);

		ServerPlayNetworking.registerGlobalReceiver(Networking.ID, Networking::handle);

		Registry.register(Registry.ATTRIBUTE, new Identifier(MOD_ID, "mana_regen"), EntityAttributes.MANA_REGEN);

		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}

	public static class DataTrackers {
        public static final TrackedData<Integer> MANA = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
		public static final TrackedData<Boolean> SHOW_MANA = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		private DataTrackers(){};
	}
	public static class EntityAttributes {
		public static final EntityAttribute MANA_COST = new ClampedEntityAttribute("attribute.name.generic." + MOD_ID + ".mana_cost", 1D, 0D, 1024D).setTracked(true);
		public static final EntityAttribute MANA_REGEN = new ClampedEntityAttribute("attribute.name.generic." + MOD_ID + ".mana_regen", 1D, 0D, 1024D).setTracked(true);
		private EntityAttributes(){};
	}

}
