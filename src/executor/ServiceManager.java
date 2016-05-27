package executor;

import servlet.Dispatcher;
import servlet.HttpServlet;
import util.SocketUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务管理者
 */
public class ServiceManager {
    private static ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * 开启服务.
     * @param port 服务端口号
     */
    public static void start(final int port){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ServerSocket server = SocketUtil.serverSocket(port);
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
