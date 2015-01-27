package org.limeprotocol;

import java.util.Arrays;
import java.util.Iterator;

public class DocumentCollection implements Document, Iterable {

    public final String MIME_TYPE = "application/vnd.lime.collection+json";

    public final String TOTAL_KEY = "total";
    public final String ITEM_TYPE_KEY = "itemType";
    public final String ITEMS_KEY = "items";

    private int total;
    private MediaType itemType;
    private Document[] items;

    public DocumentCollection() {
        this.itemType = MediaType.parse(MIME_TYPE);
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
        return null;
    }

    @Override
    public Iterator iterator() {
        if (this.items != null) {
            return Arrays.asList(items).iterator();
        }

        return null;
    }
}
