package org.limeprotocol;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents a collection of documents.
 */
public class DocumentCollection extends DocumentBase implements Iterable {

    public static final String MIME_TYPE = "application/vnd.lime.collection+json";

    private int total;
    private MediaType itemType;
    private Document[] items;

    public DocumentCollection() {
        super(MediaType.parse(MIME_TYPE));
    }

    /**
     * Gets or sets the total of items in the collection.
     * The count refers to the original source collection, without any applied filter that may be applied in the items on this collection.
     * @return
     */
    public int getTotal() {
        return total;
    }

    /**
     * Gets or sets the total of items in the collection.
     * The count refers to the original source collection, without any applied filter that may be applied in the items on this collection.
     * @param total
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Gets the media type of all items of the collection..
     * @return
     */
    public MediaType getItemType() {
        return itemType;
    }

    /**
     * Sets the media type of all items of the collection..
     * @param itemType
     */
    public void setItemType(MediaType itemType) {
        this.itemType = itemType;
    }

    /**
     * Gets the collection items.
     * @return
     */
    public Document[] getItems() {
        return items;
    }

    /**
     * Sets the collection items.
     * @param items
     */
    public void setItems(Document[] items) {
        this.items = items;
    }

    @Override
    public Iterator iterator() {
        if (this.items != null) {
            return Arrays.asList(items).iterator();
        }

        return null;
    }

    @Override
    public String toString() {
        if (this.items != null) {
            StringBuilder builder = new StringBuilder();
            for (Document item: items) {
                builder.append(item.toString() + "\n");
            }
            return builder.toString().trim();
        }

        return super.toString();
    }
}