package net.fabricmc.endallmagic.common;

import java.util.Arrays;

import net.fabricmc.endallmagic.common.spells.Spell;

public class SpellConfig {
    java.util.List<Spell> spellObjects = new java.util.ArrayList<>();
    
    public static final java.util.List<Pattern> FIRE_PATTERN = new java.util.ArrayList<>(2);
    public static final java.util.List<Pattern> WATER_PATTERN = new java.util.ArrayList<>(2);
    public static final java.util.List<Pattern> EARTH_PATTERN = new java.util.ArrayList<>(2);
    public static final java.util.List<Pattern> WIND_PATTERN = new java.util.ArrayList<>(2);

    static{
        FIRE_PATTERN.addAll(Arrays.asList(Pattern.LEFT,Pattern.LEFT));
        WATER_PATTERN.addAll(Arrays.asList(Pattern.RIGHT,Pattern.RIGHT));
        EARTH_PATTERN.addAll(Arrays.asList(Pattern.LEFT,Pattern.RIGHT));
        WIND_PATTERN.addAll(Arrays.asList(Pattern.RIGHT,Pattern.LEFT));
    }

}
