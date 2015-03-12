package com.diff.core;

public final class DiffNode extends PathNode{

    public DiffNode(int i, int j, PathNode prev){
        super(i, j, (prev == null ? null : prev.previousSnake()));
    }
   
    public boolean isSnake(){
        return false;
    }

}
