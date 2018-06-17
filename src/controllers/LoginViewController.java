package controllers;

import java.net.URL;
import db.DbManager;
import helper.Formatter;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.skife.jdbi.v2.DBI;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {
    @FXML
    Button loginButton;

    @FXML
    TextField loginTextField, passwordTextField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        EventHandler<KeyEvent> loginAction = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER)
                    try {
                        login();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
            }
        };
        passwordTextField.setOnKeyPressed(loginAction);
        loginTextField.setOnKeyPressed(loginAction);
        loginButton.setOnKeyPressed(loginAction);
    }

    @FXML
    private void login() throws IOException, SQLException, NoSuchAlgorithmException {
        DBI dbConnection = DbManager.getDbConnection();
        if(dbConnection == null) {
            Stage stage = new Stage();
            Parent root;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MessageView.fxml"));
            root = loader.load();
            stage.setScene(new Scene(root, 300, 120));
            MessageViewController controller = loader.getController();
            controller.setWindowProperties("Błąd połączenia", "Nie można połączyć z bazą danych, spróbuj ponownie za chwilę.");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
        else {
            boolean userExists = DbManager.getDao().checkIfUserExists(loginTextField.getText());
            if(userExists) {
                boolean validUser = DbManager.getDao().checkUserCredentials(loginTextField.getText(), Formatter.hashPassword(passwordTextField.getText()));
                if(validUser) {
                    Formatter.setUserName(loginTextField.getText());
                    Stage stage;
                    Parent root;
                    stage = (Stage) loginButton.getScene().getWindow();
                    root = FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));
                    stage.setScene(new Scene(root, 640, 500));
                    stage.setResizable(false);
                    double x = Screen.getPrimary().getVisualBounds().getWidth();
                    double y = Screen.getPrimary().getVisualBounds().getHeight();
                    stage.setX(x/2 - stage.getWidth()/2);
                    stage.setY(y/2 - stage.getHeight()/2);
                    stage.show();
                }
                else {
                    Stage stage = new Stage();
                    Parent root;
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MessageView.fxml"));
                    root = loader.load();
                    stage.setScene(new Scene(root, 300, 120));
                    MessageViewController controller = loader.getController();
                    controller.setWindowProperties("Błąd logowania", "Login lub hasło są nieprawidłowe!");
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                }
            }

        }
    }
}
