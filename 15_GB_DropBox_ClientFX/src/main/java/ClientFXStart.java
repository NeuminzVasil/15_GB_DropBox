import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientFXStart extends Application {

    public static void main(String[] args) {

        if (args.length > 0) {
            CommonVar.HOST_NAME = args[0];
            CommonVar.HOST_PORT = Integer.parseInt(args[1]);
        }

        // запускаем поток подлючения к серверу
        CommonVar.clientNetListener = new ClientNetListener(CommonVar.clientName);
        Thread clientNetListenerThread = new Thread(CommonVar.clientNetListener);
        clientNetListenerThread.start();

        launch(args); // NL блокирующая команда работы окна.

        CommonVar.clientNetListener.getSocketChannel().closeFuture();
        CommonVar.clientNetListener.getSocketChannel().close();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("loginWindow.fxml"));
        primaryStage.setTitle(CommonVar.clientName + ". Login window.");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
