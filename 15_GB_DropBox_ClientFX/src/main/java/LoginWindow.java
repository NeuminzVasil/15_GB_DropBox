import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginWindow implements Initializable {

    @FXML
    TextField textFieldLogin;
    @FXML
    TextField textFieldPassword;
    @FXML
    Button btnConnect;
    @FXML
    Button btnExit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Set login window");
    }

    public void btnConnectAction() {

        ClientSharedVariables.commandFromUsersUI.append("~lu " +
                textFieldLogin.getText() + " " +
                textFieldPassword.getText());

        System.out.println(ClientSharedVariables.commandFromUsersUI);

// NL клиент. Любой интерфейс пользователя, для взаимодействя с сервером, обязан выполнить два пункта:
//  - 1) подготовить команду commandForSend.sendingSettings() принимает 3 параметра:
//      -- мнемокод команды с параметрами,
//      -- суффикс отравителя,
//      -- Регистационный номер клиента, полученный при аутентификации.
//  - 2) отправить команду в сеть writeAndFlush(String ПОДГОТОВЛЕННАЯ_КОМАНДА)
        try {
            ClientSharedVariables.commandForSend.sendingSettings(ClientSharedVariables.commandFromUsersUI.toString(), CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
            ClientSharedVariables.clientNetListener.getSocketChannel().writeAndFlush(ClientSharedVariables.commandForSend); // NL клиент. отправка команды в сеть.
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientSharedVariables.commandFromUsersUI.setLength(0);

        // NL переключаемся в основное окно
        // TODO Переложить этот код в место которое срабатывает по вводу правильного логина
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = null;
        try {
            root = loader.load(getClass().getResource("mainWindow.fxml").openStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Controller controller = loader.getController(); // nl так получаем доступ к контролелру другого окна
        primaryStage.setTitle(ClientSharedVariables.clientName);
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();

    }

}
