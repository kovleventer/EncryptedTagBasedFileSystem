import com.kovlev.etbf.data.EncryptedDataManager;
import com.kovlev.etbf.filesystem.File;
import com.kovlev.etbf.util.ETBFSException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntryCreation {

    private EncryptedDataManager edm;

    @Before
    public void createEDM() throws Exception {
        edm = new EncryptedDataManager("TEST.xcr", "TESTPASSWORD");
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

        for (File f : edm.getFileManager().getFiles()) {
            assertTrue(filenames.contains(f.getFilename()));
            filenames.remove(f.getFilename());
        }
    }

    @After
    public void closeEDM() throws Exception {
        edm.close();
        
    }
}
