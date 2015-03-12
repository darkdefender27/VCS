package com.diff.core;

public class Chunk{

  protected int first;
  protected int count;

  public Chunk(int first,int count){
    this.first = first;
    this.count = count;
  }

  public int first(){
    return first;
  }

  public int count(){
    return count;
  }
}
