import main.Net.MessageWriter;
import main.Until.Profile;
import main.Until.Status;
import main.java.Server;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class testServer {
    ServerSocket serverSocket;
    Socket socket;
    MessageWriter mWriter;
    MessageWriter test;
    ByteArrayInputStream bais;
    ByteArrayOutputStream baos;
    String message = "Hi";
    Server server;


    @Before
    public void init() throws IOException {
         serverSocket = mock(ServerSocket.class);
         socket = mock(Socket.class);
         mWriter = mock(MessageWriter.class);
         test = mock(MessageWriter.class);
         bais = new ByteArrayInputStream(message.getBytes());
         baos = new ByteArrayOutputStream();

        when(serverSocket.accept()).thenReturn(socket);
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(true);
        when(socket.getInputStream()).thenReturn(bais);
        when(socket.getOutputStream()).thenReturn(baos);

        server = new Server(serverSocket);
    }

    @After
    public void clear(){
        Server.clientWaitList.clear();
        Server.agentList.clear();
        Server.connectionMap.clear();
        Server.messageWriterMap.clear();

        server.onDisconnect(socket);
    }


    @Test
    public void testServerStart() {
        server.startServer();

        Assert.assertTrue(Server.messageWriterMap.containsKey(socket));
    }

    @Test
    public void testSendMessageButUserNotRegistered()  {

        Server.messageWriterMap.put(socket,mWriter);
        server.onSend(socket,message);

        verify(mWriter).send("Вы не зарегистрированны");

    }

    @Test
    public void testSendMessage() {

        Profile profile = new Profile(mWriter);
        profile.setStatus(Status.CLIENT);
        profile.setConnection(test);
        Server.connectionMap.put(mWriter,profile);
        Server.messageWriterMap.put(socket,mWriter);

        server.onSend(socket,message);

        verify(test).send(profile.getName() + ": " + message);

    }

    @Test
    public void testAgentWantSendMessage() {

        Profile profile = new Profile(mWriter);
        profile.setStatus(Status.AGENT);
        Server.connectionMap.put(mWriter,profile);
        Server.messageWriterMap.put(socket,mWriter);

        server.onSend(socket,message);

        verify(mWriter).send("Дождитесь клиента");

    }

    @Test
    public void testClientWantSendMessageButNotAgent()  {

        Profile profile = new Profile(mWriter);
        profile.setStatus(Status.CLIENT);
        Server.connectionMap.put(mWriter,profile);
        Server.messageWriterMap.put(socket,mWriter);

        server.onSend(socket,message);

        verify(mWriter).send("Нет свободных агентов");
        Assert.assertTrue(profile.getMessageInVoid().equals(profile.getName() + ": "+message+"\n"));

    }

    @Test
    public void testConnectionClientToAgent()  {

        Profile profile = new Profile(mWriter);
        profile.setStatus(Status.CLIENT);
        Profile agent = new Profile(test);
        agent.setStatus(Status.AGENT);
        Server.connectionMap.put(mWriter,profile);
        Server.connectionMap.put(test,agent);
        Server.messageWriterMap.put(socket,mWriter);
        Server.agentList.add(test);

        server.onSend(socket,message);

        verify(test).send(profile.getName() + ": " + message);
        Assert.assertTrue(profile.getConnection() == test);
        Assert.assertTrue(agent.getConnection() == mWriter);
        Assert.assertFalse(Server.agentList.contains(test));

    }

    @Test
    public void testWhenAgentConnectToWaitClient()  {

        Profile profile = new Profile(mWriter);
        profile.setStatus(Status.CLIENT);

        Server.connectionMap.put(mWriter,profile);
        Server.clientWaitList.add(mWriter);

        server.startServer();
        server.onSend(socket,"\\register agent A");

        Profile agent = Server.connectionMap.get(profile.getConnection());

        Assert.assertFalse(Server.clientWaitList.contains(mWriter));
        Assert.assertFalse(Server.agentList.contains(agent.getSelfWriter()));
        Assert.assertTrue(agent.getConnection() == mWriter);
        Assert.assertTrue(profile.getConnection() == agent.getSelfWriter());

    }
}
