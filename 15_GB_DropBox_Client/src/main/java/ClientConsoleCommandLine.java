import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ClientConsoleCommandLine implements Runnable {

    private String clientName = "ClientConsoleCommandLine";
    private ClientNetListener clientNetListener; // ссылка на клиентское подключение
    private InputStreamReader inputStream = new InputStreamReader(System.in); // объект чтения данных из потока консоли
    private BufferedReader bufferedReader = new BufferedReader(inputStream); // объект чтения данных из консоли
    private StringBuilder consoleCommand = new StringBuilder(); // введенная в консоль пользователем
    private CommandAnswer networkCommand = new CommandsList(); //объект "команды пользователя"

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

                if (!!(consoleCommand.insert(0, bufferedReader.readLine()).toString().toLowerCase().equals("~#stop"))) // NL обработка команды "стоп консоль"
                    break;
                if (consoleCommand.toString().toLowerCase().startsWith("~?")) {// NL обработка команды "справка"
                    System.out.println(CommandsList.commandsInfo);
                } else if (consoleCommand.toString().toLowerCase().startsWith("~si") ||
                        consoleCommand.toString().toLowerCase().startsWith("~lu") ||
                        consoleCommand.toString().toLowerCase().startsWith("~gf") ||
                        consoleCommand.toString().toLowerCase().startsWith("~sf") ||
                        consoleCommand.toString().toLowerCase().startsWith("~df") ||
                        consoleCommand.toString().toLowerCase().startsWith("~rf")) {

                    networkCommand.sendingSettings(consoleCommand.toString(), CommandAnswer.WhoIsSender.CLIENT);
                    clientNetListener.getSocketChannel().writeAndFlush(networkCommand);

                } else { // NL обработка неизвестной команды
                    System.err.println("Console: unknown command!");
                }
                consoleCommand.setLength(0);
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
