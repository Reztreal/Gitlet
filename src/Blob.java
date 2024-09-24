package gitlet;

import java.io.File;

public class Blob {
    public static final File BLOB_DIR = Repository.BLOB_DIR;

    public static String saveBlob(File f) {
        String s = Utils.readContentsAsString(f);
        String hash = Utils.sha1(s);

        File newBlob = Utils.join(BLOB_DIR, hash);
        if (newBlob.exists()) {
            System.out.println("Blob already exists.");
            return hash;
        }

        Utils.writeContents(newBlob, s);
        return hash;
    }

}
