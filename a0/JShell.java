package a0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JShell {
    
    public static NODE rootNode = new NODE(false,"",null);              // no name and no parent folder for root folder
    public static NODE currNode;
    public static List<CMD> cmdList = new ArrayList<CMD>();
    public static List<String> dirStack   = new ArrayList<String>();    // for pushd & popd
    public static List<String> cmdHistory = new ArrayList<String>();    // for history
        
    public static void main(String[] args) {
        currNode = rootNode;                                            // init file system
        cmdList = RegistAllLegalCMD();                                  // init cmd system
        Scanner in = new Scanner(System.in);                            // init scanner for command line
        while(true) {
            System.out.printf("/# ");                                   // print prompt
            String cmdStr = removeDuplicatedSpace(in.nextLine());
            if (cmdStr.isEmpty())                                       // not count "" cmd
                continue;
            cmdHistory.add(cmdStr);
            CMD curCMD=IdentifyCMD(cmdStr);
            if (curCMD.cmdRegExp.equals("illegal")==false)              // when input cmd is legal, print it
                printCMD(cmdStr);
            if (curCMD.cmdRegExp.equals("exit"))                        // close scanner before exit
                in.close();
            curCMD.interfaceSave.SwithOver(cmdStr);                     // implement cmd
        }
    }
    
    public static List<CMD> RegistAllLegalCMD(){                        // connect ImplementXXX to corresponding CMD which allows using CMD.interfaceSave.SwithOver to call ImplementXXX 
        List<CMD> cmdList = new ArrayList<CMD>();
        cmdList.add(new CMD("exit",
                            new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementEXIT(cmdStr);}},
                            "exit:\n"
                            + "\tQuit the program."));
        cmdList.add(new CMD("mkdir .+",
                            new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementMKDIR(cmdStr);}},
                            "mkdir DIR ...:\n"
                            + "\tCreate directories.\n"
                            + "\tNote: Each of DIR may be relative to the current directory or may be a full path."));
        cmdList.add(new CMD("cd [^ ]+",
                            new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementCD(cmdStr);}},
                            "cd DIR:\n"
                            + "\tChange directory to DIR.\n"
                            + "\tNote: DIR may be relative to the current directory or may be a full path."));
        cmdList.add(new CMD("ls( .+)?",
                            new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementLS(cmdStr);}},
                            "ls [PATH ...]:\n"
                            + "\tList the contents.\n"
                            + "\tNote:\n"
                            + "\t1.If no PATH given, print contents of current directory.\n"
                            + "\t2.If PATH specifies a directory, print its contents(file or directory).\n"
                            + "\t3.If PATH specifies a file, print its name."));
        cmdList.add(new CMD("pwd",
                            new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementPWD(cmdStr);}},
                            "pwd:\n"
                            + "\tPrint the current working directory(including the whole path)."));
        cmdList.add(new CMD("pushd [^ ]+",
                            new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementPUSHD(cmdStr);}},
                            "pushd DIR:\n"
                            + "\tSaves the current working directory by pushing onto directory stack and then changes the new current working directory to DIR."));
        cmdList.add(new CMD("popd",
                            new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementPOPD(cmdStr);}},
                            "popd\n"
                            + "\tRemove the top entry from the directory stack, and cd into it."));
        cmdList.add(new CMD("history( [0-9]+)?",
                            new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementHISTORY(cmdStr);}},
                            "history [number]:\n"
                            + "\tPrint out recent commands.\n"
                            + "\tNote: If number is given, the output will be truncate by this number."));
        cmdList.add(new CMD("cat [^ ]+( .+)?",
                            new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementCAT(cmdStr);}},
                            "cat FILE1 [FILE2 ...]:\n"
                            + "\tDisplay the contents of FILE1 and other files(i.e. FILE2 ...) one by one."));
        cmdList.add(new CMD("echo \".*\"( >>? .+)?",
                            new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementECHO(cmdStr);}},
                            "echo STRING [> OUTFILE] or [>> OUTFILE]:\n"
                            + "\tWrite STRING into OUTFILE.\n"
                            + "\tNote:\n"
                            + "\t1.If OUTFILE is not provided, just print STRING on the shell.\n"
                            + "\t2.If OUTFILE do not exist, this CMD will create it.\n"
                            + "\t3.\">\" means overwrites while \">>\" means appends."));
        cmdList.add(new CMD("man [^ ]+",
                            new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementMAN(cmdStr);}},
                            "man CMD:\n"
                            + "\tPrint documentation for CMD."));
        return cmdList;
    }
    public static CMD IdentifyCMD(String cmdStr){                       // which CMD is this
        for (CMD tmpCMD : cmdList)
            if (MatchRegExp(cmdStr,tmpCMD.cmdRegExp))
                return tmpCMD;
        return new CMD("illegal",
                new CMD.cmdImplementInterface(){@Override public void SwithOver(String cmdStr){ImplementILLEGAL(cmdStr);}},
                "");
    }
    
    protected static void ImplementEXIT(String cmdStr) {
        System.exit(0);
    }
    protected static void ImplementMKDIR(String cmdStr) {
        String args[] = cmdStr.split(" ");
        for (int i=1;i<args.length;i++) {
            if (isNamesAllLeagal(args[i].split("/"))==false) 
                System.out.printf("Error: Fail to create folder \"%s\". Note: illegal characters \\/:*?\"<>| can not be used in a file or folder name!\n",args[i]);
            else if (GetNode(args[i])!=null)
                System.out.printf("Warnning: a folder or file named \"%s\" has been exsited!\n",args[i]);
            else 
                CreateFolder(args[i]);
        }
    }
    protected static void ImplementCD(String cmdStr) {
        String args[] = cmdStr.split(" ");
        NODE tNode = GetNode(args[1]);
        if (tNode==null)
            System.out.printf("Error: The input path \"%s\" doesn't exist!\n",args[1]);
        else if (tNode.isFile)
            System.out.printf("Error: The input path \"%s\" is a file, please input a folder path!\n",args[1]);
        else
            currNode = tNode;   
    }
    protected static void ImplementLS(String cmdStr) {
        List<String> args = new ArrayList<String>(Arrays.asList(cmdStr.split(" ")));
        if (args.size()==1)
            args.add("./");
        for (int i=1;i<args.size();i++) {
            NODE tNode = GetNode(args.get(i));
            if (tNode == null)
                System.out.printf("Error: The input path \"%s\" doesn't exist!\n",args.get(i));
            else if (tNode.isFile)
                System.out.println(tNode.name);
            else {
                for (int j=0;j<tNode.subList.size();j++)
                    System.out.printf(tNode.subList.get(j).name + " ");
                System.out.printf("\n");
            }
        }
        
    }
    protected static void ImplementPWD(String cmdStr) {
        System.out.println(currNode.GetAbsolutePath());
    }
    protected static void ImplementPUSHD(String cmdStr) {
        String args[] = cmdStr.split(" ");
        NODE tNode = GetNode(args[1]);
        if (tNode==null)
            System.out.printf("Error: Fail to push current path. Input DIR \"%s\" doesn't exist!\n",args[1]);
        else if (tNode.isFile)
            System.out.printf("Error: Fail to push current path. Input DIR \"%s\" is actually a file!\n",args[1]);
        else {
            dirStack.add(currNode.GetAbsolutePath());
            currNode = tNode;
        }
    }
    protected static void ImplementPOPD(String cmdStr) {
        int stackSize = dirStack.size();
        if (stackSize<=0) 
            System.out.println("Error: Directory stack has been empty. Please use cmd \"pushd\" first.");
        else {
            currNode = GetNode(dirStack.get(stackSize-1));
            if (stackSize==1) // update dirStack
                dirStack.clear();
            else
                dirStack = dirStack.subList(0, stackSize-1);
        }
    }
    protected static void ImplementHISTORY(String cmdStr) {
        String args[] = cmdStr.split(" ");
        int inputNum = args.length==1 ? Integer.MAX_VALUE : Integer.parseInt(args[1]);
        int nHistory = cmdHistory.size();
        int nDisp = Math.min(nHistory, inputNum);
        for (int i=nHistory-nDisp;i<nHistory;i++)
            System.out.printf("%d.%s\n",i+1,cmdHistory.get(i));
    }
    protected static void ImplementCAT(String cmdStr) {
        String args[] = cmdStr.split(" ");
        for (int i=1;i<args.length;i++) {
            NODE tNode = GetNode(args[i]);
            if (tNode==null)
                System.out.printf("Error: The target file \"%s\" do not exist!\n",args[i]);
            else if(tNode.isFile==false)
                System.out.printf("Error: The target file \"%s\" is actually a folder!\n",args[i]);
            else
                System.out.println(tNode.fileContent);
            System.out.print("\n\n\n");
        }

    }
    protected static void ImplementECHO(String cmdStr) {
        String args[] = cmdStr.split(" \"",2);
        if (args[1].endsWith("\"")) // no ">" nor ">>", just print it without double markers
            System.out.println(args[1].substring(0, args[1].length()-1));
        else {
            String inputStr=null,leftStr=null;
            for (int i=args[1].length()-3;i>=0;i--) { // try to find the end double quotation marker
                if (args[1].substring(i, i+3).equals("\" >")) {
                    inputStr = args[1].substring(0, i);
                    leftStr = args[1].substring(i, args[1].length());
                    break;
                }
            }
            boolean isAppend;
            String destFilePath=null;
            if (leftStr.startsWith("\" >> ")) {
                isAppend = true;
                destFilePath = leftStr.substring(5, leftStr.length());
            } else {
                isAppend = false;
                destFilePath = leftStr.substring(4, leftStr.length());
            }
            NODE tNode = GetNode(destFilePath);
            if (tNode == null) 
                tNode = CreateFile(destFilePath);
            else if(tNode.isFile==false) {
                System.out.printf("Error: The target file \"%s\" is actually a folder!\n",destFilePath);
                return;
            }
            if (isAppend)
                tNode.fileContent += inputStr;
            else
                tNode.fileContent = inputStr;
        }
    }
    
    protected static void ImplementILLEGAL(String cmdStr) {
        System.out.println("Invalid command, please try again");
    }
    protected static void ImplementMAN(String cmdStr) {
        String args[] = cmdStr.split(" ");
        args[1] = args[1].toLowerCase();
        for (CMD tmpCMD : cmdList) {
            if (tmpCMD.cmdRegExp.startsWith(args[1])) {
                System.out.println(tmpCMD.cmdDocStr);
                return;
            }
        }
        System.out.printf("Error: No such a cmd called \"%s\"!\n",args[1]);
    }


    // TOOLS
    // tool functions for cmd and file system
    public static boolean MatchRegExp(String inStr,String inRegExp){
        Pattern pattern = Pattern.compile(inRegExp);
        Matcher matcher = pattern.matcher(inStr.toLowerCase());
        return matcher.matches();
    }
    public static String removeDuplicatedSpace(String inStr){
        inStr = inStr.trim();
        if (inStr.length()==0)
            return inStr;
        String outStr="";
        if (MatchRegExp(inStr,"echo[ |\t]+\".+\".*")) { // special protection for chars in cmd ECHO
            String tmpArgs[] = inStr.split("\"");
            outStr = "echo ";
            for (int i=1;i<tmpArgs.length-1;i++)
                outStr += ("\"" + tmpArgs[i]);
            if (inStr.endsWith("\""))
                outStr += "\"" + tmpArgs[tmpArgs.length-1] + "\"";
            else
                outStr += ("\" " + removeDuplicatedSpace(tmpArgs[tmpArgs.length-1]));
        } else {                                        // normal process
            inStr = inStr.replace('\t', ' ');
            outStr+=inStr.charAt(0);
            int nChar = inStr.length();
            for (int i=1;i<nChar;i++)
                if (inStr.charAt(i)!=' ' | inStr.charAt(i-1)!=' ')
                    outStr+=inStr.charAt(i);
        }
        return outStr;
    }
    public static void printCMD(String cmdStr) {
        String cmd[] = cmdStr.split(" ",2);
        System.out.println(cmd[0].toLowerCase());
        if (cmd.length>1)
            System.out.println(cmd[1]);
        else
            System.out.printf("\n");
    }
    public static boolean isNameLeagal(String name){
        return MatchRegExp(name,"[^\\/:*?\"<>|]*");
    }
    public static boolean isNamesAllLeagal(String[] names) {
        for (int i=0;i<names.length;i++) {
            if (isNameLeagal(names[i])==false) 
                return false;
        }
        return true;
    }
    public static boolean isAbsolutePath(String path) {
        return path.startsWith("/");
    }
    public static boolean isRelatvivePath(String path) {
        return !path.startsWith("/");
    }
    public static NODE GetNode(String path) {
        NODE tNode = currNode;
        if (isAbsolutePath(path))
            tNode = rootNode;
        String args[] = GetNodeNamesFromPath(path);
        for (int i=0;i<args.length;i++) {
            if (args[i].equals("..") && tNode.parent!=null)
                tNode = tNode.parent;
            else if (args[i].equals(".") || (args[i].equals("..") && tNode.parent==null) || args[i].equals(""))
                continue;
            else {
                tNode = tNode.GetSubNode(args[i]);
                if (tNode==null)
                    return null;
            }
        }
        return tNode;
    }
    public static NODE CreateNode(String path,boolean isFile) {
        NODE tNode = currNode;
        if (isAbsolutePath(path)) 
            tNode = rootNode;
        String args[] = GetNodeNamesFromPath(path);
        for (int i=0;i<args.length;i++) {
            if (args[i].equals("..") && tNode.parent!=null)
                tNode = tNode.parent;
            else if (args[i].equals(".") || (args[i].equals("..") && tNode.parent==null) || args[i].equals(""))
                continue;
            else {
                NODE tmp = tNode.GetSubNode(args[i]);
                if (tmp==null) {
                    if (i==(args.length-1))
                        tNode.subList.add(new NODE(isFile,args[i],tNode));
                    else
                        tNode.subList.add(new NODE(false,args[i],tNode));
                    tNode = tNode.GetSubNode(args[i]);
                } else
                    tNode = tmp;
            }
        }
        return tNode;
    }
    public static NODE CreateFile(String path) {
        return CreateNode(path,true);
    }
    public static NODE CreateFolder(String path) {
        return CreateNode(path,false);
    }
    public static String[] GetNodeNamesFromPath(String path) {
        path = path.replace("\\", "/").replace("\\\\", "/");    //compatible for win
        return path.replace("/", " ").trim().split(" ");            //remove "/" in both ends
    }
}

