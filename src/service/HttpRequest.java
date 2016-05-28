package service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
        // 设置字符集：系统默认
        this.characterSet = System.getProperty("file.encoding");
        // 创建参数对象,防止空指针异常
        parameterMapValues = new HashMap<>();
        analysisHttp(readHttp(inputStream));
    }

    private byte[] readHttp(InputStream inputStream) throws IOException {
        // 读取http请求字节数组
        byte[] buff = new byte[10];
        List<Byte> ib = new ArrayList<>();
        while (inputStream.available() != 0) {
            int len = inputStream.read(buff);
            for (int i = 0; i < len; i++) {
                ib.add(buff[i]);
            }
        }
        byte[] httpByte = new byte[ib.size()];
        for (int i = 0; i < ib.size(); i++) {
            httpByte[i] = ib.get(i).byteValue();
        }
        return httpByte;
    }
    /**
     * 解析出 请求方式(POST、GET两种) 请求参数  请求路径  --->根据http协议
     * @param source
     * @throws UnsupportedEncodingException
     */
    private void analysisHttp(byte[] source) throws UnsupportedEncodingException {
        // 将http字节数组转换成字符串
        String sourceStr = new String(source);
        String paramString = "";
        // 第一行存放 请求方法(GET POST ...) 请求路径以及get参数  协议名称 版本号
        // POST /xxxxx?name=张三&pwd=123456 HTTP/1.1
        // 读取第一行
        String firstLine = sourceStr.substring(0, sourceStr.indexOf(CR) >= 0 ? sourceStr.indexOf(CR) : (sourceStr.indexOf(CRLF) >= 0 ? sourceStr.indexOf(CRLF) : 0));
        // 请求方法
        this.method = firstLine.substring(0, sourceStr.indexOf("/")).trim().toUpperCase();
        // 请求路径及get参数
        String urlStr = firstLine.substring(sourceStr.indexOf("/"), firstLine.indexOf("HTTP/")).trim();
        if (this.method.equalsIgnoreCase("post")) {
            // 如果是post方式 urlStr中应该不含参数
            this.url = urlStr.contains("?") ? urlStr.substring(0, urlStr.indexOf("?")) : urlStr;
            // 如果是post 方法 最后一行存放的就是 post方式的参数
            int crLastIndex = sourceStr.lastIndexOf(CR) >= 0 ? sourceStr.lastIndexOf(CR) : (sourceStr.lastIndexOf(CRLF) >= 0 ? sourceStr.lastIndexOf(CRLF) : 0);
            paramString = sourceStr.substring(crLastIndex).trim();

        } else if (this.method.equalsIgnoreCase("get")) {
            // 如果是get
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
        // paramString   uuuu=1111&yyyyy=222222&jjjjj=iiiii&jjjjj=ooooo
        //分割 将字符串转成数组
        StringTokenizer token = new StringTokenizer(paramString, "&");
        while (token.hasMoreTokens()) {
            String[] keyValues = token.nextToken().split("=");
            if (keyValues.length == 1) {
                keyValues = Arrays.copyOf(keyValues, 2);
                keyValues[1] = null;
            }

            String key = keyValues[0].trim();
            String value = null == keyValues[1] ? null : URLDecoder.decode(keyValues[1].trim(), characterSet);

            // 如果不包含key
            if (!parameterMapValues.containsKey(key)) {
                // 使得parameterMapValues持有该key 并创建List对象
                parameterMapValues.put(key, new ArrayList<>());
            }
            // 将值挂载到对应的key下
            parameterMapValues.get(key).add(value);
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
