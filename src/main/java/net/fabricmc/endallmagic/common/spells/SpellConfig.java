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
	public static final Spell WINDSPEED;
	public static final Spell FIRERESIST;
	public static final Spell TORNADO;
	public static final Spell ROCKWALL;

    public enum Affinity {
		FIRE {
			@Override
			public String toString() {
				return "Fire";
			}
		},
		WIND {
			@Override
			public String toString() {
				return "Wind";
			}
		},
		EARTH {
			@Override
			public String toString() {
				return "Earth";
			}
		},
		WATER {
			@Override
			public String toString() {
				return "Water";
			}
		},
        NONE {
			@Override
			public String toString() {
				return "None";
			}
		}
	}
	public static Identifier affinityToId(Affinity a) {
		return new Identifier(EndAllMagic.MOD_ID,String.valueOf(a.ordinal()));
	}
	public static Affinity idToAffinity(Identifier id) {
		return Affinity.values()[Integer.parseInt(id.getPath())];
	}

    static{
        FIRE_PATTERN.addAll(Arrays.asList(Pattern.LEFT,Pattern.LEFT));
        WATER_PATTERN.addAll(Arrays.asList(Pattern.RIGHT,Pattern.RIGHT));
        EARTH_PATTERN.addAll(Arrays.asList(Pattern.LEFT,Pattern.RIGHT));
        WIND_PATTERN.addAll(Arrays.asList(Pattern.RIGHT,Pattern.LEFT));
		FIREBALL = new FireBall(); // use config constuctor and link to config
		HEAL = new HealSpell();
		WINDBLADE = new WindBladeSpell();
		WINDSPEED = new WindSpeed();
		FIRERESIST = new FireResistSpell();
		TORNADO = new Tornado();
		ROCKWALL = new RockWall();
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
		if(EndAllMagic.getConfig().spells.enableWindSpeed){
			Registry.register(EndAllMagic.SPELL, new Identifier(EndAllMagic.MOD_ID, "wind_speed"), WINDSPEED);
			ENABLED_SPELLS.addSpell(WINDSPEED);
		}
		if(EndAllMagic.getConfig().spells.enableFireResistSpell){
			Registry.register(EndAllMagic.SPELL, new Identifier(EndAllMagic.MOD_ID, "fire_resist"), FIRERESIST);
			ENABLED_SPELLS.addSpell(FIRERESIST);
		}
		if(EndAllMagic.getConfig().spells.enableTornado){
			Registry.register(EndAllMagic.SPELL, new Identifier(EndAllMagic.MOD_ID, "tornado"), TORNADO);
			ENABLED_SPELLS.addSpell(TORNADO);
		}
		// if(EndAllMagic.getConfig().spells.enableRockWall){
		// 	Registry.register(EndAllMagic.SPELL, new Identifier(EndAllMagic.MOD_ID, "rock_wall"), ROCKWALL);
		// 	ENABLED_SPELLS.addSpell(ROCKWALL);
		// }
	}

}
