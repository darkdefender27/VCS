package com.diff.core; 

import java.util.ArrayList; 

public class MergeResult { 

   private ArrayList<MergeResultItem> mergeItems; 
   private ParsedFile leftFile = null; 
   private ParsedFile rightFile = null; 
   private String eol = System.getProperty("line.separator"); 

   public MergeResult(ArrayList<MergeResultItem> mergeItems) { 
      this.mergeItems = mergeItems; 
   } 

   public ParsedFile getLeftFile(){ 
      if (leftFile==null){ 
         ArrayList<FileLine> lines = new ArrayList<FileLine> (); 
         for (int i = 0; i < mergeItems.size(); i++) { 
            lines.addAll(mergeItems.get(i).getLeftVersion()); 
         } 
         leftFile = new ParsedFile(lines); 
      } 
      return leftFile; 
   } 

   public ParsedFile getRightFile(){ 
      if (rightFile==null){ 
         ArrayList<FileLine> lines = new ArrayList<FileLine> (); 
         for (int i = 0; i < mergeItems.size(); i++) { 
            lines.addAll(mergeItems.get(i).getRightVersion()); 
         } 
         rightFile = new ParsedFile(lines); 
      } 
      return rightFile; 
   } 

   public ArrayList<MergeResultItem> getMergeItems(){ 
      return mergeItems; 
   } 

   public String getDefaultMergedResult(){       
      StringBuffer buf = new StringBuffer(); 
      for(int i=0;i<mergeItems.size();i++){ 
         if (i>0){ 
            buf.append(eol); 
         } 
         MergeResultItem item = mergeItems.get(i);          
         MergeResultItem.Type type = item.getType(); 
         String text = ""; 
         boolean defaultLeft = item.getDefaultVersion()==MergeResultItem.DefaultVersion.LEFT; 
         String leftText = getText(item.getLeftVersion()); 
         String rightText = getText(item.getRightVersion()); 
         switch(type){ 
         case NO_CONFLICT: 
            text = defaultLeft?leftText:rightText; 
            break; 
         case CONFLICT: 
            text = "<<CONFLICT>>"; 
            break; 
         case WARNING_ORDER: 
            text = leftText + eol + rightText; 
            break; 
         case WARNING_DELETE: 
            text = defaultLeft?leftText:rightText; 
            break; 
         }        
         buf.append(text); 
      } 
      return buf.toString(); 
   } 
    
   public boolean isConflict(){ 
      boolean result = false; 
      for(int i=0;!result && i<mergeItems.size();i++){ 
         MergeResultItem item = mergeItems.get(i);          
         if (item.getType()==MergeResultItem.Type.CONFLICT){ 
            result = true; 
         } 
      } 
      return result; 
   } 
    
   public boolean isWarning(){ 
      boolean result = false; 
      for(int i=0;!result && i<mergeItems.size();i++){ 
         MergeResultItem item = mergeItems.get(i);          
         if (item.getType()==MergeResultItem.Type.WARNING_DELETE || 
            item.getType()==MergeResultItem.Type.WARNING_ORDER){ 
            result = true; 
         } 
      } 
      return result; 
   } 

   private String getText(ArrayList<FileLine> lines){ 
      StringBuffer buf = new StringBuffer(); 
      for(int i=0;i<lines.size();i++){ 
         if (i>0){ 
            buf.append(eol); 
         } 
         buf.append(lines.get(i).getContent()); 
      } 
      return buf.toString(); 
   } 
} 
