package gitlet;

import java.io.File;
import java.io.IOException;

public class Branch {
    public static final File BRANCH_DIR = Repository.BRANCH_DIR;
    public static final File ACTIVE_BRANCH = Repository.ACTIVE_BRANCH;
    public static final File HEAD = Repository.HEAD;
    public static final File MASTER = Repository.MASTER;

    public static void createNewBranch(String branchName) {
        File newBranch = Utils.join(BRANCH_DIR, branchName);

        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
        } else {
            try {
                newBranch.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String head = Utils.readContentsAsString(HEAD);
            Utils.writeContents(newBranch, head);
        }
    }

    public static void update(String hash) {
        updateActiveBranch(hash);
        updateHead(hash);
    }

    public static void updateActiveBranch(String hash) {
        File activeBranch = Utils.join(BRANCH_DIR, getActiveBranch());
        Utils.writeContents(activeBranch, hash);
    }

    public static Commit getActiveBranchHeadCommit() {
        File activeBranch = Utils.join(BRANCH_DIR, getActiveBranch());

        return Commit.fromFile(Utils.readContentsAsString(activeBranch));
    }

    public static String getActiveBranchHeadCommitHash() {
        File activeBranch = Utils.join(BRANCH_DIR, getActiveBranch());

        return Utils.readContentsAsString(activeBranch);
    }

    public static void updateHead(String hash) {
        Utils.writeContents(HEAD, hash);
    }

    public static String getActiveBranch() {
        return Utils.readContentsAsString(ACTIVE_BRANCH);
    }

    public static Commit getBranchHeadCommit(String branch) {
        File b = Utils.join(BRANCH_DIR, branch);
        if (!b.exists()) {
            return null;
        }

        return Commit.fromFile(Utils.readContentsAsString(b));
    }
}
