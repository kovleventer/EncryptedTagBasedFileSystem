import com.kovlev.etbf.data.EncryptedDataManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntryModification {

    private EncryptedDataManager edm;
    private String edmFilename = "TEST.xcr";
    private String password = "TESTPASSWORD";

    private String filename = "original.file";

    @Before
    public void createEDM() throws Exception {
        edm = new EncryptedDataManager(edmFilename, password);
        edm.getFileManager().addFile(filename);
    }

    @Test
    public void renameFile() throws Exception {
        String modified1 = "modifieeeed_1.file";
        edm.getFileManager().modifyFileName(edm.getFileManager().getFile(filename), modified1);
        System.out.println(edm.getFileManager().getFiles());
        assertEquals(modified1, edm.getFileManager().getFile(modified1).getFilename());
        edm.close();
        edm = new EncryptedDataManager(edmFilename, password);
        String modified2 = "mod2.file";
        assertEquals(edm.getFileManager().getFile(modified1).getFilename(), modified1);
        edm.getFileManager().modifyFileName(edm.getFileManager().getFile(modified1), modified2);
        assertEquals(modified2, edm.getFileManager().getFile(modified2).getFilename());
    }

    @After
    public void closeEDM() throws Exception {
        edm.close();
        assertTrue(new java.io.File(edmFilename).delete());
    }
}
