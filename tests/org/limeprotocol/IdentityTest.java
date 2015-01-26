package org.limeprotocol;

import org.junit.Test;
import org.limeprotocol.exceptions.ArgumentNullException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class IdentityTest {

    @Test
    public void equals_EqualsIdentities_ReturnsTrue() {
        // Arrange
        Identity identity1 = new Identity("thisIsName", "thisIsDomain");

        Identity identity2 = new Identity(identity1.getName(), identity1.getDomain());

        // Assert
        assertEquals(identity1, identity2);
        assertEquals(identity2, identity1);
    }

    @Test
    public void equals_EqualsIdentitiesDifferentCasing_ReturnsTrue() {
        // Arrange
        Identity identity1 = new Identity("thisIsName".toUpperCase(), "thisIsDomain".toUpperCase());

        Identity identity2 = new Identity(identity1.getName().toLowerCase(), identity1.getDomain().toLowerCase());

        // Assert
        assertEquals(identity1, identity2);
        assertEquals(identity2, identity1);
    }

    @Test
    public void equals_EqualsIdentitiesNullDomain_ReturnsTrue() {
        // Arrange
        Identity identity1 = new Identity("thisIsName", null);

        Identity identity2 = new Identity(identity1.getName(), null);

        // Assert
        assertEquals(identity1, identity2);
        assertEquals(identity2, identity1);
    }

    @Test
    public void equals_EqualsIdentitiesNullName_ReturnsTrue() {
        // Arrange
        Identity identity1 = new Identity(null, "thisIsDomain");

        Identity identity2 = new Identity(null, identity1.getDomain());

        // Assert
        assertEquals(identity1, identity2);
        assertEquals(identity2, identity1);
    }

    @Test
    public void Equals_NotEqualsIdentities_ReturnsFalse() {
        // Arrange
        Identity identity1 = new Identity("thisIsName", "thisIsDomain");

        Identity identity2 = new Identity("thisIsOtherName", "thisIsDomain");

        // Assert
        assertNotEquals(identity1, identity2);
        assertNotEquals(identity2, identity1);
    }

    @Test
    public void equals_NotEqualsIdentitiesNullName_ReturnsFalse() {
        // Arrange
        Identity identity1 = new Identity(null, "thisIsDomain");

        Identity identity2 = new Identity("thisIsOtherName", "thisIsOtherDomain");

        // Assert
        assertNotEquals(identity1, identity2);
        assertNotEquals(identity2, identity1);
    }

    @Test
    public void Equals_NotEqualsIdentitiesNullDomain_ReturnsFalse() {
        // Arrange
        Identity identity1 = new Identity("thisIsName", null);

        Identity identity2 = new Identity("thisIsOtherName", "thisIsDomain");

        // Assert
        assertNotEquals(identity1, identity2);
        assertNotEquals(identity2, identity1);
    }

    @Test
    public void Equals_NotEqualsIdentitiesNullProperties_ReturnsFalse() {
        // Arrange
        Identity identity1 = new Identity(null, null);

        Identity identity2 = new Identity("thisIsOtherName", "thisIsDomain");

        // Assert
        assertNotEquals(identity1, identity2);
        assertNotEquals(identity2, identity1);
    }

    @Test
    public void getHashCode_EqualsIdentities_ReturnsSameHash() {
        // Arrange
        Identity identity1 = new Identity("thisIsName", "thisIsDomain");

        Identity identity2 = new Identity(identity1.getName(), identity1.getDomain());

        // Assert
        assertEquals(identity1.hashCode(), identity2.hashCode());
    }

    @Test
    public void getHashCode_EqualsIdentitiesDifferentCasing_ReturnsSameHash() {
        // Arrange
        Identity identity1 = new Identity("thisIsName", "thisIsDomain");

        Identity identity2 = new Identity(identity1.getName().toLowerCase(), identity1.getDomain().toLowerCase());

        // Assert
        assertEquals(identity1.hashCode(), identity2.hashCode());
    }

    @Test
    public void getHashCode_NotEqualsIdentities_ReturnsDifferentHash() {
        // Arrange
        Identity identity1 = new Identity("thisIsName", "thisIsDomain");

        Identity identity2 = new Identity("thisIsOtherName", "thisIsDomain");

        // Assert
        assertNotEquals(identity1.hashCode(), identity2.hashCode());
    }

    @Test
    public void toString_CompleteIdentity_ReturnsValidString() {
        // Arrange
        String name = "thisIsName";
        String domain = "thisIsDomain";

        Identity identity = new Identity(name, domain);

        String expectedResult = name + "@" + domain;

        // Act
        String identityString = identity.toString();

        // Assert
        assertEquals(expectedResult, identityString);
    }

    @Test
    public void toString_OnlyNameIdentity_ReturnsValidString() {
        // Arrange
        String name = "thisIsName";
        String domain = null;

        Identity identity = new Identity(name, domain);

        String expectedResult = name;

        // Act
        String identityString = identity.toString();

        // Assert
        assertEquals(expectedResult, identityString);
    }

    @Test
    public void toString_OnlyDomainIdentity_ReturnsValidString() {
        // Arrange
        String name = null;
        String domain = "thisIsDomain";

        Identity identity = new Identity(name, domain);

        String expectedResult = "@" + domain;

        // Act
        String identityString = identity.toString();

        // Assert
        assertEquals(expectedResult, identityString);
    }

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

    @Test
    public void parse_OnlyNameString_ReturnsValidIdentity() {
        // Arrange
        String name = "anyName";

        // Act
        Identity identity = Identity.parse(name);

        // Arrange
        assertEquals(name, identity.getName());
        assertNull(identity.getDomain());
    }

    @Test
    public void parse_OnlyDomain_ReturnsValidIdentity() {
        // Arrange
        String domain = "theBestDomain";
        String identityString = String.format("@%1$s", domain);

        // Act
        Identity identity = Identity.parse(identityString);

        // Arrange
        assertEquals(domain, identity.getDomain());
        assertNull(identity.getName());
    }

    @Test(expected = ArgumentNullException.class)
    public void parse_NullString_ThrowsArgumentNullException() {
        // Arrange
        String identityString = null;

        // Act
        Identity identity = Identity.parse(identityString);
    }

    @Test(expected = ArgumentNullException.class)
    public void parse_EmptyString_ThrowsArgumentNullException() {
        // Arrange
        String identityString = "";

        // Act
        Identity identity = Identity.parse(identityString);
    }
}