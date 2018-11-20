package main.Net;

import java.net.Socket;

public interface ConnectionListener {

    void onConnect(Socket socket);
    void onDisconnect(Socket socket);
    void onSend(Socket socket, String message);
    void onException(Socket socket, Exception e);
}
