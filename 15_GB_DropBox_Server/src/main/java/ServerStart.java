public class ServerStart {


    public static void main(String[] args) {


        if (args.length > 0) {
            //SettingsServer.HOST_NAME = args[0];
            CommonVariables.HOST_PORT = Integer.parseInt(args[0]);
        }

        // запускаем сервер
        String serverName = "Server";


        ServerNetListener serverNetListener = new ServerNetListener(serverName);
        Thread serverNetListenerThread = new Thread(serverNetListener);
        serverNetListenerThread.start();

    }
}
