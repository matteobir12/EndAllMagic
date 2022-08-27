package net.fabricmc.endallmagic.common.spells;


import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.Pattern;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class Spell {
    public final java.util.List<Pattern> pattern = new java.util.ArrayList<>();
    protected int manaCost;
    protected final Random random = Random.create();

    public <T extends LivingEntity & MagicUser> void attemptCast(T entity, World world){
   
    }

    public int getManaCost() {
        return manaCost;
    }
}
