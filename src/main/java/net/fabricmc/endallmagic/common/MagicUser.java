package net.fabricmc.endallmagic.common;

import net.fabricmc.endallmagic.common.spells.Spell;
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

	boolean isManaVisible();

	void shouldShowMana(boolean shouldShowMana);

	void setLastCastTime(long lastCastTime);

	void setActiveSpell(Spell spell, int timer);
}
