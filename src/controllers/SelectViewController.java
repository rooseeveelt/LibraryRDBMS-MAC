package controllers;

import db.DbManager;
import db.dto.MappedDTOFactory;
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
 * Created by Marcin on 01.04.2018.
 */
public class SelectViewController implements Initializable {
    @FXML
    ListView columnsListView;
    @FXML
    Stage stage;
    @FXML
    HBox columnsListHBox;

    List<TextField> textFieldsList;

    String tableName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        columnsListView.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                ObservableList<String> items = columnsListView.getItems();
                List<String> selectedItems = new ArrayList<>();
                for(String i: items) {
                    if(columnsListView.getSelectionModel().isSelected(items.indexOf(i)))
                        selectedItems.add(i);
                }
                for(TextField tf: textFieldsList) {
                    tf.setEditable(false);
                    tf.setPromptText("");
                    if(!selectedItems.contains(tf.getId()))
                        tf.setText("");
                }
                for(String s: selectedItems) {
                    for(TextField tf: textFieldsList) {
                        if(tf.getId().equals(s)) {
                            tf.setEditable(true);
                            tf.setPromptText(tf.getId());
                        }
                    }
                }
            }
        });
    }

    public void setTitle(String tableName) {
        this.tableName = tableName;
        stage = (Stage)columnsListView.getScene().getWindow();
        stage.setTitle("Wyszukiwanie");
        populateListView();
    }

    private void populateListView() {
        if(MappedDTOFactory.getDTOForTable(tableName) != null) {
            List<String> columnNames = new ArrayList<>(Arrays.asList(MappedDTOFactory.getDTOForTable(tableName).getColumnsNames().split(", ")));
            ObservableList<String> data = FXCollections.observableArrayList(columnNames);
            columnsListView.setItems(data);
            textFieldsList = new ArrayList<>();
            VBox vbox = new VBox();
            for(String c: columnNames) {
                TextField t = new TextField();
                t.setId(c);
                t.setEditable(false);
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

    public String getConditions() {
        StringBuilder condition = new StringBuilder();
        ObservableList<String> items = columnsListView.getItems();
        List<String> selectedItems = new ArrayList<>();
        for(String i: items) {
            if(columnsListView.getSelectionModel().isSelected(items.indexOf(i)))
                selectedItems.add(i);
        }
        if(selectedItems.size() > 0)
            condition.append(" WHERE ");
        for(String s: selectedItems) {
            for(TextField tf: textFieldsList) {
                if(tf.getId().equals(s)) {
                    condition.append(s).append("=").append("\"" + tf.getText() + "\"");
                    condition.append(" AND ");
                }
            }
        }
        String result;
        if(condition.lastIndexOf(" AND ") > 0)
            result = condition.substring(0, condition.lastIndexOf(" AND "));
        else
            result = condition.toString();
        return result;
    }

    public List<Map<String, Object>> getSelectedData() throws NoSuchFieldException {
        if(checkIfAllValuesEntered().isEmpty() && checkTypeConsistency().isEmpty()) {
            return DbManager.getDao().selectFromTable(this.tableName, getConditions());
        }
        else
            return null;
    }

    @FXML
    private void okButtonAction() throws IOException, NoSuchFieldException {
        if(checkIfAllValuesEntered().isEmpty()) {
            if(checkTypeConsistency().isEmpty())
                this.stage.close();
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
}
