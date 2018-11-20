import main.Client;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestClient {
    ByteArrayOutputStream baos;
    ByteArrayInputStream bai;
    String testMessage;
    ByteArrayInputStream bais;

    @Before
    public void init(){
         baos = new ByteArrayOutputStream();
         bai = new ByteArrayInputStream("".getBytes());

         testMessage = String.format(
                        "Hello"+
                        System.getProperty("line.separator")+
                        "\\exit");

         bais = new ByteArrayInputStream(testMessage.getBytes());
        System.setIn(bais);
    }

    @Test
    public void testOnClientWriteMessage() throws IOException {



        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(bai);
        when(socket.getOutputStream()).thenReturn(baos);
        when(socket.isClosed()).thenReturn(false);

        Client client = new Client(socket);
        client.startClient();

        System.out.println("WOT TUT"+baos.toString());
        Assert.assertEquals(baos.toString(),testMessage + System.getProperty("line.separator"));
        verify(socket).close();

    }
}
