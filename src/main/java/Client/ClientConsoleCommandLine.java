package Client;

import Shared.CommandAnswer;
import Shared.CommandsList;
import Shared.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ClientConsoleCommandLine implements Runnable {

    private String clientName = "ClientConsoleCommandLine";
    private ClientNetListener clientNetListener; // ссылка на клиентское подключение
    private InputStreamReader inputStream = new InputStreamReader(System.in); // объект чтения данных из потока консоли
    private BufferedReader BufferedReader = new BufferedReader(inputStream); // объект чтения данных из консоли
    private StringBuilder usersCommand = new StringBuilder(); // введенная пользователем команда


    /**
     * констуктор консольной программы с созданием клиентского подключения
     */
    public ClientConsoleCommandLine(String hostName, int port) {

        Settings.hostName = hostName;
        Settings.hostPort = port;

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        // запускаем клиентское подлючение к серверу
        clientNetListener = new ClientNetListener(clientName);
        Thread clientNetListenerThread = new Thread(clientNetListener);
        clientNetListenerThread.start();
    }

    public static void main(String[] args) {

        if (args.length > 0) {
            Settings.hostName = args[0];
            Settings.hostPort = Integer.parseInt(args[1]);
        }

        // запускаем консоль с внутренним запуском клиентского подключения
        ClientConsoleCommandLine clientConsoleCommandLine = new ClientConsoleCommandLine(Settings.hostName, Settings.hostPort);
        Thread clientConsoleCommandLineThread = new Thread(clientConsoleCommandLine);
        clientConsoleCommandLineThread.start();
    }

    /**
     * метод чтения введенных данных их консоли
     */
    @Override
    public void run() {
        System.out.println("ClientConsoleCommandLine.started"); //log
        try {
            while (true) { // NL цикл чтения команд с консоли пользователя

                if (!!(usersCommand.insert(0, BufferedReader.readLine()).toString().toLowerCase().equals("~#stop")))
                    break;

                if (usersCommand.toString().toLowerCase().startsWith("~ci")) {// NL обработка команды ci

                    clientNetListener.getSocketChannel().writeAndFlush(
                            new CommandsList.ClientStorageInfo());

                } else if (usersCommand.toString().toLowerCase().startsWith("~gf")) {// NL обработка команды gf

                    clientNetListener.getSocketChannel().writeAndFlush(
                            new CommandsList.GetFileFromServer(usersCommand.toString(),
                                    CommandAnswer.WhoIsSender.CLIENT));

                } else if (usersCommand.toString().toLowerCase().startsWith("~sf")) {// NL обработка команды gf

                    clientNetListener.getSocketChannel().writeAndFlush(
                            new CommandsList.SendFileToServer(usersCommand.toString(),
                                    CommandAnswer.WhoIsSender.CLIENT));

                } else if (usersCommand.toString().toLowerCase().startsWith("~si")) {// NL обработка команды si

                    clientNetListener.getSocketChannel().writeAndFlush(
                            new CommandsList.ServerStorageInfo(CommandAnswer.WhoIsSender.CLIENT));

                } else {// NL обработка неизвестной команды

                    System.err.println("Console: unknown command!");
                }
                usersCommand.setLength(0);
            }

            clientNetListener.getSocketChannel().closeFuture();
            clientNetListener.getSocketChannel().close();

        } catch (
                IOException e) {
            System.err.println("ClientConsoleCommandLine.consoleSender().error");// log
            e.printStackTrace();
        }

    }

}
