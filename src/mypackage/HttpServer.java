package mypackage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private Integer port;
    private String[] docRoot;

    public HttpServer(Integer port, String[] docRoot) {
        this.port = port;
        this.docRoot = docRoot;
    }

    public void start() {
        System.out.println("Server Started at port " + port);

        // Task 4: check path (exist, is a directory, is readable);
        List<Path> paths = new ArrayList<>();
        for (String doc : docRoot) {
            paths.add(Paths.get(doc));
        }
        for (Path path : paths) {
            if (!Files.exists(path)){
                System.out.printf("Path <%s> does not exist", path.toString());
                System.exit(1);
            }
            if(!Files.isDirectory(path)){
                System.out.printf("Path <%s> is not directory", path.toString());
                System.exit(1);
            }
            if(!Files.isReadable(path)){
                System.out.printf("Path <%s> is not readable", path.toString());
                System.exit(1);
            }
        }

        try(ServerSocket serverSocket = new ServerSocket(this.port)){
            ExecutorService threadPool = Executors.newFixedThreadPool(3);
            
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                HttpClientConnection client = new HttpClientConnection(socket, docRoot);
                threadPool.submit(client);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
    }


}