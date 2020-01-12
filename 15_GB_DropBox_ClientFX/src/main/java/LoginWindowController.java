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

public class LoginWindowController implements Initializable {

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

        // запускаем поток подлючения к серверу
        CommonVariables.clientNetListener = new ClientNetListener("ClientFX");
        Thread clientNetListenerThread = new Thread(CommonVariables.clientNetListener);
        clientNetListenerThread.start();
    }

    public void loginBtn(ActionEvent actionEvent) {
// NL ЭТО СТАНДАРТНАЯ СХЕМА РАБОТЫ КЛИЕНТА С СЕРВЕРОМ. ТАКОЙ ПРИНЦИП ИСПОЛЬЗУЕТСЯ ДЛЯ ЛЮБОГО СОБЫТИЯ В ПРИЛОЖЕНИИ:
//  Любой интерфейс пользователя, для взаимодействя с сервером, обязан выполнить два пункта:
//  - 1) подготовить команду commandForSend.sendingSettings() принимает 3 параметра:
//      -- мнемокод команды с параметрами,
//      -- суффикс отравителя,
//      -- Регистационный номер клиента, полученный при аутентификации.
//  - 2) отправить команду в сеть writeAndFlush(String ПОДГОТОВЛЕННАЯ_КОМАНДА)
        try {
            CommonVariables.commandForSend.sendingSettings("~lu " + // NL подготовка команды
                            textFieldLogin.getText() + " " +
                            textFieldPassword.getText(),
                    CommandAnswer.WhoIsSender.CLIENT);
            CommonVariables.clientNetListener.getSocketChannel().writeAndFlush(CommonVariables.commandForSend); // NL отправка команды в сеть.
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        CommonVariables.waitForAnswer(20);

        if (CommonVariables.commandForSend.getRegisteredUserID() != null) {
            // NL переключаемся в основное окно
            try {
                ((Node) actionEvent.getSource()).getScene().getWindow().hide(); // скрываем текущее окно
                Stage mainWindowsStage = new Stage(); // создаем экземпляр сцены
                FXMLLoader loader = new FXMLLoader(); // создаем экземпляр FXMLLoader-а
                Pane root = loader.load(getClass().getResource("mainWindow.fxml").openStream()); // создаем экземпляр корневой "панели??"
                mainWindowsStage.setScene(new Scene(root)); // создаем "комплект" нового окна.
                mainWindowsStage.show(); // показываем окно пользователю.
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public void exitBtn(ActionEvent actionEvent) {
        try {
            CommonVariables.clientNetListener.closeConnection();
        } finally {
            ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close(); // скрываем текущее окно
/*            ((Node) actionEvent.getSource()).getScene().getWindow().hide(); // скрываем текущее окно
            Stage stage = (Stage) btnExit.getScene().getWindow(); // get a handle to the stage
            stage.close(); // do what you have to do*/

        }
    }

    public void newUserBtn(ActionEvent actionEvent) {
        try {
            CommonVariables.commandForSend.sendingSettings("~ru " +
                            textFieldLogin.getText() + " " + textFieldPassword.getText(),
                    CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды регистрации нового пользователя
            CommonVariables.clientNetListener.getSocketChannel().writeAndFlush(CommonVariables.commandForSend); // NL клиент. отправка команды в сеть.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

