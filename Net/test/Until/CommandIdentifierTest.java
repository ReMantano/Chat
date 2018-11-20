package Until;

import main.Until.Command;
import main.Until.CommandIdentifier;
import org.junit.Assert;
import org.junit.Test;

public class CommandIdentifierTest {

    @Test
    public void testCommandIdentifier(){

        Assert.assertEquals(checkCommandTest("\\exit"),Command.EXIT);
        Assert.assertEquals(checkCommandTest("\\leave"),Command.LEAVE);
        Assert.assertEquals(checkCommandTest("\\register"),Command.REGISTER);
        Assert.assertEquals(checkCommandTest("\\unknown"),Command.UNKNOWN);
        Assert.assertEquals(checkCommandTest("Hello"),Command.NULL);


    }

    private Command checkCommandTest(String command){
        CommandIdentifier commandIdentifier = new CommandIdentifier();

        return commandIdentifier.checkCommand(command);
    }

}
