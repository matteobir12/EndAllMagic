package net.fabricmc.endallmagic.common;

public enum Pattern{
    LEFT {
        @Override
        public String toString(){
            return "L";
        }
    },
    RIGHT {
        @Override
        public String toString(){
            return "R";
        }
    }
}
