package net.fabricmc.endallmagic.common.spells;

import java.util.Arrays;

import net.fabricmc.endallmagic.EndAllMagic;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SpellConfig {

    public static final SpellTree ENABLED_SPELLS = new SpellTree();
	//-----Spells-----//
    
    public static final java.util.List<Pattern> FIRE_PATTERN = new java.util.ArrayList<>(2);
    public static final java.util.List<Pattern> WATER_PATTERN = new java.util.ArrayList<>(2);
    public static final java.util.List<Pattern> EARTH_PATTERN = new java.util.ArrayList<>(2);
    public static final java.util.List<Pattern> WIND_PATTERN = new java.util.ArrayList<>(2);

    public static final Spell FIREBALL;
	public static final Spell HEAL;
	public static final Spell WINDBLADE;

    public enum Affinity {
		FIRE,
		WIND,
		EARTH,
		WATER,
        NONE
	}

    static{
        FIRE_PATTERN.addAll(Arrays.asList(Pattern.LEFT,Pattern.LEFT));
        WATER_PATTERN.addAll(Arrays.asList(Pattern.RIGHT,Pattern.RIGHT));
        EARTH_PATTERN.addAll(Arrays.asList(Pattern.LEFT,Pattern.RIGHT));
        WIND_PATTERN.addAll(Arrays.asList(Pattern.RIGHT,Pattern.LEFT));
		FIREBALL = new FireBall(); // use config constuctor and link to config
		HEAL = new HealSpell();
		WINDBLADE = new WindBladeSpell();
    }

    public static void register() {
		if(EndAllMagic.getConfig().spells.enableFireBall) {
			Registry.register(EndAllMagic.SPELL, new Identifier(EndAllMagic.MOD_ID, "fireball"), FIREBALL);
			ENABLED_SPELLS.addSpell(FIREBALL);
		}
		if(EndAllMagic.getConfig().spells.enableHeal){
			Registry.register(EndAllMagic.SPELL, new Identifier(EndAllMagic.MOD_ID, "heal"), HEAL);
			ENABLED_SPELLS.addSpell(HEAL);
		}
		if(EndAllMagic.getConfig().spells.enableWindBlade){
			Registry.register(EndAllMagic.SPELL, new Identifier(EndAllMagic.MOD_ID, "wind_blade"), WINDBLADE);
			ENABLED_SPELLS.addSpell(WINDBLADE);
		}
	}

}
