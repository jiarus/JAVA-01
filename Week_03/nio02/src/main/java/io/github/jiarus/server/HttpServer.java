package io.github.jiarus.server;

import java.net.Socket;

/**
 * @author zhangjiaru
 * @date: 2021/01/25
 */
public interface HttpServer {
    
    void run();
    
    String dispatcher(String url);
    
    void handler(Socket socket);
}
