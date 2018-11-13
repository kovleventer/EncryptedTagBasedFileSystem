import com.kovlev.etbf.data.Block;
import com.kovlev.etbf.util.ETBFSException;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class EncodingTests {
    @Test
    public void stringWrap() throws ETBFSException {
        String source = "kutya macska retek";
        Block b = new Block();

        b.putString(source, 12, 64);
        String ret = b.getString(12, 64);
        assertEquals(ret, source);
    }
}
