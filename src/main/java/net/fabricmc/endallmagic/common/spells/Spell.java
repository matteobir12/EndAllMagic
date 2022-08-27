package net.fabricmc.endallmagic.common.spells;


import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.Pattern;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class Spell {
    public final java.util.List<Pattern> pattern = new java.util.ArrayList<>();
    protected int manaCost;
    protected final Random random = Random.create();
    private String translationKey;

    public <T extends LivingEntity & MagicUser> void attemptCast(T entity, World world){
   
    }

    public int getManaCost() {
        return manaCost;
    }
    protected String getOrCreateTranslationKey() {
		if(this.translationKey == null)
			this.translationKey = Util.createTranslationKey("spell", EndAllMagic.SPELL.getId(this));

		return this.translationKey;
	}

	public String getTranslationKey() {
		return getOrCreateTranslationKey();
	}
}
