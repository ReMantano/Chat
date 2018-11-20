package main.Net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;

public class MessageReader extends Thread {

    private final ConnectionListener listener;
    private final Socket socket;
    private final BufferedReader bReader;

    public MessageReader(ConnectionListener listener,Socket socket) throws IOException {
        this.listener = listener;
        this.socket = socket;
        this.bReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
    }

    private void read(){
        try {
            listener.onConnect(socket);
            while(!socket.isClosed()) {
                String s = bReader.readLine();
                if (s !=null)
                    listener.onSend(socket, s);
            }
       } catch (IOException e) {
            listener.onException(socket,e);
        }finally {
                if(!socket.isClosed())
                    listener.onDisconnect(socket);
        }
    }

    public void close(){
        listener.onDisconnect(socket);
        interrupt();

    }

    @Override
    public void run() {
        read();
    }
}
