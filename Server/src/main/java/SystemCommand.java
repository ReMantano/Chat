package main.java;

import main.Net.MessageWriter;
import main.Until.Command;
import main.Until.CommandIdentifier;
import main.Until.Profile;

public class SystemCommand {

    private CommandIdentifier commandIdentifier = new CommandIdentifier();
    private RegisteredNewUser commandRegister = new RegisteredNewUser();
    private LeaveUserFromChat commandLeave = new LeaveUserFromChat();

    public boolean checkCommand(MessageWriter mWriter, String message){
        Command status = commandIdentifier.checkCommand(message);
        switch(status){
            case REGISTER:{
                if (!Server.connectionMap.containsKey(mWriter))
                    commandRegister.registered(message,mWriter);
                else
                    mWriter.send("Вы уже зарегистрированы");
                return true;
            }
            case LEAVE:{
                Profile prof =  Server.connectionMap.get(mWriter);
                if (prof != null && prof.getConnection() != null)
                    commandLeave.leave(mWriter);
                else
                    mWriter.send("У вас нет собеседника");

                return true;
            }
            case EXIT:{
                Profile prof =  Server.connectionMap.get(mWriter);
                if (prof != null && prof.getConnection() != null)
                    commandLeave.leave(mWriter);

                exitUserFromCaht(mWriter);

                return true;
            }
            case UNKNOWN:{
                mWriter.send("Неизвестная комманда");
                return true;
            }
        }
        return false;
    }

    private synchronized void exitUserFromCaht(MessageWriter mWriter){
        Server.agentList.remove(mWriter);
        Server.clientWaitList.remove(mWriter);
        Server.connectionMap.remove(mWriter);
        mWriter.close();
    }
}
