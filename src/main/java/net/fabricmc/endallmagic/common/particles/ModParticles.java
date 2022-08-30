package net.fabricmc.endallmagic.common.particles;

import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;

public class ModParticles {
	//-----Particle Map-----//
	public static final LinkedHashMap<ParticleType<?>, Identifier> PARTICLE_TYPES = new LinkedHashMap<>();

	//-----Particles-----//
	public static final ParticleType<DefaultParticleType> HEAL = create("heal", FabricParticleTypes.simple());

	//-----Registry-----//
	public static void register() {
		PARTICLE_TYPES.keySet().forEach(particleType -> Registry.register(Registry.PARTICLE_TYPE, PARTICLE_TYPES.get(particleType), particleType));
	}

	private static <T extends ParticleEffect> ParticleType<T> create(String name, ParticleType<T> type) {
		PARTICLE_TYPES.put(type, new Identifier(EndAllMagic.MOD_ID, name));
		return type;
	}
}
