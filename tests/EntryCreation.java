import com.kovlev.etbf.data.EncryptedDataManager;
import com.kovlev.etbf.filesystem.File;
import com.kovlev.etbf.filesystem.Tag;
import com.kovlev.etbf.util.ETBFSException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntryCreation {

    private EncryptedDataManager edm;
    private String filename = "TEST.xcr";
    private String password = "TESTPASSWORD";

    @Before
    public void createEDM() throws Exception {
        edm = new EncryptedDataManager(filename, password);
    }

    @Test
    public void createFiles() throws Exception {
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add("dog.png");
        filenames.add("cat.jpg");
        filenames.add("config.conf");

        for (String s : filenames) {
            edm.getFileManager().addFile(s);
        }

        assertEquals(edm.getMasterBlockManager().getFileCount(), filenames.size());

        edm.close();
        edm = new EncryptedDataManager(filename, password);

        for (File f : edm.getFileManager().getFiles()) {
            assertTrue(filenames.contains(f.getFilename()));
            filenames.remove(f.getFilename());
        }
    }

    @Test
    public void createTags() throws Exception {
        ArrayList<String> tagnames = new ArrayList<>();
        tagnames.add("animal");
        tagnames.add("human");
        tagnames.add("textfile");

        for (String s : tagnames) {
            edm.getTagManager().addTag(s);
        }

        assertEquals(edm.getMasterBlockManager().getTagCount(), tagnames.size());

        edm.close();
        edm = new EncryptedDataManager(filename, password);

        for (Tag t : edm.getTagManager().getTags()) {
            assertTrue(tagnames.contains(t.getTagString()));
            tagnames.remove(t.getTagString());
        }
    }

    @Test
    public void createRelations() throws Exception {
        String[] filenames = { "dog.png", "cat.jpg", "config.conf", "kitten.tiff" };
        String[] tagnames = { "animal", "cat", "text" };
        String[] relations = { "dog.png~animal", "cat.jpg~animal", "kitten.tiff~animal", "cat.jpg~cat", "kitten.tiff~cat", "config.conf~text"};

        for (String s : filenames) {
            edm.getFileManager().addFile(s);
        }

        for (String s : tagnames) {
            edm.getTagManager().addTag(s);
        }

        for (String s : relations) {
            String[] splitted = s.split("~");
            edm.getRelationManager().addRelation(splitted[0], splitted[1]);
        }

        edm.close();
        edm = new EncryptedDataManager(filename, password);

        assertEquals(edm.getMasterBlockManager().getFileCount(), filenames.length);
        assertEquals(edm.getMasterBlockManager().getTagCount(), tagnames.length);
        assertEquals(edm.getMasterBlockManager().getRelationCount(), relations.length);

        for (File f : edm.getRelationManager().getFilesWithTag("cat")) {
            assertTrue(f.getFilename().equals("cat.jpg") || f.getFilename().equals("kitten.tiff"));
        }

        for (Tag t : edm.getRelationManager().getTagsOfFile("kitten.tiff")) {
            System.out.println(t);
            assertTrue(t.getTagString().equals("cat") || t.getTagString().equals("animal"));
        }
    }

    @Test
    public void writeDataToFile() throws Exception {
        String fname = "test.txt";
        byte[] data = {0, 5, 6, 8, 9, 7, 14, 56, 32, 58, -52, 14, 6, 7};
        edm.getFileManager().addFile(fname);
        edm.getFileManager().getFile(fname).getOS().write(data);
        edm.close();

        edm = new EncryptedDataManager(filename, password);
        File f = edm.getFileManager().getFile(fname);
        assertEquals(f.getFilename(), fname);
        byte[] readBack = new byte[data.length];
        f.getIS().read(readBack);
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], readBack[i]);
        }
    }

    @Test
    public void writeLotOfDataToFile() throws Exception {
        String fname = "test.txt";
        Random r = new Random();
        int size = r.nextInt(50000);
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = (byte) r.nextInt(256);
        }
        edm.getFileManager().addFile(fname);
        edm.getFileManager().getFile(fname).getOS().write(data);
        edm.close();

        edm = new EncryptedDataManager(filename, password);
        File f = edm.getFileManager().getFile(fname);
        assertEquals(f.getFilename(), fname);
        assertEquals(f.getFileSize(), size);
        byte[] readBack = new byte[size];
        f.getIS().read(readBack);
        f.getIS().close();
        for (int i = 0; i < size; i++) {
            assertEquals(data[i], readBack[i]);
        }
    }

    @After
    public void closeEDM() throws Exception {
        edm.close();
        assertTrue(new java.io.File(filename).delete());
    }
}
