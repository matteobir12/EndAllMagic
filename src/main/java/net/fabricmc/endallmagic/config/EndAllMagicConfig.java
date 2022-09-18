package net.fabricmc.endallmagic.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.fabricmc.endallmagic.EndAllMagic;

@Config(name = EndAllMagic.MOD_ID)
public class EndAllMagicConfig implements ConfigData {

	@Comment("The colour of objects created by spells (i.e. the laser" +
			"\n    from Solar Strike and the wall from Arcane Wall).")
	public String magicColour = Integer.toString(0x7ecdfb, 16);

	@Comment("The time Mana takes to refill by 1 in ticks before modifiers.")
	public int baseManaCooldown = 20;

	@Comment("The time Burnout takes to reduce by 1 in ticks before modifiers.")
	public int baseBurnoutCooldown = 60;

	@Comment("Spells that should be enabled. (Restart Required)")
	@ConfigEntry.Gui.CollapsibleObject
	public SpellStuff spells = new SpellStuff();

	@ConfigEntry.Category(value = "client")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 50)
	@ConfigEntry.Gui.Tooltip
	public int textScaleX = 50;

	@ConfigEntry.Category(value = "client")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 50)
	@ConfigEntry.Gui.Tooltip
	public int textScaleY = 50;

	public static class SpellStuff {

		@Comment("Should Lunge be enabled? (Restart Required)")
		public boolean enableLunge = true;

		@Comment("Should Dream Warp be enabled? (Restart Required)")
		public boolean enableDreamWarp = true;

		@Comment("Should Fire Ball be enabled? (Restart Required)")
		public boolean enableFireBall = true;

		@Comment("Should Wind Blade be enabled? (Restart Required)")
		public boolean enableWindBlade = true;

		@Comment("Should Heal be enabled? (Restart Required)")
		public boolean enableHeal = true;

		@Comment("Should Wind Speed be enabled? (Restart Required)")
		public boolean enableWindSpeed = true;

		@Comment("Should the fire resist spell be enabled? (Restart Required)")
		public boolean enableFireResistSpell = true;

		@Comment("Should tornado be enabled? (Restart Required)")
		public boolean enableTornado = true;

		@Comment("Should rock wall be enabled? (Restart Required)")
		public boolean enableRockWall = true;

		@Comment("The Mana costs for all the spells.")
		@ConfigEntry.Gui.CollapsibleObject
		public ManaCosts manaCosts = new ManaCosts();
	}

	public static class ManaCosts {
		@Comment("The Mana cost for the Lunge spell.")
		public int lungeCastingCost = 5;

		@Comment("The Mana cost for the Dream Warp spell.")
		public int dreamWarpCastingCost = 15;

		@Comment("The Mana cost for the Magic Missile spell.")
		public int magicMissileCastingCost = 3;

		@Comment("The Mana cost for the Telekinetic Shock spell.")
		public int telekinesisCastingCost = 4;

		@Comment("The Mana cost for the Heal spell.")
		public int healCastingCost = 10;

		@Comment("The Mana cost for the Discombobulate spell.")
		public int discombobulateCastingCost = 10;

		@Comment("The Mana cost for the Solar Strike spell.")
		public int solarStrikeCastingCost = 20;

		@Comment("The Mana cost for the Arcane Barrier spell.")
		public int arcaneBarrierCastingCost = 4;
	}

	public float textScaleX() {
		return (this.textScaleX + 25) * 0.01F;
	}
	
	public float textScaleY() {
		return (this.textScaleY + 25) * 0.01F;
	}
}
