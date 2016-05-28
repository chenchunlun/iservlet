package service;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * HTTP响应.
 */
public final class HttpResponse {
    OutputStream outputStream;
    byte[] content;
    String characterSet;

    HttpResponse(OutputStream outputStream, String characterSet) {
        this.outputStream = outputStream;
        this.characterSet = characterSet;
        content = new byte[0];
    }

    /**
     * 做出响应.
     *
     * @param content
     */
    public void respond(byte[] content) {
        this.content = content;
    }

    /**
     * 做出响应
     *
     * @param content
     */
    public void respond(String content) {
        try {
            this.content = content.getBytes(characterSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 做出响应
     *
     * @param content
     */
    public void respond(String content, String characterSet) {
        try {
            this.content = content.getBytes(characterSet);
            this.characterSet = characterSet;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
