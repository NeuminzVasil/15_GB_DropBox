
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {

        if (args.length > 0) {
            Settings.HOST_NAME = args[0];
            Settings.HOST_PORT = Integer.parseInt(args[1]);
        }

        // запускаем подлючение к серверу
        Controller.clientNetListener = new ClientNetListener(Controller.clientName);
        Thread clientNetListenerThread = new Thread(Controller.clientNetListener);
        clientNetListenerThread.start();


        launch(args); // NL блокирующая команда работы окна.

        Controller.clientNetListener.getSocketChannel().closeFuture();
        Controller.clientNetListener.getSocketChannel().close();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle(Controller.clientName);
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }
}
