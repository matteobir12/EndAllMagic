 package net.fabricmc.endallmagic.common.spells;

 import net.fabricmc.endallmagic.EndAllMagic;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

 public class Spells {
	//-----Spells-----//
	public static final Spell FIREBALL = new FireBall();
	public static final Spell HEAL = new HealSpell();

	//-----Registry-----//
	public static void register() {
		if(EndAllMagic.getConfig().spells.enableFireBall)
			Registry.register(EndAllMagic.SPELL, new Identifier(EndAllMagic.MOD_ID, "fireball"), FIREBALL);
		if(EndAllMagic.getConfig().spells.enableHeal)
			Registry.register(EndAllMagic.SPELL, new Identifier(EndAllMagic.MOD_ID, "heal"), HEAL);
	}
 }