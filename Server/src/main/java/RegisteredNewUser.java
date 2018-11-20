package main.java;

import main.Net.MessageWriter;
import main.Until.Command;
import main.Until.Profile;
import main.Until.Status;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisteredNewUser {

    public synchronized void registered(String message, MessageWriter mWriter){
        int length = Command.REGISTER.name().length();
        String temp = message.substring(2 + length);
        ArrayList<String> commandList = new ArrayList<String>(Arrays.asList(temp.split(" ")));
        String status = commandList.remove(0).toUpperCase();
        String name = arrayListToString(commandList);

        Profile prof = new Profile(mWriter);
        prof.setName(name);

        try{
            addNewUser(prof, Status.valueOf(status));
        }catch (IllegalArgumentException e){
            mWriter.send("Вы указали неправильный статус");
            return;
        }

        Server.connectionMap.put(mWriter,prof);
        mWriter.send(name + "вы зарегистрировались как  " + prof.getStatus().toString().toLowerCase());
        Server.log.info(name + "зарегистрировался как " + prof.getStatus().toString());
    }

    private void addNewUser(Profile prof, Status status){
        if(status == Status.CLIENT)
            prof.setStatus(Status.CLIENT);
        else
        {
            prof.setStatus(Status.AGENT);
            Server.agentList.add(prof.getSelfWriter());
        }
    }

    private String arrayListToString(List<String> list){
        StringBuilder st = new StringBuilder();
        for(String s : list){
            st.append(s + " ");
        }
        return st.toString();
    }
}
