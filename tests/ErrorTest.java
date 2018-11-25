import com.kovlev.etbf.data.EncryptedDataManager;
import com.kovlev.etbf.filesystem.File;
import com.kovlev.etbf.util.ETBFSException;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ErrorTest {
    String filename = "TEST.xcr";

    @Test(expected = ETBFSException.class)
    public void invalidPassword() throws Exception {
        EncryptedDataManager edm = new EncryptedDataManager(filename, "valid password");
        edm.close();
        edm = new EncryptedDataManager(filename, "invalid password");
    }

    @After
    public void removeFile() {
        assertTrue(new java.io.File(filename).delete());
    }
}
