package controllers;

import helper.AnnotationsChecker;
import helper.OperationType;
import helper.SecurityMonitor;
import db.DbManager;
import db.dto.MappedDTO;
import db.dto.MappedDTOFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by Marcin on 11.03.2018.
 */
public class MainViewController implements Initializable {
    private final int WINDOW_WIDTH = 450;
    private final int WINDOW_HEIGHT = 250;

    @FXML
    private
    ComboBox tablesComboBox;

    @FXML
    private
    Button deleteButton, updateButton, insertButton, selectButton;

    @FXML
    private
    TableView tableView;
/*
    @FXML
    private
    Label userNameLabel, tableNameLabel;*/

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> options =
                FXCollections.observableArrayList(DbManager.getDao().getTables());
        tablesComboBox.setItems(options);
        tablesComboBox.getSelectionModel().selectFirst();
        Integer userLevel = DbManager.getDao().getSecurityLabelForUser(helper.Formatter.getUsername());
        //userNameLabel.setText("Użytkownik: " + helper.Formatter.getUsername() + ", etykieta: " + userLevel);
        hideButtons();
    }

    private String getSelectedTableName() {
        return tablesComboBox.getSelectionModel().getSelectedItem().toString();
    }

    @FXML
    private void viewTable() {
        String tableName = getSelectedTableName();
        hideButtons();
        tableView.getColumns().clear();
        populateTable(tableName, DbManager.getDao().getTable(tableName));
        showButtonsForCurrent();
        Integer tableLevel = DbManager.getDao().getSecurityLabelForTable(getSelectedTableName());
       // tableNameLabel.setText("Tabela: " + getSelectedTableName() + ", etykieta: " + tableLevel);
    }

    @FXML
    private void selectButtonAction() throws IOException, NoSuchFieldException {
        if(SecurityMonitor.canUserPerformOperation(getSelectedTableName(), OperationType.SELECT)) {
            Stage stage = new Stage();
            stage.setMinHeight(300);
            stage.setMinWidth(400);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SelectView.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root, 400, 300));
            SelectViewController controller = loader.getController();
            controller.setTitle(getSelectedTableName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            List<Map<String, Object>> selectedData = controller.getSelectedData();
            if(selectedData != null)
                populateTable(controller.tableName, selectedData);
        }
        else
            showAccessErrorDialog("Nie masz uprawnień do wykonania tej operacji!");
    }

    @FXML
    private void updateButtonAction() throws IOException, NoSuchFieldException {
        if(SecurityMonitor.canUserPerformOperation(getSelectedTableName(), OperationType.UPDATE)) {
            MappedDTO selectedObject = (MappedDTO)tableView.getSelectionModel().getSelectedItem();
            if(selectedObject != null) {
                Stage stage = new Stage();
                stage.setMinHeight(300);
                stage.setMinWidth(400);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateView.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
                UpdateViewController controller = loader.getController();
                controller.setTitle(getSelectedTableName(), selectedObject);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                List<Map<String, Object>> selectedData = DbManager.getDao().getTable(this.getSelectedTableName());
                populateTable(controller.tableName, selectedData);
            }
            else
                showNothingSelectedErrorDialog();
        }
        else
            showAccessErrorDialog("Nie masz uprawnień do wykonania tej operacji!");
    }

    @FXML
    private void insertButtonAction() throws IOException, NoSuchFieldException {
        if(SecurityMonitor.canUserPerformOperation(getSelectedTableName(), OperationType.INSERT)) {
            List<String> foreignKeys = AnnotationsChecker.getForeignKeys(MappedDTOFactory.getDTOForTable(this.getSelectedTableName()).getClass());
            List<String> nullables = AnnotationsChecker.getNullables(MappedDTOFactory.getDTOForTable(this.getSelectedTableName()).getClass());
            foreignKeys.removeAll(nullables);
            boolean canInsert = true;
            for (String s: foreignKeys ) {
                String tableName = AnnotationsChecker.getForeignTableForField(MappedDTOFactory.getDTOForTable(this.getSelectedTableName()).getClass(), s);
                if(!SecurityMonitor.canUserPerformOperation(tableName, OperationType.SELECT)) {
                    canInsert = false;
                    break;
                }
            }
            if (canInsert) {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/InsertView.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
                InsertViewController controller = loader.getController();
                controller.setTitle(getSelectedTableName());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                List<Map<String, Object>> selectedData = DbManager.getDao().getTable(this.getSelectedTableName());
                populateTable(controller.tableName, selectedData);
            }
            else
                showAccessErrorDialog("Nie masz uprawnień do wyświetlenia tabeli, do której odwołują się klucze obce!");
        }
        else
            showAccessErrorDialog("Nie masz uprawnień do wykonania tej operacji!");
    }

    @FXML
    private void deleteButtonAction() throws IOException {
        if(SecurityMonitor.canUserPerformOperation(getSelectedTableName(), OperationType.DELETE)) {
            MappedDTO selectedObject = (MappedDTO)tableView.getSelectionModel().getSelectedItem();
            if(selectedObject != null) {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DeleteView.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root, 300, 120));
                DeleteViewController controller = loader.getController();
                controller.setTitle(getSelectedTableName(), selectedObject);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                if(!controller.isMyUserBeingDeleted) {
                    List<Map<String, Object>> selectedData = DbManager.getDao().getTable(this.getSelectedTableName());
                    populateTable(controller.tableName, selectedData);
                }
                else {
                    logoutAction();
                }
            }
            else
                showNothingSelectedErrorDialog();
        }
        else
            showAccessErrorDialog("Nie masz uprawnień do wykonania tej operacji!");
    }

    private void showAccessErrorDialog(String errorMessage) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MessageView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 450, 150));
        MessageViewController controller = loader.getController();
        controller.setWindowProperties("Błąd bezpieczeństwa", errorMessage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void showNothingSelectedErrorDialog() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MessageView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 350, 150));
        MessageViewController controller = loader.getController();
        controller.setWindowProperties("Błąd operacji", "Nie zaznaczono żadnego rekordu do edycji! Zaznacz rekord i ponów operację.");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void populateTable(String tableName, List<Map<String, Object>> rows) {
        if(MappedDTOFactory.getDTOForTable(tableName) != null) {
            tableView.getColumns().clear();
            if(SecurityMonitor.canUserPerformOperation(tableName, OperationType.SELECT)) {
                ObservableList<MappedDTO> data = FXCollections.observableArrayList();
                for (Map<String, Object> r : rows) {
                    MappedDTO row = MappedDTOFactory.getDTOForTable(tableName).map(r);
                    data.add(row);
                }
                tableView.setItems(data);
                tableView.getColumns().addAll(MappedDTOFactory.getDTOForTable(tableName).getColumns());
            }
            tableView.refresh();
        }
    }

    private void hideButtons() {
        deleteButton.setVisible(false);
        updateButton.setVisible(false);
        insertButton.setVisible(false);
        selectButton.setVisible(false);
    }

    private void showButtonsForCurrent() {
        if(SecurityMonitor.canUserPerformOperation(getSelectedTableName(), OperationType.DELETE))
            deleteButton.setVisible(true);
        if(SecurityMonitor.canUserPerformOperation(getSelectedTableName(), OperationType.UPDATE))
            updateButton.setVisible(true);
        if(SecurityMonitor.canUserPerformOperation(getSelectedTableName(), OperationType.INSERT))
            insertButton.setVisible(true);
        if(SecurityMonitor.canUserPerformOperation(getSelectedTableName(), OperationType.SELECT))
            selectButton.setVisible(true);
    }

    @FXML
    public void logoutAction() throws IOException {
        DbManager.close();
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 300, 200));
        stage.setTitle("Baza biblioteki");
        stage.setResizable(false);
        stage.show();
        ((Stage)deleteButton.getScene().getWindow()).close();
    }
}
