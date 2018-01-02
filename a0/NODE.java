package a0;

import java.util.ArrayList;
import java.util.List;

//NODE
//class to build mock file system
public class NODE {
 public boolean isFile; // true means file, false means folder
 public String name;
 public NODE parent;
 public List<NODE> subList;
 public String fileContent;
 public NODE (boolean isFile, String name, NODE parent) {
     this.isFile = isFile;
     this.name = name;
     this.parent = parent;
     if (isFile) {                           // init for file
         this.fileContent = new String();
     } else {                                // init for folder
         this.subList = new ArrayList<NODE>();
     }
 }
 public String GetAbsolutePath() {
     if (parent==null)
         return "/";
     else {
         String re = new String(name);
         NODE tNode = this;
         while (tNode.parent!=null) {
             tNode = tNode.parent;
             re = tNode.name + "/" + re;
         }
         return re;
     }
 }
 public NODE GetSubNode(String name) {
     for (NODE tNode : subList) 
         if (tNode.name.equals(name))
             return tNode;
     return null;
 }
}
