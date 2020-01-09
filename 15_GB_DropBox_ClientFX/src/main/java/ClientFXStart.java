import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientFXStart extends Application {

    public static void main(String[] args) {

        if (args.length > 0) {
            Settings.HOST_NAME = args[0];
            Settings.HOST_PORT = Integer.parseInt(args[1]);
        }

        // запускаем подлючение к серверу
        ClientSharedVariables.clientNetListener = new ClientNetListener(ClientSharedVariables.clientName);
        Thread clientNetListenerThread = new Thread(ClientSharedVariables.clientNetListener);
        clientNetListenerThread.start();

        launch(args); // NL блокирующая команда работы окна.

        ClientSharedVariables.clientNetListener.getSocketChannel().closeFuture();
        ClientSharedVariables.clientNetListener.getSocketChannel().close();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("loginWindow.fxml"));
        primaryStage.setTitle(ClientSharedVariables.clientName + ". Login window.");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
