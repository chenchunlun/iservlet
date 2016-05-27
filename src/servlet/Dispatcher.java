package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

/**
 * 地址分配器.
 */
public class Dispatcher implements Runnable {
    private static Map<String, HttpServlet> servlets;
    //两个常量
    private static final String CRLF="\r\n";
    private static final String BLANK=" ";
    static {
        servlets = new HashMap<>();
    }
    private Socket socket;

    public Dispatcher(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            InputStream is = socket.getInputStream();
            byte[] buff = new byte[10];
            List<Byte> ib = new ArrayList<>();
            while(is.available()!=0) {
                int len = is.read(buff);
                for (int i = 0; i < len; i++) {
                    ib.add(buff[i]);
                }
            }
            byte[] rs = new byte[ib.size()];
            for(int i = 0;i<ib.size();i++){
                rs[i] = ib.get(i).byteValue();
            }

            OutputStream os = socket.getOutputStream();
            StringBuilder  headInfo = new StringBuilder();
            int code = 200;
            //1)  HTTP协议版本、状态代码、描述
            headInfo.append("HTTP/1.1").append(BLANK).append(code).append(BLANK);
            switch(code){
                case 200:
                    headInfo.append("OK");
                    break;
                case 404:
                    headInfo.append("NOT FOUND");
                    break;
                case 505:
                    headInfo.append("SEVER ERROR");
                    break;
            }
            headInfo.append(CRLF);
            //2)  响应头(Response Head)
            headInfo.append("Server:bjsxt Server/0.0.1").append(CRLF);
            headInfo.append("Date:").append(new Date()).append(CRLF);
            headInfo.append("Content-type:text/html;charset=GBK").append(CRLF);
            //正文长度 ：字节长度
            headInfo.append("Content-Length:").append(0).append(CRLF);
            headInfo.append(CRLF); //分隔符
            os.write(headInfo.toString().getBytes());
            os.flush();
            System.out.println(new String(rs,0,rs.length));

            is.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
