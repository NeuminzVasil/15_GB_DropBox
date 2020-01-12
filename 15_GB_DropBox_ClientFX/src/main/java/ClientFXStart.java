import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientFXStart extends Application {

    public static void main(String[] args) {

        if (args.length > 0) {
            CommonVariables.HOST_NAME = args[0];
            CommonVariables.HOST_PORT = Integer.parseInt(args[1]);
        }

        launch(args); // NL блокирующая команда работы окна.

        CommonVariables.clientNetListener.closeConnection();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("loginWindow.fxml"));
        primaryStage.setTitle("Login window.");
        primaryStage.setScene(new Scene(root, 300, 150));
        primaryStage.show();
    }

}
