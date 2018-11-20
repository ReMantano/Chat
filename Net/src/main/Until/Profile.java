package main.Until;

import main.Net.MessageWriter;

import java.util.Objects;

public class Profile {

    private String name;
    private final MessageWriter selfWriter;
    private MessageWriter connection;
    private StringBuffer sendMessageInVoid = new StringBuffer();
    private Status status;

    public Profile(MessageWriter selfWriter){
        this.selfWriter = selfWriter;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public MessageWriter getSelfWriter() {
        return selfWriter;
    }

    public MessageWriter getConnection() {
        return connection;
    }

    public void setConnection(MessageWriter connection) {
        this.connection = connection;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public void setName(String name){
        this.name = name;
    }

    public void addMessageInVoid(String message){
        sendMessageInVoid.append(
                name+ ": "+
                message+ "\n");
    }

    public String getMessageInVoid(){
        return sendMessageInVoid.toString();
    }

    public void clearMessageInVoid(){
        sendMessageInVoid.setLength(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(name, profile.name) &&
                Objects.equals(status, profile.status) &&
                Objects.equals(selfWriter, profile.selfWriter) &&
                Objects.equals(connection, profile.connection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, status, selfWriter, connection);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", selfWriter=" + selfWriter +
                ", connection=" + connection +
                '}';
    }
}
