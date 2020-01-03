public class Start {


    public static void main(String[] args) {

        if (args.length > 0) {
            Settings.HOST_NAME = args[0];
            Settings.HOST_PORT = Integer.parseInt(args[1]);
        }

        // запускаем сервер
        String serverName = "Server";


        ServerNetListener serverNetListener = new ServerNetListener(serverName);
        Thread serverNetListenerThread = new Thread(serverNetListener);
        serverNetListenerThread.start();

    }
}
