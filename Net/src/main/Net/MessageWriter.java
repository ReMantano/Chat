package main.Net;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Objects;

public class MessageWriter {

    private final ConnectionListener listener;
    private final Socket socket;
    private final BufferedWriter bWriter;


    public MessageWriter(ConnectionListener listener, Socket socket) throws IOException {
        this.listener = listener;
        this.socket = socket;
        this.bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
    }


    public void send(String message){
        try {
            bWriter.write(message + System.getProperty("line.separator"));
            bWriter.flush();
        } catch (IOException e) {
            listener.onException(socket, e);
            listener.onDisconnect(socket);
        }
    }


    public void close(){
        listener.onDisconnect(socket);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageWriter that = (MessageWriter) o;
        return Objects.equals(socket, that.socket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket);
    }
}
