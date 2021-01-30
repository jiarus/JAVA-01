package io.github.jiarus.server;

import io.github.jiarus.util.HttpClientUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * 多线程 同步I/O
 *
 * @author zhangjiaru
 * @date: 2021/01/25
 */
@Slf4j
public class HttpMulThreadServer implements HttpServer {
    
    private int port;
    
    private List<String> hosts;
    
    public HttpMulThreadServer(int port, List<String> hosts) {
        this.port = port;
        this.hosts = hosts;
    }
    
    /**
     * 建立socket连接
     */
    @Override
    public void run() {
        int coreSize = Runtime.getRuntime().availableProcessors() + 2;
        int maxSize = coreSize + 2;
        ExecutorService fixThreadPool = new ThreadPoolExecutor(coreSize, maxSize, 1, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                fixThreadPool.execute(() -> handler(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 路由
     *
     * @param url
     * @return
     */
    @Override
    public String dispatcher(String url) {
        try {
            return HttpClientUtil.doGet(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public void handler(Socket socket) {
        
        String content = read(socket);
        if (StringUtil.isNullOrEmpty(content)) {
            return;
        }
        String[] params = content.split("\r\n");
        String path = null;
        for (String param : params) {
            if (param.startsWith("GET")) {
                path = param.substring(param.indexOf("GET") + 4, param.indexOf("HTTP"));
                continue;
            }
        }
        if (StringUtil.isNullOrEmpty(path)) {
            return;
        }
        if ("/favicon.ico".equals(path)) {
            return;
        }
        String module = path.substring(1, path.indexOf("/", 1));
        String url = String.format("http://%s%s", moduleSelector(module), path.trim());
        String response = dispatcher(url);
        write(socket, response);
    }
    
    /**
     * 读取socket内容
     *
     * @param socket
     */
    private String read(Socket socket) {
        InputStream inputStream = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = socket.getInputStream();
            //读取Socket
            byte[] buf = new byte[2048];
            while (inputStream.read(buf) != -1) {
                stringBuilder.append(new String(buf, Charset.defaultCharset()));
                String currentString = stringBuilder.toString();
                //todo 没有content就不会有Content-Length，为防止阻塞直接关闭输入流
//                if (currentString.indexOf("Content-Length") > 0) {
//
//                }
                System.out.println(currentString);
                break;
            }
            
            return stringBuilder.toString();
        } catch (IOException e) {
            log.error("read socket error", e);
        } finally {
            try {
                socket.shutdownInput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private String moduleSelector(String module) {
        Collections.shuffle(hosts);
        return hosts.get(0);
    }
    
    public void write(Socket socket, String result) {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;charset=utf-8");
            printWriter.println("Content-Length:" + result.getBytes().length);
            printWriter.println();
            printWriter.println(result);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
