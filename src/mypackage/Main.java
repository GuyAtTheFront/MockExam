package mypackage;

public class Main {

    private static Integer port = 3000;
    private static String[] docRoot = {"./static"};
    public static void main(String[] args) {
        
        // String[] myArgs = {"--port", "8080", "--docRoot", "./target:/opt/temp/www"};
        //String[] myArgs = {"--port", "8080"};
        //args = myArgs;

        // Assume user-input is always valid
        try {
            for(int i = 0; i < args.length; i++) {
                if(args[i] == "--port") {
                    port = Integer.parseInt(args[i+1]);
                }
                if(args[i] == "--docRoot") {
                    docRoot = args[i+1].split(":");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return;
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
            return;
        }

        //System.out.println(port + "||" + docRoot.length);
        HttpServer server = new HttpServer(port, docRoot);
        server.start();
    }
}
