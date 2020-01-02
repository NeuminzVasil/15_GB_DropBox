package Shared;

import Client.ClientConsoleCommandLine;
import Server.ServerNetListener;


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

        // запускаем консоль с внутренним запуском клиентского подключения
        ClientConsoleCommandLine clientConsoleCommandLine = new ClientConsoleCommandLine(Settings.HOST_NAME, Settings.HOST_PORT);
        Thread clientConsoleCommandLineThread = new Thread(clientConsoleCommandLine);
        clientConsoleCommandLineThread.start();

    }
}
