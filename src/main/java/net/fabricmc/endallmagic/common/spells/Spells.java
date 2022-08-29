 package net.fabricmc.endallmagic.common.spells;

 import net.fabricmc.endallmagic.EndAllMagic;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

 public class Spells {
	public static final SpellTree ENABLED_SPELLS = new SpellTree();
	//-----Spells-----//
	public static final Spell FIREBALL = new FireBall(); // use config constuctor and link to config
	public static final Spell HEAL = new HealSpell();
	private Spells() { }

	//-----Registry-----//
	public static void register() {
		if(EndAllMagic.getConfig().spells.enableFireBall) {
			Registry.register(EndAllMagic.SPELL, new Identifier(EndAllMagic.MOD_ID, "fireball"), FIREBALL);
			ENABLED_SPELLS.addSpell(FIREBALL);
		}
		if(EndAllMagic.getConfig().spells.enableHeal){
			Registry.register(EndAllMagic.SPELL, new Identifier(EndAllMagic.MOD_ID, "heal"), HEAL);
			ENABLED_SPELLS.addSpell(HEAL);
		}
	}
 }