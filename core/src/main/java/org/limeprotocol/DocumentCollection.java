package org.limeprotocol;

import java.util.Arrays;
import java.util.Iterator;

public class DocumentCollection implements Document, Iterable {

    public static final String MIME_TYPE = "application/vnd.lime.collection+json";

    private MediaType mediaType;

    private int total;
    private MediaType itemType;
    private Document[] items;

    public DocumentCollection() {
        this.mediaType = MediaType.parse(MIME_TYPE);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public MediaType getItemType() {
        return itemType;
    }

    public void setItemType(MediaType itemType) {
        this.itemType = itemType;
    }

    public Document[] getItems() {
        return items;
    }

    public void setItems(Document[] items) {
        this.items = items;
    }

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }

    @Override
    public Iterator iterator() {
        if (this.items != null) {
            return Arrays.asList(items).iterator();
        }

        return null;
    }
}
