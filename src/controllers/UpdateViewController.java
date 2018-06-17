package controllers;

import db.DbManager;
import db.dto.MappedDTO;
import db.dto.MappedDTOFactory;
import helper.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by Marcin on 2018-04-29.
 */
public class UpdateViewController implements Initializable {
    @FXML
    ListView columnsListView;
    @FXML
    Stage stage;
    @FXML
    HBox columnsListHBox;

    List<TextField> textFieldsList;

    String tableName;

    MappedDTO selectedObject;
    Map<String, Object> selectedObjectMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        columnsListView.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                ObservableList<String> items = columnsListView.getItems();
                List<String> selectedItems = new ArrayList<>();
                for(String i: items) {
                    if(columnsListView.getSelectionModel().isSelected(items.indexOf(i))) {
                        try {
                            String foreignTable = AnnotationsChecker.getForeignTableForField(MappedDTOFactory.getDTOForTable(tableName).getClass(), i);
                            if(foreignTable != null) {
                                if(!SecurityMonitor.canUserPerformOperation(foreignTable, OperationType.SELECT))
                                    columnsListView.getSelectionModel().clearSelection(items.indexOf(i));
                                else
                                    selectedItems.add(i);
                            }
                            else
                                selectedItems.add(i);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                }
                for(TextField tf: textFieldsList) {
                    tf.setEditable(false);
                    tf.setPromptText("");
                    if(!selectedItems.contains(tf.getId())) {
                        String text;
                        if(selectedObjectMap.get(tf.getId()) != null)
                            text = selectedObjectMap.get(tf.getId()).toString();
                        else
                            text = "";
                        tf.setPromptText(text);
                        tf.setText(text);
                    }
                }
                for(String s: selectedItems) {
                    for(TextField tf: textFieldsList) {
                        if(tf.getId().equals(s)) {
                            tf.setEditable(true);
                            String text;
                            if(selectedObjectMap.get(tf.getId()) != null)
                                text = selectedObjectMap.get(tf.getId()).toString();
                            else
                                text = "";
                            tf.setPromptText(text);
                            tf.setText(text);
                        }
                    }
                }
            }
        });
    }

    public void setTitle(String tableName, MappedDTO selectedObject) throws NoSuchFieldException {
        this.tableName = tableName;
        stage = (Stage)columnsListView.getScene().getWindow();
        stage.setTitle("Edytowanie");
        this.selectedObject = selectedObject;
        this.selectedObjectMap = helper.Formatter.getMapFromDTO(selectedObject, false);
        populateListView();
    }

    private void populateListView() throws NoSuchFieldException {
        if(MappedDTOFactory.getDTOForTable(tableName) != null) {
            List<String> columnNames = new ArrayList<>(Arrays.asList(MappedDTOFactory.getDTOForTable(tableName).getColumnsNames().split(", ")));
            ObservableList<String> data = FXCollections.observableArrayList(columnNames);
            columnsListView.setItems(data);
            textFieldsList = new ArrayList<>();
            VBox vbox = new VBox();
            for(String c: columnNames) {
                TextField t;
                if(AnnotationsChecker.checkPassword(MappedDTOFactory.getDTOForTable(tableName).getClass(), c))
                    t = new PasswordField();
                else
                    t = new TextField();
                t.setId(c);
                t.setEditable(false);
                String text;
                if(selectedObjectMap.get(t.getId()) != null)
                    text = selectedObjectMap.get(t.getId()).toString();
                else
                    text = "";
                String foreignTable = AnnotationsChecker.getForeignTableForField(MappedDTOFactory.getDTOForTable(tableName).getClass(), c);
                if(foreignTable != null) {
                    if(!SecurityMonitor.canUserPerformOperation(foreignTable, OperationType.SELECT))
                        t.setPromptText("Brak uprawnień");
                }
                t.setPromptText(text);
                t.setText(text);
                t.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent e) {
                        if(t.isEditable()) {
                            String foreignTable = null;
                            try {
                                foreignTable = AnnotationsChecker.getForeignTableForField(MappedDTOFactory.getDTOForTable(tableName).getClass(), c);
                            }
                            catch (NoSuchFieldException e1) {
                                e1.printStackTrace();
                            }
                            if(foreignTable != null) {
                                if(!SecurityMonitor.canUserPerformOperation(foreignTable, OperationType.SELECT))
                                    t.setPromptText("Brak uprawnień");
                                else {
                                    Stage stage = new Stage();
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ForeignTableView.fxml"));
                                    Parent root = null;
                                    try {
                                        root = loader.load();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    stage.setScene(new Scene(root, 350, 200));
                                    ForeignTableViewController controller = loader.getController();
                                    controller.populateTable(foreignTable, DbManager.getDao().getTable(foreignTable));
                                    stage.initModality(Modality.APPLICATION_MODAL);
                                    stage.showAndWait();
                                    if(controller.isReady()) {
                                        t.setText(controller.getId().toString());
                                    }
                                    t.getParent().requestFocus();
                                }
                            }
                        }
                    }
                });
                t.textProperty().addListener((observable, oldValue, newValue) -> {
                    selectedObjectMap.replace(t.getId(), newValue);
                });
                textFieldsList.add(t);
                vbox.getChildren().add(t);
            }
            columnsListHBox.getChildren().add(vbox);
        }
        double cellHeight = columnsListView.getFixedCellSize();
        stage.setHeight(columnsListView.getItems().size()*cellHeight + 100);
    }

    private List<String> checkIfAllValuesEntered() throws NoSuchFieldException {
        ObservableList<String> items = columnsListView.getItems();
        List<String> selectedItems = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for(String i: items) {
            if(columnsListView.getSelectionModel().isSelected(items.indexOf(i)))
                selectedItems.add(i);
        }
        for (String s : selectedItems) {
            for (TextField tf : textFieldsList) {
                if (tf.getId().equals(s)) {
                    if (tf.getText().isEmpty()) {
                        if(!AnnotationsChecker.checkNullable(MappedDTOFactory.getDTOForTable(this.tableName).getClass(), tf.getId()))
                            result.add(tf.getId());
                    }
                }
            }
        }
        return result;
    }

    private List<String> checkTypeConsistency() {
        ObservableList<String> items = columnsListView.getItems();
        List<String> selectedItems = new ArrayList<>();

        List<String> result = new ArrayList<>();
        for(String i: items) {
            if(columnsListView.getSelectionModel().isSelected(items.indexOf(i)))
                selectedItems.add(i);
        }
        for (String s : selectedItems) {
            for (TextField tf : textFieldsList) {
                if (tf.getId().equals(s)) {
                    if(!MappedDTOFactory.getDTOForTable(this.tableName).checkTypesConsistency(tf.getId(), tf.getText()))
                        result.add(tf.getId());
                }
            }
        }
        return result;
    }

    public String getDataSet() throws NoSuchAlgorithmException {
        StringBuilder dataSet = new StringBuilder();
        ObservableList<String> items = columnsListView.getItems();
        List<String> selectedItems = new ArrayList<>();

        List<String> result = new ArrayList<>();
        for(String i: items) {
            if(columnsListView.getSelectionModel().isSelected(items.indexOf(i)))
                selectedItems.add(i);
        }
        for (String s : selectedItems) {
            for (TextField tf : textFieldsList) {
                if (tf.getId().equals(s)) {
                    if(tf.getText() != "") {
                        if(tf.getClass().equals(PasswordField.class))
                            dataSet.append(tf.getId()).append("=").append("\"" + helper.Formatter.hashPassword(tf.getText()) + "\"");
                        else
                            dataSet.append(tf.getId()).append("=").append("\"" + tf.getText() + "\"");
                        dataSet.append(" , ");
                    }
                }
            }
        }
        String data;
        if(dataSet.lastIndexOf(" , ") > 0)
            data = dataSet.substring(0, dataSet.lastIndexOf(" , "));
        else
            data = dataSet.toString();
        return data;
    }

    public String getCondition() throws NoSuchFieldException {
        StringBuilder conditions = new StringBuilder();
        Map<String, Object> map = helper.Formatter.getMapFromDTO(selectedObject, true);
        conditions.append(" WHERE ");
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            if(entry.getValue() != null) {
                if(AnnotationsChecker.checkPrimaryKey(MappedDTOFactory.getDTOForTable(this.tableName).getClass(), entry.getKey())) {
                    conditions.append(entry.getKey()).append("=").append("\"" + entry.getValue() + "\"");
                    conditions.append(" AND ");
                }
            }
        }
        String result;
        if(conditions.lastIndexOf(" AND ") > 0)
            result = conditions.substring(0, conditions.lastIndexOf(" AND "));
        else
            result = conditions.toString();
        return result;
    }

    @FXML
    private void okButtonAction() throws IOException, NoSuchFieldException, NoSuchAlgorithmException {
        if(checkIfAllValuesEntered().isEmpty()) {
            if(checkTypeConsistency().isEmpty()) {
                if(!getDataSet().isEmpty()) {
                    //System.out.println(this.tableName + " " + getDataSet() + " " + getCondition());
                    try {
                        DbManager.getDao().updateTable(this.tableName, getDataSet(), getCondition());
                    } catch (UnableToExecuteStatementException ex) {
                        showBadConstraintsErrorDialog();
                    }
                }
                this.stage.close();
            }
            else
                showWrongDataErrorDialog(checkTypeConsistency());
        }
        else
            showMissingDataErrorDialog(checkIfAllValuesEntered());
    }

    private void showWrongDataErrorDialog(List<String> columnNames) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MessageView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 300, 150));
        MessageViewController controller = loader.getController();
        StringBuilder columns = new StringBuilder();
        for(String s: columnNames) {
            columns.append(s);
            columns.append(", ");
        }
        String column = "kolumn";
        if(columnNames.size() == 1)
            column = "kolumny";
        String cols = columns.substring(0, columns.lastIndexOf(", "));
        controller.setWindowProperties("Niepoprawne dane", "Wprowadzono niepoprawne dane dla " + column + " \"" + cols + "\".");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void showMissingDataErrorDialog(List<String> columnNames) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MessageView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 300, 150));
        MessageViewController controller = loader.getController();
        StringBuilder columns = new StringBuilder();
        for(String s: columnNames) {
            columns.append(s);
            columns.append(", ");
        }
        String column = "kolumn";
        if(columnNames.size() == 1)
            column = "kolumny";
        String cols = columns.substring(0, columns.lastIndexOf(", "));
        controller.setWindowProperties("Brakujące dane", "Zaznaczono, ale nie wprowadzono danych dla " + column + " \"" + cols + "\".");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void showBadConstraintsErrorDialog() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MessageView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 300, 150));
        MessageViewController controller = loader.getController();
        controller.setWindowProperties("Błąd spójności", "Wprowadzono błędny klucz obcy!");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
