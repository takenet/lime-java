package limeprotocol;

import org.junit.Test;
import org.limeprotocol.Identity;
import org.limeprotocol.LimeUri;
import org.limeprotocol.util.StringUtils;

import java.net.URI;

import static org.fest.assertions.api.Assertions.assertThat;

public class LimeUriTest {

    //region parse method

    @Test
    public void parse_ValidRelativeString_ReturnsInstance() {
        // Arrange
        String resourceName = "aResource";
        String relativePath = "/" + resourceName;

        // Act
        LimeUri actual = LimeUri.parse(relativePath);

        // Assert
        assertThat(actual.getPath()).isNotNull();
        assertThat(actual.getPath()).isEqualTo(relativePath);
        assertThat(actual.isRelative()).isTrue();
    }

    @Test
    public void parse_ValidAbsoluteString_ReturnsInstance() {
        // Arrange
        Identity identity = new Identity("myName", "theDomain");
        String resourceName = "aResource";
        String absolutePath = StringUtils.format("{0}://{1}/{2}", LimeUri.LIME_URI_SCHEME, identity, resourceName);

        // Act
        LimeUri actual = LimeUri.parse(absolutePath);

        // Assert
        assertThat(actual.getPath()).isNotNull();
        assertThat(actual.getPath()).isEqualTo(absolutePath);
        assertThat(actual.isRelative()).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_NullString_ThrowsArgumentNullException() {
        // Arrange
        String path = null;

        // Act
        LimeUri actual = LimeUri.parse(path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_InvalidRelativeString_ThrowsArgumentException() {
        // Arrange
        String resourceName = "transaction";
        String invalidPath = StringUtils.format("\\{0}", resourceName);

        // Act
        LimeUri actual = LimeUri.parse(invalidPath);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_InvalidSchemeAbsoluteString_ThrowsArgumentException() {
        // Arrange
        String absolutePath = "http://server@limeprotocol.org/presence";

        // Act
        LimeUri actual = LimeUri.parse(absolutePath);
    }
    //endregion

    //region toUri methods
    @Test
    public void toUri_AbsoluteInstance_ReturnsUri() {
        // Arrange
        Identity identity = new Identity("myName", "theDomain");
        String resourceName = "presence";
        String absolutePath = StringUtils.format("{0}://{1}/{2}", LimeUri.LIME_URI_SCHEME, identity, resourceName);
        LimeUri limeUri = LimeUri.parse(absolutePath);

        // Act
        URI uri = limeUri.toUri();

        // Assert
        assertThat(uri.getScheme()).isEqualTo(LimeUri.LIME_URI_SCHEME);
        assertThat(uri.getUserInfo()).isEqualTo(identity.getName());
        assertThat(uri.getHost()).isEqualTo(identity.getDomain());
        assertThat(uri.getAuthority()).isEqualTo(identity.toString());
        assertThat(uri.getPath()).isEqualTo("/" + resourceName);
    }

    @Test(expected = IllegalStateException.class)
    public void toUri_RelativeInstance_ThrowsInvalidOperationException() {
        String resourceName = "dataRes";
        String relative = StringUtils.format("/{0}", resourceName);
        LimeUri limeUri = LimeUri.parse(relative);

        // Act
        limeUri.toUri();
    }

    @Test
    public void toUriIdentity_RelativeInstance_ReturnsUri() {
        // Arrange
        Identity identity = new Identity("myName", "theDomain");
        String resourceName = "presence";
        String relative = StringUtils.format("/{0}", resourceName);

        LimeUri limeUri = LimeUri.parse(relative);

        // Act
        URI uri = limeUri.toUri(identity);

        // Assert
        assertThat(uri.getScheme()).isEqualTo(LimeUri.LIME_URI_SCHEME);
        assertThat(uri.getUserInfo()).isEqualTo(identity.getName());
        assertThat(uri.getHost()).isEqualTo(identity.getDomain());
        assertThat(uri.getAuthority()).isEqualTo(identity.toString());
        assertThat(uri.getPath()).isEqualTo(relative);
    }

    @Test(expected = IllegalStateException.class)
    public void toUriIdentity_AbsoluteInstance_ThrowsInvalidOperationException() {
        // Arrange
        Identity identity = new Identity("myName", "theDomain");
        String resourceName = "presence";
        String absolutePath = StringUtils.format("{0}://{1}/{2}", LimeUri.LIME_URI_SCHEME, identity, resourceName);
        LimeUri limeUri = LimeUri.parse(absolutePath);

        // Act
        limeUri.toUri(identity);
    }
    //endregion

}