package Net;

import main.Net.ConnectionListener;
import main.Net.MessageReader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class testMessageReader {

    private ConnectionListener listener;
    private ConnectionListener lis;
    private Socket socket;
    private ByteArrayInputStream bais;
    private IOException ex;

    @Before
    public void init(){

        lis = new ConnectionListener() {
            @Override
            public void onConnect(Socket socket) {

            }

            @Override
            public void onDisconnect(Socket socket) {

            }

            @Override
            public void onSend(Socket socket, String message) {

            }

            @Override
            public void onException(Socket socket, Exception e) {
                ex = (IOException) e;
            }
        };
        listener = Mockito.spy(lis);
        socket = mock(Socket.class);
        bais = mock(ByteArrayInputStream.class);

    }

    @Test
    public void testOnReadMessage() throws IOException {
        bais = new ByteArrayInputStream("message".getBytes());
        when(socket.getInputStream()).thenReturn(bais);
        when(socket.isClosed()).thenReturn(false).thenReturn(true);

        MessageReader mReader = new MessageReader(listener,socket);
        mReader.run();

        Mockito.verify(listener).onSend(socket,"message");

    }

    @Test
    public void testOnErrorReadMessage() throws IOException {

        NullPointerException exception = new NullPointerException();

        Mockito.doThrow(exception).when(bais).read();
        when(socket.getInputStream()).thenReturn(bais);


        MessageReader mReader = new MessageReader(listener,socket);
        mReader.run();

        Mockito.verify(listener).onException(socket,ex);
        Mockito.verify(listener).onDisconnect(socket);

    }
}
