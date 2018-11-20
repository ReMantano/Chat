package main.java;

import main.Net.MessageWriter;
import main.Until.Profile;
import main.Until.Status;

public class LeaveUserFromChat {

    public synchronized void leave(MessageWriter mWriter){
        Profile prof = Server.connectionMap.get(mWriter);
        MessageWriter temp = prof.getConnection();

        String name = prof.getName();

        prof.setConnection(null);
        Server.connectionMap.get(temp).setConnection(null);

        temp.send(name + " вышел из чата");
        mWriter.send("Вы вышли из чата");

        if (prof.getStatus() == Status.AGENT)
            Server.agentList.add(mWriter);
        else
            Server.agentList.add(temp);

    }

}
