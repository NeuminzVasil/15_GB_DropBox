import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ClientConsoleCommandLine implements Runnable {

    private String clientName = "ClientConsoleCommandLine";
    private ClientNetListener clientNetListener; // ссылка на клиентское подключение
    private InputStreamReader inputStream = new InputStreamReader(System.in); // объект чтения данных из потока консоли
    private BufferedReader bufferedReader = new BufferedReader(inputStream); // объект чтения данных из консоли

    /**
     * набор объектов стандартных команд.
     * чтобы не создавать каждый раз новые компанды будем исопльзвоать повторно заготовки
     * однако это не исключает создание НОВЫХ объектов команд.
     */
    private StringBuilder usersCommand = new StringBuilder(); // введенная пользователем команда
    private CommandsList.GetStorageInfo getStorageInfo = new CommandsList.GetStorageInfo();
    private CommandsList.UserRegistering userRegistering = new CommandsList.UserRegistering();
    private CommandsList.GetFileFromServer getFileFromServer = new CommandsList.GetFileFromServer();
    private CommandsList.SendFileToServer sendFileToServer = new CommandsList.SendFileToServer();
    private CommandsList.DeleteFile deleteFile = new CommandsList.DeleteFile();
    private CommandsList.RenamingFile renamingFile = new CommandsList.RenamingFile();


    /**
     * констуктор консольной программы с созданием клиентского подключения
     */
    public ClientConsoleCommandLine(String hostName, int port) throws IOException {

        SettingsClient.HOST_NAME = hostName;
        SettingsClient.HOST_PORT = port;

        // запускаем клиентское подлючение к серверу
        clientNetListener = new ClientNetListener(clientName);
        Thread clientNetListenerThread = new Thread(clientNetListener);
        clientNetListenerThread.start();
    }

    public static void main(String[] args) throws IOException {

        if (args.length > 0) {
            SettingsClient.HOST_NAME = args[0];
            SettingsClient.HOST_PORT = Integer.parseInt(args[1]);
        }

        // запускаем консоль с внутренним запуском клиентского подключения
        ClientConsoleCommandLine clientConsoleCommandLine = new ClientConsoleCommandLine(SettingsClient.HOST_NAME, SettingsClient.HOST_PORT);
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

                if (!!(usersCommand.insert(0, bufferedReader.readLine()).toString().toLowerCase().equals("~#stop"))) //NL обработка команды "стоп консоль"
                    break;

                if (usersCommand.toString().toLowerCase().startsWith("~?")) {// NL обработка команды "справка"

                    System.out.println(CommandsList.commandsInfo);

                } else if (usersCommand.toString().toLowerCase().startsWith("~si")) {// NL обработка команды "storage info"

                    getStorageInfo.sendingSettings(usersCommand.toString(), CommandAnswer.WhoIsSender.CLIENT);
                    clientNetListener.getSocketChannel().writeAndFlush(getStorageInfo);

                } else if (usersCommand.toString().toLowerCase().startsWith("~lu")) {// NL обработка команды "login user"

                    userRegistering.sendingSettings(usersCommand.toString(), CommandAnswer.WhoIsSender.CLIENT);
                    clientNetListener.getSocketChannel().writeAndFlush(userRegistering);

                } else if (usersCommand.toString().toLowerCase().startsWith("~gf")) {// NL обработка команды "get file"

                    getFileFromServer.sendingSettings(usersCommand.toString(), CommandAnswer.WhoIsSender.CLIENT);
                    clientNetListener.getSocketChannel().writeAndFlush(getFileFromServer);

                } else if (usersCommand.toString().toLowerCase().startsWith("~sf")) {// NL обработка команды send file

                    sendFileToServer.sendingSettings(usersCommand.toString(), CommandAnswer.WhoIsSender.CLIENT);
                    clientNetListener.getSocketChannel().writeAndFlush(sendFileToServer);

                } else if (usersCommand.toString().toLowerCase().startsWith("~df")) {// NL обработка команды deleting file

                    deleteFile.sendingSettings(usersCommand.toString(), CommandAnswer.WhoIsSender.CLIENT);
                    clientNetListener.getSocketChannel().writeAndFlush(deleteFile);

                } else if (usersCommand.toString().toLowerCase().startsWith("~rf")) {// NL обработка команды renaming file

                    renamingFile.sendingSettings(usersCommand.toString(), CommandAnswer.WhoIsSender.CLIENT);
                    clientNetListener.getSocketChannel().writeAndFlush(renamingFile);

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
