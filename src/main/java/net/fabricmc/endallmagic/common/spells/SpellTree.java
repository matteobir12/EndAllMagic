package net.fabricmc.endallmagic.common.spells;

import java.util.function.Consumer;

import oshi.util.tuples.Pair;

public class SpellTree {
    
    private class SpellTreeNode{

        private SpellTreeNode left;
        private SpellTreeNode right;
        private Spell spell;

        public SpellTreeNode(){
            left = null;
            right = null;
            spell = null;
        }
    }

    private SpellTreeNode head;
    
    public SpellTree(){
        head = new SpellTreeNode();
    }

    public void addSpell(Spell spell){
        SpellTreeNode node = head;
        for (Pattern p : spell.pattern){
            if (p == Pattern.LEFT){
                if (node.left == null)
                    node.left = new SpellTreeNode();
                
                node = node.left;
                
            } else {
                if (node.right == null)
                    node.right = new SpellTreeNode();
                
                node = node.right;
            }
        }
        node.spell = spell;
    }

    public Pair<Spell,Boolean> getSpell(java.util.List<Pattern> pattern) {
        SpellTreeNode node = head;
        for (Pattern p : pattern){
            if (p == Pattern.LEFT){
                if (node.left == null)
                    return new Pair<>(null,false);
                
                node = node.left;
                
            } else {
                if (node.right == null)
                    return new Pair<>(null,false);

                node = node.right;
            }
        }
        return new Pair<>(node.spell,true);
    }
    
    public java.util.List<Spell> asList(){
        java.util.List<Spell> list = new java.util.ArrayList<>();
        asListHelper(head, list);
        return list;
    }

    private void asListHelper(SpellTreeNode node, java.util.List<Spell> list){
        if (node == null) 
            return;
        if (node.spell !=null)
            list.add(node.spell);
        asListHelper(node.left, list);
        asListHelper(node.right, list);

    }

    public void forEach(Consumer<Spell> forEachSpell){
        asList().forEach(forEachSpell);
    }
    public boolean isEmpty(){
        return head.left ==null && head.right == null && head.spell == null;
    }
    public boolean contains(Spell spell){
        return getSpell(spell.pattern).getA() !=null;
    }

    public void clear(){
        head = new SpellTreeNode();
    }

    public void remove(Spell spell){
        SpellTreeNode node = head;
        for (Pattern p : spell.pattern){
            if (p == Pattern.LEFT){
                if (node.left == null)
                    return;
                
                node = node.left;
                
            } else {
                if (node.right == null)
                    return;

                node = node.right;
            }
        }
        node.spell = null;
    }
}

