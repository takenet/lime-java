package org.limeprotocol.network;

public class JsonBuffer {
    private int jsonStartPos;
    private int jsonCurPos;
    private int jsonStackedBrackets;
    private boolean jsonStarted = false;
    private byte[] buffer;
    private int bufferCurPos;

    public JsonBuffer(int bufferSize) {
        buffer = new byte[bufferSize];
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getBufferCurPos() {
        return bufferCurPos;
    }

    public void increaseBufferCurPos(int bytes) {
        bufferCurPos += bytes;
    }

    public JsonBufferReadResult tryExtractJsonFromBuffer() {
        if (bufferCurPos > buffer.length) {
            throw new IllegalArgumentException("Buffer current pos or length value is invalid", null);
        }

        byte[] json = null;
        int jsonLength = 0;
        for (int i = jsonCurPos; i < bufferCurPos; i++) {
            jsonCurPos = i + 1;

            if (buffer[i] == '{') {
                jsonStackedBrackets++;
                if (!jsonStarted) {
                    jsonStartPos = i;
                    jsonStarted = true;
                }
            }
            else if (buffer[i] == '}') {
                jsonStackedBrackets--;
            }

            if (jsonStarted &&
                    jsonStackedBrackets == 0) {
                jsonLength = i - jsonStartPos + 1;
                break;
            }
        }

        if (jsonLength > 1) {
            json = new byte[jsonLength];
            System.arraycopy(buffer, jsonStartPos, json, 0, jsonLength);

            // Shifts the buffer to the left
            bufferCurPos -= (jsonLength + jsonStartPos);
            System.arraycopy(buffer, jsonLength + jsonStartPos, buffer, 0, bufferCurPos);
            jsonCurPos = 0;
            jsonStartPos = 0;
            jsonStarted = false;

            return new JsonBufferReadResult(true, json);
        }

        return new JsonBufferReadResult(false, null);
    }

    public class JsonBufferReadResult {
        private final boolean success;
        private final byte[] jsonBytes;

        public JsonBufferReadResult(boolean success, byte[] jsonBytes) {
            this.success = success;
            this.jsonBytes = jsonBytes;
        }

        public boolean isSuccess() {
            return success;
        }

        public byte[] getJsonBytes() {
            return jsonBytes;
        }
    }

}
