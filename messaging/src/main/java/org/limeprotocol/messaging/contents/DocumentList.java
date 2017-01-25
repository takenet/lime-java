package org.limeprotocol.messaging.contents;

import org.limeprotocol.DocumentBase;
import org.limeprotocol.DocumentContainer;
import org.limeprotocol.MediaType;

/**
 * Defines a list of documents with a header.
 */
public class DocumentList extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.lime.list+json";

    private DocumentContainer header;
    private DocumentContainer[] items;

    public DocumentList() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets the list header document.
     * @return
     */
    public DocumentContainer getHeader() {
        return header;
    }

    /**
     * Sets the list header document.
     * @param header
     */
    public void setHeader(DocumentContainer header) {
        this.header = header;
    }

    /**
     * Gets the list items.
     * @return
     */
    public DocumentContainer[] getItems() {
        return items;
    }

    /**
     * Sets the list items.
     * @param items
     */
    public void setItems(DocumentContainer[] items) {
        this.items = items;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (getHeader() != null) {
            builder.append(getHeader() + "\n");
        }

        for (DocumentContainer item : getItems()) {
            builder.append(item.toString() + "\n");
        }
        return builder.toString().trim();    }
}
