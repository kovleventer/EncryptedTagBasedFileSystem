import com.kovlev.etbf.data.EncryptedDataManager;
import com.kovlev.etbf.filesystem.File;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class EntryCreation {
    // may not work but whatever
    @Test
    public void addFile() throws Exception {
        String[] fnames = {".gitignore", "EncryptedTagBasedFileSystem.iml"};
        EncryptedDataManager edm = new EncryptedDataManager("xd.xcr", "pass");
        for (String fname : fnames) {
            edm.getFileManager().addFile(fname);

        }
        for (String fname : fnames) {
            edm.getFileManager().getFile(fname).getOS().write(Files.readAllBytes(Paths.get(fname)));
        }
        byte[] imgData = Files.readAllBytes(Paths.get("asd.jpg"));
        edm.getFileManager().getFile("asd2.jpg").getOS().write(imgData);



        edm.flush();

        edm = new EncryptedDataManager("xd.xcr", "pass");

        for (String fname : fnames) {
            BufferedReader br = new BufferedReader(new InputStreamReader(edm.getFileManager().getFile(fname).getIS()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }

        edm.flush();
        //Files.delete(Paths.get("kecske.xcr"));
    }

    @Test
    public void addTags() throws Exception {
        EncryptedDataManager edm = new EncryptedDataManager("kutya.xcr", "");
        for (int i = 0; i < 15; i++) {
            edm.getTagManager().addTag("tag#" + i);
        }
        edm.flush();

        edm = new EncryptedDataManager("kutya.xcr", "");
        for (int i = 0; i < 15; i++) {
            System.out.println(edm.getTagManager().getTag("tag#" + i));
        }
    }

    @Test
    public void addRelations() throws Exception {
        EncryptedDataManager edm = new EncryptedDataManager("shit.xcr", "asd");
        edm.getFileManager().addFile("korcs.jpg");
        edm.getFileManager().addFile("családi album.png.tar.gz");
        edm.getTagManager().addTag("kutya");
        edm.getTagManager().addTag("eb");
        edm.getTagManager().addTag("gyerek");
        edm.getTagManager().addTag("unikód tesztőáűú");
        edm.getTagManager().addTag("family");
        edm.getRelationManager().addRelation("korcs.jpg", "kutya");
        edm.getRelationManager().addRelation("korcs.jpg", "eb");
        edm.getRelationManager().addRelation("családi album.png.tar.gz", "family");
        edm.getRelationManager().addRelation("családi album.png.tar.gz", "gyerek");
        edm.getRelationManager().addRelation("családi album.png.tar.gz", "eb");

        Set<File> fs = edm.getRelationManager().getFilesWithTag("eb");
        for (File f : fs) {
            System.out.println("eb : " + f.getFilename());
        }



        edm.flush();
    }
}
