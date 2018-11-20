package main;

import main.Net.ConnectionListener;
import main.Net.MessageReader;
import main.Net.MessageWriter;
import main.Until.Command;
import main.Until.CommandIdentifier;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;



public class Client implements ConnectionListener {

    private MessageReader messageReader;
    private MessageWriter messageWriter;
    private final Socket socket;

    private CommandIdentifier commandIdentifier = new CommandIdentifier();
    private static Logger logger = Logger.getLogger(Client.class);
    private boolean connecting = true;

    public static void main(String[] args) {
        try {
            Client client = new Client(new Socket("127.0.0.1", 8080));
            client.startClient();
        } catch (IOException e) {
            System.out.println("Сервер еще не запущен");
        }
    }

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        messageReader = new MessageReader(this, socket);
        messageWriter = new MessageWriter(this, socket);
    }


    public void startClient(){
        messageReader.start();
        Scanner sc = new Scanner(System.in);

        while(connecting) {
            writeMessage(sc.nextLine());
        }
    }

    public void writeMessage(String message){
        if (message.length() > 0) {
            Command status = commandIdentifier.checkCommand(message);
            messageWriter.send(message);
            if(status == Command.EXIT) {
                messageReader.close();
                messageWriter.close();

                if (socket.isClosed())
                    System.exit(0);
            }
        }

    }


    @Override
    public void onConnect(Socket connection) {
        System.out.println("Соединение установленно");
    }

    @Override
    public void onDisconnect(Socket connection) {
        if (connecting) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                connecting = false;
            }
            System.out.println("Соединение прерванно");
        }
    }

    @Override
    public void onSend(Socket connection, String message) {
        System.out.println(message);
    }

    @Override
    public void onException(Socket connection, Exception e) {
        logger.error("Error: ",e);
    }
}
