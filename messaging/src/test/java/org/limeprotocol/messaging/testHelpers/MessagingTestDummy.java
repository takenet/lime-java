package org.limeprotocol.messaging.testHelpers;

import org.limeprotocol.messaging.contents.PlainText;
import static org.limeprotocol.testHelpers.TestDummy.*;

public class MessagingTestDummy {

    public static PlainText createTextContent()
    {
        return new PlainText(createRandomString(150));
    }
}
