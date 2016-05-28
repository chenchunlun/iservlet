package service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求.
 */
public final class HttpRequest {
    /**
     * socket输入流
     */
    private InputStream inputStream;
    /**
     * 请求路径
     */
    private String url;
    /**
     * 请求方式
     */
    private String method;

    /**
     * 字符集
     */
    private String characterSet;

    private Map<String, List<Object>> params;

    HttpRequest(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        this.characterSet = System.getProperty("file.encoding");
        byte[] buff = new byte[10];
        List<Byte> ib = new ArrayList<>();
        while (inputStream.available() != 0) {
            int len = inputStream.read(buff);
            for (int i = 0; i < len; i++) {
                ib.add(buff[i]);
            }
        }
        byte[] rs = new byte[ib.size()];
        for (int i = 0; i < ib.size(); i++) {
            rs[i] = ib.get(i).byteValue();
        }
        System.out.print(new String(rs));


    }

    public String getCharacterSet() {
        return characterSet;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }
}
