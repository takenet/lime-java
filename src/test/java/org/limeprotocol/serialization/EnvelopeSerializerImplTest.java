package org.limeprotocol.serialization;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.Session;
import org.limeprotocol.Session.SessionState;
import org.limeprotocol.security.PlainAuthentication;
import org.limeprotocol.testHelpers.JsonConstants;

import java.util.HashMap;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.limeprotocol.testHelpers.TestDummy.*;

public class EnvelopeSerializerImplTest {

    private EnvelopeSerializerImpl target;

    @Before
    public void setUp() throws Exception {
        target = new EnvelopeSerializerImpl();
    }

    @Test
    public void serialize_AuthenticatingSession_ReturnsValidJsonString() {
        // Arrange
        Session session = createSession();
        PlainAuthentication plainAuthentication = createPlainAuthentication();
        session.setAuthentication(plainAuthentication);
        session.setState(SessionState.Authenticating);

        String metadataKey1 = "randomString1";
        String metadataValue1 = createRandomString(50);
        String metadataKey2 = "randomString2";
        String metadataValue2 = createRandomString(50);
        session.setMetadata(new HashMap<String, String>());
        session.getMetadata().put(metadataKey1, metadataValue1);
        session.getMetadata().put(metadataKey2, metadataValue2);

        // Act
        String resultString = target.serialize(session);

        // Assert
        assertThatJson(resultString).node(JsonConstants.Envelope.ID_KEY).isEqualTo(session.getId());
        assertThatJson(resultString).node(JsonConstants.Envelope.FROM_KEY).isEqualTo(session.getFrom());
        assertThatJson(resultString).node(JsonConstants.Envelope.TO_KEY).isEqualTo(session.getTo());
        assertThatJson(resultString).node(JsonConstants.Session.STATE_KEY).isEqualTo(session.getState());
        assertThatJson(resultString).node(JsonConstants.Envelope.getMetadataKeyFromRoot(metadataKey1)).isEqualTo(metadataValue1);
        assertThatJson(resultString).node(JsonConstants.Envelope.getMetadataKeyFromRoot(metadataKey2)).isEqualTo(metadataValue2);
        assertThatJson(resultString).node(JsonConstants.Session.AUTHENTICATION_KEY).isPresent();
        assertThatJson(resultString).node(JsonConstants.PlainAuthentication.PASSWORK_KEY_FROM_ROOT).isEqualTo(plainAuthentication.getPassword());

        assertThatJson(resultString).node(JsonConstants.Envelope.PP_KEY).isAbsent();
        assertThatJson(resultString).node(JsonConstants.Session.REASON_KEY).isAbsent();
    }

    /*
    [TestMethod]
            [TestCategory("Serialize")]
    public void Serialize_AuthenticatingSession_ReturnsValidJsonString()
    {
        var target = GetTarget();

        var session = DataUtil.CreateSession();
        var plainAuthentication = DataUtil.CreatePlainAuthentication();
        session.Authentication = plainAuthentication;
        session.State = SessionState.Authenticating;

        var metadataKey1 = "randomString1";
        var metadataValue1 = DataUtil.CreateRandomString(50);
        var metadataKey2 = "randomString2";
        var metadataValue2 = DataUtil.CreateRandomString(50);
        session.Metadata = new Dictionary<string, string>();
        session.Metadata.Add(metadataKey1, metadataValue1);
        session.Metadata.Add(metadataKey2, metadataValue2);

        var resultString = target.Serialize(session);

        Assert.IsTrue(resultString.HasValidJsonStackedBrackets());
        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.ID_KEY, session.Id));
        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.FROM_KEY, session.From));
        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.TO_KEY, session.To));
        Assert.IsTrue(resultString.ContainsJsonProperty(Session.STATE_KEY, session.State));
        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey1, metadataValue1));
        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey2, metadataValue2));
        Assert.IsTrue(resultString.ContainsJsonKey(Session.AUTHENTICATION_KEY));
        Assert.IsTrue(resultString.ContainsJsonProperty(PlainAuthentication.PASSWORD_KEY, plainAuthentication.Password));

        Assert.IsFalse(resultString.ContainsJsonKey(Envelope.PP_KEY));
        Assert.IsFalse(resultString.ContainsJsonKey(Session.REASON_KEY));
    }

    [TestMethod]
            [TestCategory("Serialize")]
    public void Serialize_FailedSession_ReturnsValidJsonString()
    {
        var target = GetTarget();

        var session = DataUtil.CreateSession();
        session.State = SessionState.Failed;
        session.Reason = DataUtil.CreateReason();

        var resultString = target.Serialize(session);

        Assert.IsTrue(resultString.HasValidJsonStackedBrackets());
        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.ID_KEY, session.Id));
        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.FROM_KEY, session.From));
        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.TO_KEY, session.To));
        Assert.IsTrue(resultString.ContainsJsonProperty(Session.STATE_KEY, session.State));
        Assert.IsTrue(resultString.ContainsJsonKey(Session.REASON_KEY));
        Assert.IsTrue(resultString.ContainsJsonProperty(Reason.CODE_KEY, session.Reason.Code));
        Assert.IsTrue(resultString.ContainsJsonProperty(Reason.DESCRIPTION_KEY, session.Reason.Description));

        Assert.IsFalse(resultString.ContainsJsonKey(Envelope.PP_KEY));
        Assert.IsFalse(resultString.ContainsJsonKey(Envelope.METADATA_KEY));
        Assert.IsFalse(resultString.ContainsJsonKey(Session.AUTHENTICATION_KEY));
    }
*/
}