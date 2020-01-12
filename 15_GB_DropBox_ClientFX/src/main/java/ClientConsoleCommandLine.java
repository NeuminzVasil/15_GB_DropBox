import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ClientConsoleCommandLine implements Runnable {

    private String clientName = "ClientConsoleCommandLine";
    private ClientNetListener clientNetListener; // ссылка на клиентское подключение
    private InputStreamReader inputStream = new InputStreamReader(System.in); // объект чтения данных из потока консоли
    private BufferedReader bufferedReader = new BufferedReader(inputStream); // объект чтения данных из консоли
    private StringBuilder commandFromUsersConsole = new StringBuilder(); // введенная в консоль пользователем
    private CommandAnswer commandForSend = new CommandsList(); //объект "команды пользователя"

    /**
     * констуктор консольной программы с созданием клиентского подключения
     */
    public ClientConsoleCommandLine(String hostName, int port) {

        CommonVariables.HOST_NAME = hostName;
        CommonVariables.HOST_PORT = port;

        // запускаем клиентское подлючение к серверу
        clientNetListener = new ClientNetListener(clientName);
        Thread clientNetListenerThread = new Thread(clientNetListener);
        clientNetListenerThread.start();
    }

    public static void main(String[] args) {

        if (args.length > 0) {
            CommonVariables.HOST_NAME = args[0];
            CommonVariables.HOST_PORT = Integer.parseInt(args[1]);
        }

        // запускаем консоль с внутренним запуском клиентского подключения
        ClientConsoleCommandLine clientConsoleCommandLine = new ClientConsoleCommandLine(CommonVariables.HOST_NAME, CommonVariables.HOST_PORT);
        Thread clientConsoleCommandLineThread = new Thread(clientConsoleCommandLine);
        clientConsoleCommandLineThread.start();
    }

    /**
     * метод чтения введенных данных их консоли
     */
    @Override
    public void run() {

        System.out.println("ClientConsoleCommandLine.started");
        try {
            while (true) { // NL клиент. цикл чтения команд с консоли пользователя

// NL клиент. обработка команды "стоп консоль"
                if (!!(commandFromUsersConsole.insert(0, bufferedReader.readLine()).toString().toLowerCase().equals("~#stop")))
                    break;

// NL ЭТО СТАНДАРТНАЯ СХЕМА РАБОТЫ КЛИЕНТА С СЕРВЕРОМ. ТАКОЙ ПРИНЦИП ИСПОЛЬЗУЕТСЯ ДЛЯ ЛЮБОГО СОБЫТИЯ В ПРИЛОЖЕНИИ:
//  Любой интерфейс пользователя, для взаимодействя с сервером, обязан выполнить два пункта:
//  - 1) подготовить команду commandForSend.sendingSettings() принимает 3 параметра:
//      -- мнемокод команды с параметрами,
//      -- суффикс отравителя,
//      -- Регистационный номер клиента, полученный при аутентификации.
//  - 2) отправить команду в сеть writeAndFlush(String ПОДГОТОВЛЕННАЯ_КОМАНДА)
                commandForSend.sendingSettings(commandFromUsersConsole.toString(), CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
                clientNetListener.getSocketChannel().writeAndFlush(commandForSend); // NL клиент. отправка команды в сеть.

                commandFromUsersConsole.setLength(0);
            }

            clientNetListener.getSocketChannel().closeFuture();
            clientNetListener.getSocketChannel().close();

        } catch (
                IOException e) {
            System.err.println(e.getMessage());
        }

    }

}
