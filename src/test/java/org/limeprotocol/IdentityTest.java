package org.limeprotocol;

import org.junit.Test;
import org.limeprotocol.Identity;
import org.limeprotocol.util.StringUtils;

import static org.junit.Assert.*;

public class IdentityTest {

    //region equals method
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
    public void equals_NotEqualsIdentities_ReturnsFalse() {
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
    public void equals_NotEqualsIdentitiesNullDomain_ReturnsFalse() {
        // Arrange
        Identity identity1 = new Identity("thisIsName", null);

        Identity identity2 = new Identity("thisIsOtherName", "thisIsDomain");

        // Assert
        assertNotEquals(identity1, identity2);
        assertNotEquals(identity2, identity1);
    }

    @Test
    public void equals_NotEqualsIdentitiesNullProperties_ReturnsFalse() {
        // Arrange
        Identity identity1 = new Identity(null, null);

        Identity identity2 = new Identity("thisIsOtherName", "thisIsDomain");

        // Assert
        assertNotEquals(identity1, identity2);
        assertNotEquals(identity2, identity1);
    }

    @Test
    public void equals_CompareToNull_ReturnsFalse(){
        // Arrange
        Identity identity = new Identity(null, null);

        // Act and Assert
        assertFalse("equals should return false when comparing to null", identity.equals(null));
    }
    //endregion

    //region getHashCode method
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
    //endregion

    //region toString method
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
    //endregion

    //region parse method
    @Test
    public void parse_CompleteString_ReturnsValidIdentity() {
        // Arrange
        String name = "myName";
        String domain = "myDomain";
        String identityString = StringUtils.format("{0}@{1}", name, domain);

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
        String identityString = StringUtils.format("@{0}", domain);

        // Act
        Identity identity = Identity.parse(identityString);

        // Arrange
        assertEquals(domain, identity.getDomain());
        assertNull(identity.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_NullString_ThrowsArgumentNullException() {
        // Arrange
        String identityString = null;

        // Act
        Identity identity = Identity.parse(identityString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_EmptyString_ThrowsArgumentNullException() {
        // Arrange
        String identityString = "";

        // Act
        Identity identity = Identity.parse(identityString);
    }
    //endregion
}