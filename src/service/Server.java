package service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务管理者
 */
public final class Server {
    private static ServerSocket server = null;
    private static ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * 获取服务器套接字(指定端口).
     * @param port 服务器占用的端口
     * @return 服务套接字
     */
    private static ServerSocket serverSocket(int port){
        if(null == server) {
            synchronized (Server.class) {
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

    /**
     * 开启服务.
     * @param port 服务端口号
     */
    public static void start(final int port){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ServerSocket server = serverSocket(port);
                while(true){
                    try {
                        Socket socket = server.accept();
                        executor.execute(new Dispatcher(socket));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 开启服务
     */
    public static void start(){
        start(80);
    }

    /**
     * 停止服务
     */
    public static void stop(){
        executor.shutdown();
    }
}
