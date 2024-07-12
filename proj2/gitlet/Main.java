package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            exitWithMessage("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // handle the `init` command
                validateNumArgs(args,1);
                init();
                break;
            case "add":
                // handle the `add [filename]` command
                validateNumArgs(args,2);
                checkGitEnv();
                add(args[1]);
                break;
            case "commit":
                // handle the `commit [message]` command
                validateNumArgs(args,2);
                checkGitEnv();
                commit(args[1]);
                break;
            case "rm":
                // handle the `rm [file name]` command
                validateNumArgs(args,2);
                checkGitEnv();
                (new Repository(true)).rm(args[1]);
                break;
            case "log":
                validateNumArgs(args,1);
                checkGitEnv();
                (new Repository(true)).log();
                break;
            case "global-log":
                validateNumArgs(args,1);
                checkGitEnv();
                (new Repository(true)).globalLog();
                break;
            case "find":
                // handle the `find [message]` command
                validateNumArgs(args,2);
                checkGitEnv();
                (new Repository(true)).findMessage(args[1]);
                break;
            case "status":
                validateNumArgs(args,1);
                checkGitEnv();
                (new Repository(true)).status();
                break;
            case "restore":
                checkGitEnv();
                if(args.length == 3 && args[1].equals("--")){
                    // handle the `restore -- [filename]` command
                    restore("",args[2]);
                }else if(args.length == 4 && args[2].equals("--")){
                    // handle the `restore [commit id] -- [filename]` command
                    restore(args[1],args[3]);
                }else {
                    validateNumArgs(args,0);
                }
                break;
            case "branch":
                // handle the `branch [branch name]` command
                validateNumArgs(args,2);
                checkGitEnv();
                (new Repository(true)).branch(args[1]);
                break;
            case "switch":
                // handle the `branch [branch name]` command
                validateNumArgs(args,2);
                checkGitEnv();
                (new Repository(true)).switchBranch(args[1]);
                break;
            case "rm-branch":
                // handle the `rm-branch [branch name]` command
                validateNumArgs(args,2);
                checkGitEnv();
                (new Repository(true)).rmBranch(args[1]);
                break;
            case "reset":
                // handle the `branch [branch name]` command
                validateNumArgs(args,2);
                checkGitEnv();
                (new Repository(true)).reset(args[1]);
                break;
            case "merge":
                // handle the `merge [branch name]` command
                validateNumArgs(args,2);
                checkGitEnv();
                (new Repository(true)).mergeBranch(args[1]);
                break;
            default:
                exitWithMessage("No command with that name exists.");
                break;
        }
    }

    private static void init(){
        if(Repository.checkInit()){
            exitWithMessage("A Gitlet version-control system already exists in the current directory.");
        }else{
            (new Repository(false)).init();
        }
    }
    private static void add(String fileName){
        File f = Utils.join(Repository.CWD, fileName);
        if(f.exists()) {
            (new Repository(true)).add(fileName);
        }else{
            exitWithMessage("File does not exist.");
        }
    }
    private static void commit(String message){
        if(message.length() > 0){
            Repository repo = new Repository(true);
            if(repo.hasOperations()){
                repo.commit(message);
            }else{
                exitWithMessage("No changes added to the commit.");
            }
        }else{
            exitWithMessage("Please enter a commit message.");
        }
    }
    private static void restore(String commitId, String fileName){
        if(fileName.length() > 0){
            (new Repository(true)).restore(commitId,fileName);
        }else{
            System.out.println("Incorrect operands.");
        }
    }

    private static void checkGitEnv(){
        if(!Repository.checkInit()){
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static void exitWithMessage(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(0);
    }
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}