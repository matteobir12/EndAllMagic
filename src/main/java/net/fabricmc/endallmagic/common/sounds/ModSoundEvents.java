package net.fabricmc.endallmagic.common.sounds;

import net.fabricmc.endallmagic.EndAllMagic;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModSoundEvents {
	//-----Sound Map-----//
	public static final Map<SoundEvent, Identifier> SOUNDS = new LinkedHashMap<>();

	//-----Sound Events-----//
	public static final SoundEvent HEAL = create("heal");

	//-----Registry-----//
	public static void register() {
		SOUNDS.keySet().forEach(sound -> Registry.register(Registry.SOUND_EVENT, SOUNDS.get(sound), sound));
	}

	private static SoundEvent create(String name) {
		Identifier id = new Identifier(EndAllMagic.MOD_ID, name);
		SoundEvent sound = new SoundEvent(id);
		SOUNDS.put(sound, id);
		return sound;
	}
}
