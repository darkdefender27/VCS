package com.diff.core;

public class MergeInfo {

  protected ParsedFile ancestor1,version1,ancestor2,version2;

  public MergeInfo(ParsedFile ancestor1, ParsedFile version1, ParsedFile ancestor2, ParsedFile version2) {
    this.ancestor1 = ancestor1;
    this.version1 = version1;
    this.ancestor2 = ancestor2;
    this.version2 = version2;
  }

}
