import main.Net.MessageWriter;
import main.Until.Profile;
import main.Until.Status;
import main.java.Server;
import main.java.LeaveUserFromChat;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class testLeaveUserFromChat {

    @Test
    public void testWorkStatusClient(){
        MessageWriter mWriterTest1 = mock(MessageWriter.class);
        MessageWriter mWriterTest2 = mock(MessageWriter.class);

        Profile profileTest1 = new Profile(mWriterTest1);
        Profile profileTest2 = new Profile(mWriterTest2);

        profileTest1.setConnection(mWriterTest2);
        profileTest2.setConnection(mWriterTest1);

        profileTest1.setStatus(Status.CLIENT);
        profileTest2.setStatus(Status.AGENT);

        Server.connectionMap.put(mWriterTest1,profileTest1);
        Server.connectionMap.put(mWriterTest2,profileTest2);

        LeaveUserFromChat leaveUser = new LeaveUserFromChat();

        leaveUser.leave(mWriterTest1);



        Assert.assertTrue(Server.connectionMap.get(mWriterTest1).getConnection() == null);
        Assert.assertTrue(Server.connectionMap.get(mWriterTest2).getConnection() == null);

        Assert.assertTrue(Server.agentList.contains(mWriterTest2));
        Assert.assertFalse(Server.agentList.contains(mWriterTest1));

    }

}
