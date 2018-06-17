package controllers;

import db.DbManager;
import db.dto.MappedDTO;
import db.dto.MappedDTOFactory;
import db.dto.Uzytkownik;
import helper.AnnotationsChecker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by Marcin on 2018-04-30.
 */
public class DeleteViewController implements Initializable {
    @FXML
    Stage stage;
    @FXML
    Button okButton;
    String tableName;

    MappedDTO selectedObject;

    public boolean isMyUserBeingDeleted = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setTitle(String tableName, MappedDTO object) {
        this.tableName = tableName;
        this.stage = (Stage)okButton.getScene().getWindow();
        this.stage.setTitle("Usuwanie");
        this.selectedObject = object;
    }

    public String getConditions() throws NoSuchFieldException {
        Map<String, Object> selectedObjectMap = helper.Formatter.getMapFromDTO(selectedObject, true);
        StringBuilder condition = new StringBuilder();
        condition.append(" WHERE ");
        for(Map.Entry<String, Object> entry : selectedObjectMap.entrySet()) {
            if(AnnotationsChecker.checkPrimaryKey(MappedDTOFactory.getDTOForTable(this.tableName).getClass(), entry.getKey())) {
                condition.append(entry.getKey()).append("=").append("\"" + entry.getValue() + "\"");
                condition.append(" AND ");
            }
        }
        String result;
        if(condition.lastIndexOf(" AND ") > 0)
            result = condition.substring(0, condition.lastIndexOf(" AND "));
        else
            result = condition.toString();
        //System.out.println(result);
        return result;
    }

    @FXML
    private void okButtonAction() throws IOException, NoSuchFieldException {
        DbManager.getDao().deleteFromTable(this.tableName, getConditions());
        if(this.tableName.toLowerCase().equals(new Uzytkownik().getTablename().toLowerCase())) {
            String userNameFromDao = helper.Formatter.getUsername();
            Map<String, Object> selectedObjectMap = helper.Formatter.getMapFromDTO(selectedObject, false);
            String userName = (String)selectedObjectMap.get("login");
            if(userName.equals(userNameFromDao)) {
                isMyUserBeingDeleted = true;
            }
        }
        this.stage.close();
    }

    @FXML
    private void noButtonAction() {
        this.stage.close();
    }
}
