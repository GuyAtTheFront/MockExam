package mypackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpClientConnection implements Runnable{
 
    private Socket socket;
    private String[] docRoot;

    public HttpClientConnection(Socket socket, String[] docRoot) {
        this.socket = socket;
        this.docRoot = docRoot;
    }

    @Override
    public void run() {
        System.out.println("hello");

        try {
        InputStream is = socket.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);
        //System.out.println("dis");

        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);
        //System.out.println("dos");

        BufferedReader br = new BufferedReader(new InputStreamReader(dis));
        String fromUser = br.readLine();
        //String fromUser = dis.readUTF();
        System.out.println("from user: " + fromUser);
        String[] splitFromUser = fromUser.split(" ");
        System.out.println(splitFromUser);
        // Task 6, action 2 special case: "/" --> "/index.html"
        if (splitFromUser[1].equals("/")) splitFromUser[1] = "/index.html";
        
        // Task 6, Action 1:  Not "GET" --> return error
        if(!splitFromUser[0].equals("GET")) {
            dos.writeUTF("HTTP/1.1 405 Method Not Allowed\r\n\r\n%s not supported\r\n".formatted(splitFromUser[0]));
            dos.flush();
            socket.close();
            return;
        } else {System.out.println("Has GET");}

        // Task 6, Action 2: resource not found --> return error
        Path validFilePath = null;
        for (String doc : docRoot) {
            //Path filePath = Paths.get(doc).resolve(Paths.get(splitFromUser[1]));
            Path filePath = Paths.get(doc, splitFromUser[1]);
            if(Files.exists(filePath)) {
                validFilePath = filePath;
                break;
            } 
        }
        if (validFilePath == null) {
            System.out.println("File not found");
            dos.writeUTF("HTTP/1.1 404 Not Found\r\n\r\n%s not found\r\n".formatted(splitFromUser[1]));
            dos.flush();
            socket.close();
            return;
        } else {System.out.println("Has resource");}

        // Task 6, Action 3: resource exist, not png --> return resource as bytes
        // Note: Everything must be sent as bytes :shrug:
        if (!splitFromUser[1].contains(".png")) {
            System.out.println("no PNG");
            dos.writeBytes("HTTP/1.1 200 OK\r\n");
            System.out.println("http thingy");
            dos.flush();
            dos.writeBytes("\r\n");
            System.out.println("rn thingy");
            dos.flush();
            FileInputStream fis = new FileInputStream(validFilePath.toString());
            dos.write(fis.readAllBytes());
            dos.flush();
            System.out.println("file");
            
            fis.close();
            socket.close();
            return;
        }

        // Task 6, Action 4: resource exist, png --> return resource as bytes(png)
        // TODO: Broken, probably smth to do with writeUTF / fis / bytes...
        if (splitFromUser[1].contains(".png")) {
            System.out.println("Has PNG");
            dos.writeBytes("HTTP/1.1 200 OK\r\nContent-Type: image/png\r\n\r\n");
            dos.flush();
            FileInputStream fis = new FileInputStream(validFilePath.toString());
            dos.write(fis.readAllBytes());
            dos.flush();
            
            fis.close();
            socket.close();
            return;
        }

        } catch(IOException e) {System.out.println("Error: " + e.getStackTrace().toString());}
        
    }
}