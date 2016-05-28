package service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;


/**
 * HTTP服务基类.
 */
public class HttpServlet {
    public static final String CRLF = "\r\n";
    public static final String BLANK = " ";

    /**
     * Servlet服务开启.
     *
     * @param request
     * @param response
     */
    final void service(HttpRequest request, HttpResponse response) throws IOException {
        int code = 404;
        try {
            if ("get".equalsIgnoreCase(request.getMethod())) {
                doGet(request, response);
                code = 200;
            } else if ("post".equalsIgnoreCase(request.getMethod())) {
                doPost(request, response);
                code = 200;
            }
        } catch (Exception e) {
            e.printStackTrace();
            code = 500;
        }

        if (200 == code) {
            success(response.outputStream, response.content, request.getCharacterSet());
        } else {
            fail(response.outputStream, code, request.getCharacterSet());
        }
    }

    /**
     * 成功.
     *
     * @param outputStream
     * @param content
     * @param chararcterSet
     * @throws IOException
     */
    final static void success(OutputStream outputStream, byte[] content, String chararcterSet) throws IOException {
        byte[] headInfo = hedInfo(content.length, 200, chararcterSet);
        outputStream.write(headInfo);
        outputStream.write(content);
        outputStream.flush();
    }

    /**
     * 失败.
     *
     * @param outputStream
     * @param code
     * @param chararcterSet
     * @throws IOException
     */
    final static void fail(OutputStream outputStream, int code, String chararcterSet) throws IOException {
        byte[] content = new byte[0];
        try {
            content = String.valueOf(code).getBytes(chararcterSet);
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            content = String.valueOf(code).getBytes();
        }

        byte[] headInfo = hedInfo(content.length, code, chararcterSet);
        outputStream.write(headInfo);
        outputStream.write(content);
        outputStream.flush();
    }

    /**
     * 组装头信息
     *
     * @param contentLen
     * @param code
     * @param chararcterSet
     * @return
     */
    final static byte[] hedInfo(int contentLen, int code, String chararcterSet) {
        StringBuilder headInfo = new StringBuilder();
        //1)  HTTP协议版本、状态代码、描述
        headInfo.append("HTTP/1.1").append(BLANK).append(code).append(BLANK);
        switch (code) {
            case 200:
                headInfo.append("OK");
                break;
            case 404:
                headInfo.append("NOT FOUND");
                break;
            case 500:
                headInfo.append("SEVER ERROR");
                break;
        }
        headInfo.append(CRLF);
        //2)  响应头(Response Head)
        headInfo.append("Server:chenchunlun_code Server/0.0.1").append(CRLF);
        headInfo.append("Date:").append(new Date()).append(CRLF);
        headInfo.append("Content-type:text/html;charset=").append(chararcterSet).append(CRLF);
        //正文长度 ：字节长度
        headInfo.append("Content-Length:").append(contentLen).append(CRLF);
        headInfo.append(CRLF); //分隔符
        try {
            return headInfo.toString().getBytes(chararcterSet);
        } catch (UnsupportedEncodingException e) {
            return headInfo.toString().getBytes();
        }

    }

    /**
     * 请求方法为GET时调用.
     *
     * @param request
     * @param response
     */
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    /**
     * 请求方法为POST时调用.
     *
     * @param request
     * @param response
     */
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

}
