package controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Created by Marcin on 11.03.2018.
 */
public class MessageViewController {
    @FXML
    Button confirmButton;
    @FXML
    Label messageLabel;
    @FXML
    Stage stage;

    public void setWindowProperties(String title, String message){
        messageLabel.setText(message);
        confirmButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER)
                    confirmButtonAction();
            }
        });
        stage = (Stage)messageLabel.getScene().getWindow();
        messageLabel.setWrapText(true);
        stage.setTitle(title);
        stage.setResizable(false);
    }

    @FXML
    private void confirmButtonAction(){
        ((Stage)messageLabel.getScene().getWindow()).close();
    }
}
