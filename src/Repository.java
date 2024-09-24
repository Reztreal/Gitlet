package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.join;

public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The branch directory */
    public static final File BRANCH_DIR = join(GITLET_DIR, "branches");
    public static final File ACTIVE_BRANCH = join(BRANCH_DIR, "active-branch");
    public static final File MASTER = join(BRANCH_DIR, "master");
    public static final File HEAD = join(BRANCH_DIR, "HEAD");
    /** The commit directory. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    /** The blob directory. */
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");
    /** The staging area. */
    public static final File STAGING_AREA = join(GITLET_DIR, "staging_area");

    public static void init() {
        if (GITLET_DIR.isDirectory() && GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            try {
                createFileStructure();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void add(String fileName) {
        File f = Utils.join(CWD, fileName);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        String hash = Blob.saveBlob(f);

        Commit currentCommit = Commit.fromFile(Utils.readContentsAsString(HEAD));
        TreeMap<String, String> currentTrackedFiles = currentCommit.trackedFiles;

        StagingArea st = StagingArea.fromFile();
        TreeMap<String, String> addFiles = st.stagedForAddition;
        TreeMap<String, String> removeFiles = st.stagedForRemoval;

        /**
         * If the current working version of the file
         * is identical to the version in the current commit,
         * do not stage it to be added, and remove it
         * from the staging area if it is already there
         * */
        if (currentTrackedFiles.containsKey(fileName)) {
            if (currentTrackedFiles.get(fileName).equals(hash)) {
                if (addFiles.containsKey(fileName) && addFiles.get(fileName).equals(hash)) {
                    addFiles.remove(fileName, hash);
                    StagingArea.saveStagingArea(st);
                    return;
                }
            }
        }

        if (removeFiles.containsKey(fileName) && removeFiles.get(fileName).equals(hash)) {
            removeFiles.remove(fileName, hash);
        }

        StagingArea.add(fileName, hash);
    }

    public static void commit(String message) {

        // Last commit of the active branch
        String currentHash = Utils.readContentsAsString(HEAD);
        Commit current = Commit.fromFile(currentHash);

        // Get the staging area
        StagingArea st = StagingArea.fromFile();
        TreeMap<String, String> addFiles = st.stagedForAddition;
        TreeMap<String, String> removeFiles = st.stagedForRemoval;

        if (addFiles.isEmpty() && removeFiles.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        } else if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }

        Commit newCommit = new Commit(message, new Date(), currentHash);
        newCommit.trackedFiles = new TreeMap<>(current.trackedFiles);

        for (String addKey : addFiles.keySet()) {
            newCommit.trackedFiles.put(addKey, addFiles.get(addKey));
        }

        for (String removeKey : removeFiles.keySet()) {
            newCommit.trackedFiles.remove(removeKey);
        }

        if (newCommit.trackedFiles.equals(current.trackedFiles)) {
            System.out.println("A commit wasn't created since no new file was added to staging area.");
            return;
        }

        st.clear();

        StagingArea.saveStagingArea(st);
        String hash = Commit.saveCommit(newCommit);

        Branch.update(hash);
    }

    public static void rm(String fileName) {
        StagingArea stagingArea = StagingArea.fromFile();
        Commit currentCommit = Commit.fromFile(Utils.readContentsAsString(HEAD));

        TreeMap<String, String> stagedForAddition = stagingArea.stagedForAddition;
        TreeMap<String, String> stagedForRemoval = stagingArea.stagedForRemoval;

        TreeMap<String, String> trackedFiles = currentCommit.trackedFiles;

        boolean removed = false;

        // Remove from staging area if the file is staged
        if (stagedForAddition.containsKey(fileName)){
            stagedForAddition.remove(fileName);
            removed = true;
        }

        if (trackedFiles.containsKey(fileName)) {
            stagedForRemoval.put(fileName, trackedFiles.get(fileName));
            Utils.restrictedDelete(Utils.join(CWD, fileName));
            removed = true;
        }

        StagingArea.saveStagingArea(stagingArea);

        if (!removed) {
            System.out.println("No reason to remove the file.");
        }
    }

    public static void log() {
        String hash = Utils.readContentsAsString(HEAD);

        while (hash != null) {
            Commit c = Commit.fromFile(hash);

            System.out.println("===");
            System.out.println("commit " + hash);
            c.dump();

            //TODO: add info related to merge

            hash = c.getFirstParent();
        }
    }

    // TODO: fix it so it works with the new commit representation in file system
    public static void globalLog() {
        List<String> commits = Utils.plainFilenamesIn(COMMIT_DIR);

        assert commits != null;
        for (String commit : commits) {
            Commit c = Commit.fromFile(commit);

            System.out.println("===");
            System.out.println("commit " + commit);
            c.dump();
        }
    }

    // TODO: fix it so it works with the new commit representation in file system
    /** Prints out the ids of all commits that have the given commit message, one per line. */
    public static void find(String message) {
        List<String> commits = Utils.plainFilenamesIn(COMMIT_DIR);

        boolean found = false;

        assert commits != null;
        for (String commit : commits) {
            Commit c = Commit.fromFile(commit);

            if (c.getMessage().equals(message)) {
                System.out.println(commit);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void createFileStructure() throws IOException {
        BRANCH_DIR.mkdirs();
        COMMIT_DIR.mkdirs();
        BLOB_DIR.mkdirs();

        MASTER.createNewFile();
        HEAD.createNewFile();

        StagingArea st = new StagingArea();
        StagingArea.saveStagingArea(st);

        Commit firstCommit = new Commit();
        String hash = Commit.saveCommit(firstCommit);

        Utils.writeContents(MASTER, hash);
        Utils.writeContents(HEAD, hash);
        Utils.writeContents(ACTIVE_BRANCH, "master");
    }

    public static void LogTrackedFiles(String hash) {
        Commit c = Commit.fromFile(hash);
        System.out.println(c.trackedFiles);
    }

    public static void status() {

    }

    public static void branch(String branchName) {
        Branch.createNewBranch(branchName);
    }

    public static void checkoutFilename(String filename) {
        Commit headCommit = Commit.fromFile(Utils.readContentsAsString(HEAD));
        String hash = headCommit.trackedFiles.get(filename);

        if (hash == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }

        File blob = Utils.join(BLOB_DIR, hash);
        File fileInCWD = Utils.join(CWD, filename);

        Utils.writeContents(fileInCWD, Utils.readContentsAsString(blob));
    }

    public static void checkoutCommitAndFilename(String commit, String filename) {
        String hash;
        Commit c = Commit.fromFile(commit);

        if (c != null) {
            hash = c.trackedFiles.get(filename);
            if (hash == null) {
                System.out.println("File does not exist in that commit.");
                return;
            }
        } else {
            System.out.println("No commit with that id exists.");
            return;
        }

        File blob = Utils.join(BLOB_DIR, hash);
        File fileInCWD = Utils.join(CWD, filename);

        Utils.writeContents(fileInCWD, Utils.readContentsAsString(blob));
    }

    public static void checkoutBranch(String checkoutBranch) {
        if (checkoutBranch.equals(Branch.getActiveBranch())) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        Commit activeBranchCommit = Branch.getActiveBranchHeadCommit();
        TreeMap<String, String> activeBranchTrackedFiles = activeBranchCommit.trackedFiles;

        Commit checkoutCommit = Branch.getBranchHeadCommit(checkoutBranch);
        if (checkoutCommit == null) {
            System.out.println("No such checkoutBranch exists.");
            return;
        }
        TreeMap<String, String> checkoutBranchTrackedFiles = checkoutCommit.trackedFiles;

        StagingArea stagingArea = StagingArea.fromFile();

        List<String> filesInCWD = Utils.plainFilenamesIn(CWD);

        assert filesInCWD != null;
        for (String file : filesInCWD) {
            if (!activeBranchTrackedFiles.containsKey(file) && !stagingArea.stagedForAddition.containsKey(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        for (Map.Entry<String, String> entry : checkoutCommit.trackedFiles.entrySet()) {
            String filename = entry.getKey();
            String hash = entry.getValue();

            File blob = Utils.join(BLOB_DIR, hash);
            File fileInCWD = Utils.join(CWD, filename);

            Utils.writeContents(fileInCWD, Utils.readContentsAsString(blob));
        }

        for (Map.Entry<String, String> entry : activeBranchCommit.trackedFiles.entrySet()) {
            if (!checkoutCommit.trackedFiles.containsKey(entry.getKey())) {
                File fileInCWD = Utils.join(CWD, entry.getKey());
                Utils.restrictedDelete(fileInCWD);
            }
        }

        stagingArea.clear();
        Branch.updateActiveBranch(checkoutBranch);
        Branch.updateHead(Branch.getActiveBranchHeadCommitHash());
    }

    public static void checkIfInitialized() {
        if (!GITLET_DIR.exists() || !GITLET_DIR.isDirectory()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

}
