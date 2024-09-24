package gitlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *
 *  @author Resit
 */
public class Commit implements Serializable, Dumpable {

    private static final File COMMIT_DIR = Repository.COMMIT_DIR;

    /** The message of this Commit. */
    private String message;
    private Date commitDate;
    private String parent1;
    private String parent2;

    /** The files this commit tracks. Key is file name, value is hash of the file's contents. */
    public TreeMap<String, String> trackedFiles = new TreeMap<>();

    public Commit() {
        this.message = "inital commit";
        this.commitDate = new Date(0);
    }

    public Commit(String message, Date date, String parent) {
        this.message = message;
        this.commitDate = date;
        this.parent1 = parent;
    }

    public Commit(String message, Date date, String parent1, String parent2) {
        this.message = message;
        this.commitDate = date;
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public static String saveCommit(Commit c) {
        byte[] serializedCommit = serialize(c);
        String hash = Utils.sha1(serializedCommit);
        String abbr = hash.substring(0, 6);

        File folder = Utils.join(COMMIT_DIR, abbr);
        folder.mkdirs();
        File cFile = Utils.join(folder, hash);
        Utils.writeObject(cFile, c);

        return hash;
    }

    public static byte[] serialize(Commit c) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(c);
            out.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Commit fromFile(String hash) {
        String abbr = hash.substring(0, 6);
        File f = Utils.join(COMMIT_DIR, abbr, hash);

        if (!f.exists()) {
            return null;
        }

        return Utils.readObject(f, Commit.class);
    }

    public String getFirstParent() {
        return parent1;
    }

    public String getSecondParent() {
        return parent2;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        return formatter.format(commitDate);
    }

    public void dump() {
        System.out.println("Date: " + getFormattedDate());
        System.out.println(message);
        System.out.println();
    }
}
