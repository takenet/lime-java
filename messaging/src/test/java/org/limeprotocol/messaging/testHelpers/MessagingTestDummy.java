package org.limeprotocol.messaging.testHelpers;

import org.limeprotocol.MediaType;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.messaging.resource.Capability;

import static org.limeprotocol.testHelpers.TestDummy.*;

public class MessagingTestDummy {

    public static PlainText createTextContent()
    {
        return new PlainText(createRandomString(150));
    }

    public static Capability createCapability() {
        Capability capability = new Capability();
        capability.setContentTypes(
                new MediaType[] {createJsonMediaType(),
                        createJsonMediaType(),
                        createJsonMediaType()});
        capability.setResourceTypes(
                new MediaType[] {createJsonMediaType(),
                        createJsonMediaType(),
                        createJsonMediaType()});
        return capability;
    }

}
