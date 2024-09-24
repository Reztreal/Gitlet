package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Resit
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "log-tracked":
                Repository.LogTrackedFiles(args[1]);
                break;
            case "init":
                validateNumArgs("init", args, 1);
                Repository.init();
                break;
            case "add":
                Repository.checkIfInitialized();
                validateNumArgs("add", args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                Repository.checkIfInitialized();
                validateNumArgs("commit", args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.checkIfInitialized();
                validateNumArgs("rm", args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                Repository.checkIfInitialized();
                validateNumArgs("log", args, 1);
                Repository.log();
                break;
            case "global-log":
                Repository.checkIfInitialized();
                validateNumArgs("global-log", args, 1);
                Repository.globalLog();
                break;
            case "find":
                Repository.checkIfInitialized();
                validateNumArgs("find", args, 2);
                Repository.find(args[1]);
                break;
            case "branch":
                Repository.checkIfInitialized();
                validateNumArgs("branch", args, 2);
                Repository.branch(args[1]);
                break;
            case "checkout":
                Repository.checkIfInitialized();
                if (args.length > 4 || args.length < 2) {
                    System.out.println("Incorrect operands.");
                }
                if (args.length == 3) {
                    Repository.checkoutFilename(args[2]);
                    break;
                } else if (args.length == 4) {
                    Repository.checkoutCommitAndFilename(args[1], args[3]);
                    break;
                } else if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                    break;
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
        }
    }
}
