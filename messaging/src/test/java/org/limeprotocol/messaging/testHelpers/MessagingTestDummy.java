package org.limeprotocol.messaging.testHelpers;

import org.limeprotocol.Document;
import org.limeprotocol.DocumentCollection;
import org.limeprotocol.DocumentContainer;
import org.limeprotocol.MediaType;
import org.limeprotocol.messaging.contents.*;
import org.limeprotocol.messaging.resources.Account;
import org.limeprotocol.messaging.resources.Capability;
import org.limeprotocol.messaging.resources.Contact;

import java.net.URI;
import java.net.URISyntaxException;

import static org.limeprotocol.testHelpers.Dummy.*;

public class MessagingTestDummy {

    public static PlainText createPlainText()
    {
        return new PlainText(createRandomString(150));
    }

    public static Account createAccount() {
        Account account = new Account();
        account.setFullName(createRandomString(20));
        try {
            account.setPhotoUri(createUri("http", 80));
        } catch (URISyntaxException ignore) {
        }
        return account;
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

    public static Contact createContact(){
        Contact contact1 = new Contact();
        contact1.setIdentity(createIdentity());
        contact1.setName(createRandomString(50));
        return contact1;
    }

    public static DocumentCollection createRoster(){
        DocumentCollection dc = new DocumentCollection();

        Contact contact1 = new Contact();
        contact1.setIdentity(createIdentity());
        contact1.setName(createRandomString(50));
        contact1.setIsPending(true);
        contact1.setShareAccountInfo(false);
        contact1.setSharePresence(true);

        Contact contact2 = new Contact();
        contact2.setIdentity(createIdentity());
        contact2.setName(createRandomString(50));
        contact2.setIsPending(false);
        contact2.setShareAccountInfo(true);
        contact2.setSharePresence(false);

        Contact contact3 = new Contact();
        contact3.setIdentity(createIdentity());
        contact3.setName(createRandomString(50));
        contact3.setIsPending(true);
        contact3.setShareAccountInfo(true);
        contact3.setSharePresence(false);

        dc.setItemType(MediaType.parse(Contact.MIME_TYPE));
        dc.setItems(new Document[]{ contact1, contact2, contact3 });

        return dc;
    }

    public static DocumentContainer createDocumentContainer(final Document document) {
        return new DocumentContainer() {{
            setValue(document);
        }};
    }

    public static Select createSelect() {
        return new Select() {{
            setText(createRandomString(100));
            setOptions(new SelectOption[] {
                    new SelectOption() {{
                        setText(createRandomString(10));
                        setOrder(1);
                        setValue(createTextContent());
                    }},
                    new SelectOption() {{
                        setText(createRandomString(10));
                        setOrder(2);
                    }},
                    new SelectOption() {{
                        setText(createRandomString(10));
                    }},
                    new SelectOption() {{
                        setValue(createJsonDocument());
                    }}
            });
        }};
    }

    public static DocumentSelect createDocumentSelect() {
        return new DocumentSelect() {{
            setHeader(createDocumentContainer(createPlainText()));
            setOptions(new DocumentSelectOption[] {
                createDocumentSelectOption(createTextContent(), null),
                createDocumentSelectOption(createJsonDocument(), createJsonDocument())
            });
        }};
    }

    public static DocumentSelectOption createDocumentSelectOption(final Document label, final Document value) {
        return new DocumentSelectOption() {{
            setLabel(createDocumentContainer(label));
            setValue(value != null ? createDocumentContainer(value) : null);
        }};
    }

    public static WebLink createWebLink(final URI uri) {
        return new WebLink() {{
            setText(createRandomString(30));
            setUri(uri);
        }};
    }
}
