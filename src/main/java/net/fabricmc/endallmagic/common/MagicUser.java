package net.fabricmc.endallmagic.common;

import net.fabricmc.endallmagic.common.spells.Pattern;
import net.fabricmc.endallmagic.common.spells.Spell;
import net.fabricmc.endallmagic.common.spells.SpellConfig;
import net.fabricmc.endallmagic.common.spells.SpellTree;

public interface MagicUser {
	
	SpellTree getKnownSpells();

	boolean spellIsKnown(java.util.List<Pattern> pattern);

	void setKnownSpell(Spell spell);

	int getCurrentMana();

	int getMaxMana();

	int getManaRegenTimer();

	void setMaxMana(int amount);

	void setManaRegenTimer(int amount);

	void setMana(int amount);

	void addMana(int amount);

	void setLevel(int level);

	void setAffinity(SpellConfig.Affinity affinity);

	int getLevel();

	SpellConfig.Affinity getAffinity();

	boolean isManaVisible();

	void setLastCastTime(long lastCastTime);

	void setActiveSpell(Spell spell, int timer);

	void addOnTick(Spell s, OnTick t);

	void removeOnTick(Spell s);

	boolean onTickContains(Spell s);

	interface OnTick {
		// true if it should be removed
        boolean tick();
    }

	void toggleManaFireRes();
	
	void channelSpell(int timer, OnTick spell);

	void toggleWindDash();

	boolean getWindDash();

	void windDashDirection(int direction);

}
