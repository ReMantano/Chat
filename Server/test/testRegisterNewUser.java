import main.Net.MessageWriter;
import main.Until.Profile;
import main.Until.Status;
import main.java.RegisteredNewUser;
import main.java.Server;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class testRegisterNewUser {

    @Test
    public void testRegister(){
        reg("client","SAME",mock(MessageWriter.class));
        reg("agent","SAME",mock(MessageWriter.class));
    }

    @Test
    public void testOnStatusError() {
        String text = "\\register unknown unknown";
        MessageWriter mWriter = mock(MessageWriter.class);
        RegisteredNewUser register = new RegisteredNewUser();

        register.registered(text, mWriter);

        Mockito.verify(mWriter).send("Вы указали неправильный статус");
    }

    public void reg(String status, String name, MessageWriter mWriter){

        String text = "\\register " + status + " " + name;
        RegisteredNewUser register = new RegisteredNewUser();

        register.registered(text, mWriter);


        Profile prof = Server.connectionMap.get(mWriter);

        Assert.assertTrue(prof != null);
        Assert.assertTrue(prof.getName().equals(name + " "));
        Assert.assertTrue(prof.getStatus().name().equals(status.toUpperCase()));

        if (status.equals("agent"))
            Assert.assertTrue(Server.agentList.contains(mWriter));
        else
            Assert.assertFalse(Server.agentList.contains(mWriter));
    }

}
