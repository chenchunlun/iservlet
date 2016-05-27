package util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 套接字工具类.
 */
public class SocketUtil {
    private static ServerSocket server = null;

    /**
     * 获取服务器套接字(指定端口).
     * @param port 服务器占用的端口
     * @return 服务套接字
     */
    public static ServerSocket serverSocket(int port){
        if(null == server) {
            synchronized (SocketUtil.class) {
                if(null== server){
                    try {
                        server = new ServerSocket(port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return server;
    }
}
