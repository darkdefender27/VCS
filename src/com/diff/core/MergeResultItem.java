package com.diff.core;

import java.util.ArrayList;

public class MergeResultItem {

  public enum Type {NO_CONFLICT,
                    CONFLICT,
                    WARNING_DELETE,
                    WARNING_ORDER}

  public enum DefaultVersion {LEFT,RIGHT}

  private ArrayList<FileLine> leftVersion, rightVersion;
  private DefaultVersion defaultVersion;
  private Type type;
  private int lineCount;

  public MergeResultItem(Type type, ArrayList<FileLine> leftVersion, ArrayList<FileLine> rightVersion, DefaultVersion defaultVersion, int lineCount){
    this.type = type;
    this.leftVersion = leftVersion;
    this.rightVersion = rightVersion;
    this.defaultVersion = defaultVersion;
    this.lineCount = lineCount;
  }

  public String toString(){
    String result = "# "+FileLine.statusToString(leftVersion.get(0).getStatus())+","+FileLine.statusToString(rightVersion.get(0).getStatus());
    return result;
  }

  public boolean couldMerge(MergeResultItem other){
    boolean result = false;
    if (leftVersion.get(lineCount-1).getStatus()==other.leftVersion.get(other.lineCount-1).getStatus() ||
        rightVersion.get(lineCount-1).getStatus()==other.rightVersion.get(other.lineCount-1).getStatus()){
      result = true;
    }

    return result;
  }

  public boolean isConflict(){
    return type==Type.CONFLICT;
  }

  public boolean matches(){
    boolean result = false;
    // since all lines in an item bloc have the same status, we can just test the first line
    if (leftVersion.get(0).getStatus()==FileLine.UNCHANGED && rightVersion.get(0).getStatus()==FileLine.UNCHANGED){
      result = true;
    }
    return result;
  }

  public static MergeResultItem merge(Type type, ArrayList<MergeResultItem> items){
    MergeResultItem result = new MergeResultItem(type, null, null, DefaultVersion.LEFT,0);
    result.leftVersion = new ArrayList<FileLine>();
    result.rightVersion = new ArrayList<FileLine>();
    for(MergeResultItem item:items){
      result.leftVersion.addAll(item.leftVersion);
      result.rightVersion.addAll(item.rightVersion);
      result.lineCount+=item.lineCount;
    }
    return result;
  }

  public void mergeWith(MergeResultItem other){
    leftVersion.addAll(other.leftVersion);
    rightVersion.addAll(other.rightVersion);
    lineCount+=other.lineCount;
  }

  public ArrayList<FileLine> getLeftVersion(){
    return leftVersion;
  }

  public ArrayList<FileLine> getRightVersion(){
    return rightVersion;
  }

  public DefaultVersion getDefaultVersion(){
    return defaultVersion;
  }

  public Type getType(){
    return type;
  }

  public int getLineCount(){
    return lineCount;
  }

}
