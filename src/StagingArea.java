package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

public class StagingArea implements Serializable {
    public static final File STAGING_AREA = Repository.STAGING_AREA;

    public TreeMap<String, String> stagedForAddition = new TreeMap<>();
    public TreeMap<String, String> stagedForRemoval = new TreeMap<>();

    public static StagingArea fromFile() {
        File stFile = Utils.join(STAGING_AREA);

        return Utils.readObject(stFile, StagingArea.class);
    }

    public static void saveStagingArea(StagingArea stagingArea) {
        Utils.writeObject(STAGING_AREA, stagingArea);
    }

    public static void add(String fileName, String hash) {
        StagingArea s = fromFile();
        s.stagedForAddition.put(fileName, hash);
        saveStagingArea(s);
    }

    public void clear() {
        stagedForAddition.clear();
        stagedForRemoval.clear();
    }

    public static void printStagingArea() {
        StagingArea s = fromFile();
        System.out.println("Staged to add: ");
        System.out.println(s.stagedForAddition);
        System.out.println("Staged to remove: ");
        System.out.println(s.stagedForRemoval);
    }

}
