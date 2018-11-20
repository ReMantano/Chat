package main.java;

import main.Net.ConnectionListener;
import main.Net.MessageReader;
import main.Net.MessageWriter;
import main.Until.Profile;
import main.Until.Status;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server implements ConnectionListener {


    private final ServerSocket serverSocket;
    public static final HashMap<MessageWriter, Profile> connectionMap = new HashMap<>();
    public static final HashMap<Socket, MessageWriter> messageWriterMap = new HashMap<>();
    public static ArrayList<MessageWriter> agentList = new ArrayList<>();
    public static ArrayList<MessageWriter> clientWaitList = new ArrayList<>();
    static final Logger log = Logger.getLogger(Server.class);

    private SystemCommand systemCommand = new SystemCommand();

    public static void main(String[] args) throws IOException {
        new Server(new ServerSocket(8080)).startServer();
    }


    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try(ServerSocket serverSocket = this.serverSocket) {
            log.info("Server running");
            while(!serverSocket.isClosed()){
                try{
                    Socket socket = serverSocket.accept();
                    MessageReader mReader = new MessageReader(this,socket);
                    MessageWriter mWriter = new MessageWriter(this,socket);

                    messageWriterMap.put(socket,mWriter);

                    mReader.start();
                }catch (IOException e){
                    log.error("Error: ",e);
                }
            }

        }catch (IOException e){
            log.error("Error: ",e);
        }
    }

    public void onConnect(Socket socket) {
        log.info("Connection: " + socket );
    }

    public synchronized void onDisconnect(Socket socket) {
        if (!socket.isClosed()) {
            try {
                socket.close();
                log.info("Disconnect: " + socket);
            } catch (IOException e) {
                log.error(e);
            }
        }

    }

    public void onException(Socket socket, Exception e) {
        log.error("Error: " + socket,e);
    }

    public void onSend(Socket socket, String message) {

        MessageWriter mWriter = messageWriterMap.get(socket);

        if (!systemCommand.checkCommand(mWriter,message)) {
            if (connectionMap.containsKey(mWriter))
                send(mWriter, message);
            else
                mWriter.send("Вы не зарегистрированны");
        }else if (agentList.size() > 0)
                createChatWithWaitClient();

    }


    private void send(MessageWriter mWriter, String message){
        Profile prof = connectionMap.get(mWriter);

        if (prof.getConnection() != null)
                prof.getConnection().send(prof.getName() + ": " + message);
        else
            if (prof.getStatus() == Status.AGENT)
                mWriter.send("Дождитесь клиента");
        else
            if (!connectionClientToAgent(prof,message)) {
                mWriter.send("Нет свободных агентов");
                prof.addMessageInVoid(message);
                if (!clientWaitList.contains(mWriter))
                    clientWaitList.add(mWriter);
            }

    }

    private boolean connectionClientToAgent(Profile prof, String message){
        if (agentList.size() > 0) {
            MessageWriter agent = agentList.remove(0);
            prof.setConnection(agent);

            connectionMap.get(agent).setConnection(prof.getSelfWriter());
            agent.send(prof.getName() + ": " + message);

            return true;
        }
        else
            return false;

    }

    private void createChatWithWaitClient(){
        if (clientWaitList.size() > 0) {
            MessageWriter client = clientWaitList.remove(0);
            MessageWriter agent = agentList.remove(0);
            Profile agentProfile = connectionMap.get(agent);

            agentProfile.setConnection(client);
            Profile clientProfile = connectionMap.get(client);
            clientProfile.setConnection(agent);

            client.send("Аген " + agentProfile.getName() + "готов r беседе");
            agent.send(clientProfile.getMessageInVoid() +
                    "\nВы переписываетесь с клиентом " +
                    clientProfile.getName());

            clientProfile.clearMessageInVoid();
        }
    }

}
