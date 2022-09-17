package net.fabricmc.endallmagic.common.entities;

import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;


public class ModEntities {
	//-----Entity Map-----//
	public static final Map<EntityType<?>, Identifier> ENTITIES = new LinkedHashMap<>();

	//-----Entities-----//
	public static final EntityType<WindBladeEntity> WIND_BLADE_ENTITY = create("wind_blade",
			FabricEntityTypeBuilder.<WindBladeEntity>create(SpawnGroup.MISC, WindBladeEntity::new)
					.dimensions(EntityDimensions.fixed(0.5F, 0.5F))
					.trackRangeChunks(64)
					.build());
	public static final EntityType<TornadoEntity> TORNADO_ENTITY = create("tornado",
			FabricEntityTypeBuilder.<TornadoEntity>create(SpawnGroup.MISC, TornadoEntity::new)
					.dimensions(EntityDimensions.fixed(2F, 6F))
					.trackRangeChunks(64)
					.build());
					
	//-----Registry-----//
	public static void register() {
		ENTITIES.keySet().forEach(entityType -> Registry.register(Registry.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
	}

	private static <T extends Entity> EntityType<T> create(String name, EntityType<T> type) {
		ENTITIES.put(type, new Identifier(EndAllMagic.MOD_ID, name));
		return type;
	}
}
