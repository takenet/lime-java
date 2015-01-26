package org.limeprotocol;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

public class IdentityTest {

    @Test
    public void parse_CompleteString_ReturnsValidIdentity() {
        // Arrange
        String name = "myName";
        String domain = "myDomain";
        String identityString = String.format("%1$s@%2$s", name, domain);

        // Act
        Identity identity = Identity.parse(identityString);

        // Arrange
        assertEquals(name, identity.getName());
        assertEquals(domain, identity.getDomain());
    }
}