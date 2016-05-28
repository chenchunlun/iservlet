package service;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.*;

/**
 * HTTP请求.
 */
public final class HttpRequest {

    private static final String CR = "\r";
    private static final String CRLF = "\r\n";

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

    /**
     * 请求参数
     */
    private Map<String, List<Object>> parameterMapValues;

    HttpRequest(InputStream inputStream) throws Exception {
        this.characterSet = System.getProperty("file.encoding");
        parameterMapValues = new HashMap<>();
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


        String requestInfo = new String(rs);
        String paramString = ""; //接收请求参数
        //1、获取请求方式

        int crIndex = requestInfo.indexOf(CR)>=0?requestInfo.indexOf(CR):(requestInfo.indexOf(CRLF)>=0?requestInfo.indexOf(CRLF):0);
        String firstLine = requestInfo.substring(0, crIndex);
        int idx = requestInfo.indexOf("/"); // /的位置
        this.method = firstLine.substring(0, idx).trim().toUpperCase();
        String urlStr = firstLine.substring(idx, firstLine.indexOf("HTTP/")).trim();
        if (this.method.equalsIgnoreCase("post")) {
            this.url = urlStr;
            int crLastIndex = requestInfo.lastIndexOf(CR)>=0?requestInfo.lastIndexOf(CR):(requestInfo.lastIndexOf(CRLF)>=0?requestInfo.lastIndexOf(CRLF):0);
            paramString = requestInfo.substring(crLastIndex).trim();

        } else if (this.method.equalsIgnoreCase("get")) {
            if (urlStr.contains("?")) { //是否存在参数
                String[] urlArray = urlStr.split("\\?");
                this.url = urlArray[0];
                paramString = urlArray[1];//接收请求参数
            } else {
                this.url = urlStr;
            }
        }

        //不存在请求参数
        if (paramString.equals("")) {
            return;
        }
        //2、将请求参数封装到Map中
        //分割 将字符串转成数组
        StringTokenizer token = new StringTokenizer(paramString, "&");
        while (token.hasMoreTokens()) {
            String keyValue = token.nextToken();
            String[] keyValues = keyValue.split("=");
            if (keyValues.length == 1) {
                keyValues = Arrays.copyOf(keyValues, 2);
                keyValues[1] = null;
            }

            String key = keyValues[0].trim();
            String value = null == keyValues[1] ? null : URLDecoder.decode(keyValues[1].trim(), characterSet);
            //转换成Map 分拣
            if (!parameterMapValues.containsKey(key)) {
                parameterMapValues.put(key, new ArrayList<>());
            }

            List<Object> values = parameterMapValues.get(key);
            values.add(value);
        }

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
