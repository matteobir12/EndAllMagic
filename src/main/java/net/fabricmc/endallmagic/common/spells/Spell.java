package net.fabricmc.endallmagic.common.spells;


import net.fabricmc.endallmagic.common.Pattern;

public abstract class Spell {
    public final java.util.List<Pattern> pattern = new java.util.ArrayList<>();
    private int manaCost;

    public boolean attemptCast(){
            return true;
    }
    public int getManaCost() {
        return manaCost;
    }
    @Override
    public int hashCode() {
        int code =0;
    
        // TODO Auto-generated method stub
        return super.hashCode();
    }
    @Override
    public boolean equals(Object arg0) {
        // TODO Auto-generated method stub
        return super.equals(arg0);
    }
}
