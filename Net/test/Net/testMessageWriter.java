package Net;

import main.Net.ConnectionListener;
import main.Net.MessageWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class testMessageWriter {

    private ConnectionListener listener;
    private Socket socket;
    private ByteArrayOutputStream baos;


    @Before
    public void init(){
         listener = mock(ConnectionListener.class);
         socket = mock(Socket.class);
         baos = new ByteArrayOutputStream();
    }

    @Test
    public void testSendMessage() throws IOException {
        when(socket.getOutputStream()).thenReturn(baos);
        MessageWriter mWriter = new MessageWriter(listener,socket);
        String message = "hello";

        mWriter.send(message);

        Assert.assertEquals(message + System.getProperty("line.separator"),baos.toString());

    }

    @Test
    public void testOnErrorSendMessage() throws IOException {
        ByteArrayOutputStream baos = mock(ByteArrayOutputStream.class);
        IOException exception = new IOException();
        when(socket.getOutputStream()).thenReturn(baos);
        Mockito.doThrow(exception).when(baos).flush();
        MessageWriter mWriter = new MessageWriter(listener,socket);

        mWriter.send("");

        Mockito.verify(listener).onException(socket,exception);
        Mockito.verify(listener).onDisconnect(socket);
    }

    @Test
    public void testOnClose() throws IOException {
        when(socket.getOutputStream()).thenReturn(baos);
        MessageWriter mWriter = new MessageWriter(listener,socket);

        mWriter.close();

        Mockito.verify(listener).onDisconnect(socket);
    }
}
