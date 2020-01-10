import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
        textFieldLogin.setText("l1");
        textFieldPassword.setText("p1");
    }

    public void btnConnectAction(ActionEvent actionEvent) {

        CommonVar.commandFromUsersUI.append("~lu " +
                textFieldLogin.getText() + " " +
                textFieldPassword.getText());

// NL клиент. Любой интерфейс пользователя, для взаимодействя с сервером, обязан выполнить два пункта:
//  - 1) подготовить команду commandForSend.sendingSettings() принимает 3 параметра:
//      -- мнемокод команды с параметрами,
//      -- суффикс отравителя,
//      -- Регистационный номер клиента, полученный при аутентификации.
//  - 2) отправить команду в сеть writeAndFlush(String ПОДГОТОВЛЕННАЯ_КОМАНДА)
        try {
            CommonVar.commandForSend.sendingSettings(CommonVar.commandFromUsersUI.toString(), CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
            CommonVar.clientNetListener.getSocketChannel().writeAndFlush(CommonVar.commandForSend); // NL клиент. отправка команды в сеть.
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        CommonVar.commandFromUsersUI.setLength(0);

        // NL переключаемся в основное окно
        // TODO Переложить этот код в место которое срабатывает по вводу правильного логина
        try {
            ((Node) actionEvent.getSource()).getScene().getWindow().hide(); // скрываем текущее окно
            Stage mainWindowsStage = new Stage(); // создаем экземпляр сцены
            FXMLLoader loader = new FXMLLoader(); // создаем экземпляр FXMLLoader-а
            Pane root = loader.load(getClass().getResource("mainWindow.fxml").openStream()); // создаем экземпляр корневой "панели??"
            MainWindow mainWindow = loader.getController(); // nl так получаем ссылку на контролелр " этого нвого другого" окна
            mainWindow.fileNameTextField.setText("test Dropthe text"); // nl ..или так передаем что то в другое окно.
            mainWindowsStage.setTitle(textFieldLogin.getText()); // nl ..или так передаем что то в другое окно.
            mainWindowsStage.setScene(new Scene(root)); // создаем "комплект" нового окна.
            mainWindowsStage.show(); // показываем окно пользователю.
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

}

