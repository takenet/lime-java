package org.limeprotocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a generic JSON document.
 */
public final class JsonDocument implements Document, Map<String, Object> {
    private MediaType mediaType;
    private Map<String, Object> json;


    /**
     * Initializes a new instance of the JsonDocument class.
     */
    public JsonDocument() {
        this(new MediaType(MediaType.DiscreteTypes.Application, MediaType.SubTypes.JSON));
    }

    public JsonDocument(MediaType mediaType) {
        this(new HashMap<String, Object>(), mediaType);
    }

    public JsonDocument(Map<String, Object> json, MediaType mediaType) {
        this.mediaType = mediaType;

        if (json == null) {
            throw new IllegalArgumentException("json");
        }

        this.json = json;

        if (!mediaType.isJson()) {
            throw new IllegalArgumentException("The media type is not a valid json type");
        }
    }


    public void setMediaType(MediaType mediaType) {
        if (mediaType == null) {
            throw new IllegalArgumentException("mediaType");
        }

        if (!mediaType.isJson()) {
            throw new IllegalArgumentException("The media type is not a valid json type");
        }

        this.mediaType = mediaType;
    }

    /**
     * Returns the JSON representation of the object.
     * @return String that represent json object
     */
    @Override
    public String toString() {

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }

    @Override
    public int size() {
        return json.size();
    }

    @Override
    public boolean isEmpty() {
        return json.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return json.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return json.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return json.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return json.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return json.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        this.json.putAll(m);
    }

    @Override
    public void clear() {
        json.clear();
    }

    @Override
    public Set<String> keySet() {
        return json.keySet();
    }

    @Override
    public Collection<Object> values() {
        return json.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return json.entrySet();
    }
}
