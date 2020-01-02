package Shared;

import Client.ClientConsoleCommandLine;
import Server.ServerNetListener;


public class Start {


    public static void main(String[] args) {

        if (args.length > 0) {
            Settings.hostName = args[0];
            Settings.hostPort = Integer.parseInt(args[1]);
        }

        // запускаем сервер
        String serverName = "Server";
        ServerNetListener serverNetListener = new ServerNetListener(serverName);
        Thread serverNetListenerThread = new Thread(serverNetListener);
        serverNetListenerThread.start();

        // запускаем консоль с внутренним запуском клиентского подключения
        ClientConsoleCommandLine clientConsoleCommandLine = new ClientConsoleCommandLine(Settings.hostName, Settings.hostPort);
        Thread clientConsoleCommandLineThread = new Thread(clientConsoleCommandLine);
        clientConsoleCommandLineThread.start();

    }
}
