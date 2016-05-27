package service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

/**
 * 地址分配器.
 */
public class Dispatcher implements Runnable {
    /**
     * 存放请求路径对应的服务
     */
    private static Map<String, HttpServlet> servlets;
    static {
        servlets = new HashMap<>();
        // 初始化请求路径匹配的servlet 可以从注解中读取、也可以从XML配置文件中读取
    }
    private Socket socket;



    public Dispatcher(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            // 1、读取请求信息
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
            System.out.println(new String(rs,0,rs.length));

            // 2、解析请求信息并组装成HttpRequest对象

            // 3、根据请求路径匹配Servlet并调用Servlet服务


            OutputStream os = socket.getOutputStream();
            os.write(new byte[0]);
            os.flush();

            is.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
