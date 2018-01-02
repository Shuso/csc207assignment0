package a0;


//CMD
//class to describe all CMD may used
public class CMD {
   public String cmdRegExp;
   public cmdImplementInterface interfaceSave;
   public String cmdDocStr;
   public static interface cmdImplementInterface{
       abstract void SwithOver(String cmdStr);
   }
   public CMD(String inCmdRegExp,cmdImplementInterface inInterface,String inCmdDocStr) {
       cmdRegExp = inCmdRegExp;
       interfaceSave = inInterface;
       cmdDocStr = inCmdDocStr;
   }
   public void ImplementCmd(String cmdStr) {
       interfaceSave.SwithOver(cmdStr);
   }
}


