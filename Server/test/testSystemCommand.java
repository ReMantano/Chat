import main.Net.MessageWriter;
import main.Until.Profile;
import main.java.Server;
import main.java.SystemCommand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class testSystemCommand {

    MessageWriter mWriter;
    Profile profile;
    SystemCommand sc;

    @Before
    public void init(){
        mWriter = mock(MessageWriter.class);
        sc = new SystemCommand();
        profile = new Profile(mWriter);
    }

    @Test
    public void testOnRegister(){

        String message = "\\register UNKNOWN ";
        Server.connectionMap.put(mWriter,profile);

        boolean commandExist = sc.checkCommand(mWriter,message);

        Assert.assertTrue(commandExist);
        Mockito.verify(mWriter).send("Вы уже зарегистрированы");

    }

    @Test
    public void testOnLeave(){
        String message = "\\leave";

        boolean commandExist = sc.checkCommand(mWriter,message);

        Assert.assertTrue(commandExist);
        Mockito.verify(mWriter).send("У вас нет собеседника");

    }

    @Test
    public void testOnExit(){
        String message = "\\exit";
        Server.connectionMap.put(mWriter,profile);
        Server.agentList.add(mWriter);
        Server.clientWaitList.add(mWriter);

        boolean commandExist = sc.checkCommand(mWriter,message);

        Assert.assertTrue(commandExist);
        Assert.assertFalse(Server.connectionMap.containsKey(mWriter));
        Assert.assertFalse(Server.agentList.contains(mWriter));
        Assert.assertFalse(Server.clientWaitList.contains(mWriter));

        Mockito.verify(mWriter).close();

    }

    @Test
    public void testOnSendMessage(){
        String message = "Hello";

        boolean commandExist = sc.checkCommand(mWriter,message);

        Assert.assertFalse(commandExist);
    }

    @Test
    public void testOnUnknownCommand(){
        String message = "\\unknown";

        boolean commandExist = sc.checkCommand(mWriter,message);

        Assert.assertTrue(commandExist);
        Mockito.verify(mWriter).send("Неизвестная комманда");;
    }
}
