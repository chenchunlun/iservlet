package service;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 地址分配器.
 */
final class Dispatcher implements Runnable {
    /**
     * 存放请求路径对应的服务
     */
    private static Map<String, HttpServlet> servlets;

    static {
        servlets = new HashMap<>();
        // 初始化请求路径匹配的servlet 可以从注解中读取、也可以从XML配置文件中读取
    }

    private Socket socket;


    public Dispatcher(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        HttpRequest request = null;
        try {
            // 1、组装HttpRequest对象
            request = new HttpRequest(socket.getInputStream());
            // 2、寻找匹配的servlet并启用服务
            boolean noServlet = true;
            for (String url : servlets.keySet()) {
                if (url.equals(request.getUrl())) {
                    servlets.get(url).service(request, new HttpResponse(socket.getOutputStream(), request.getCharacterSet()));
                    noServlet = false;
                }
            }

            // 3、无匹配的返回404
            if (noServlet) {
                HttpServlet.fail(socket.getOutputStream(), 404, request.getCharacterSet());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            // 异常返回500
            try {
                if (request != null) {
                    HttpServlet.fail(socket.getOutputStream(), 500, request.getCharacterSet());
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
