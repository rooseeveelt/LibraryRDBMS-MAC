package controllers;

import db.DbManager;
import db.dto.MappedDTOFactory;
import helper.AnnotationsChecker;
import helper.Formatter;
import helper.OperationType;
import helper.SecurityMonitor;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Marcin on 2018-04-29.
 */
public class InsertViewController implements Initializable {
    List<Label> columnsList;
    @FXML
    Stage stage;
    @FXML
    HBox columnsListHBox;

    List<TextField> textFieldsList;

    String tableName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setTitle(String tableName) throws NoSuchFieldException {
        this.tableName = tableName;
        stage = (Stage)columnsListHBox.getScene().getWindow();
        stage.setTitle("Wstawianie");
        populateListView();
    }

    private void populateListView() throws NoSuchFieldException {
        if(MappedDTOFactory.getDTOForTable(tableName) != null) {
            List<String> columnNames = new ArrayList<>(Arrays.asList(MappedDTOFactory.getDTOForTable(tableName).getColumnsNames().split(", ")));
            columnsList = new ArrayList<>();
            textFieldsList = new ArrayList<>();
            VBox vbox = new VBox();
            VBox labelVBox = new VBox();
            vbox.setSpacing(10);
            labelVBox.setSpacing(10);
            for(String c: columnNames) {
                Label l = new Label();
                l.setPrefSize(140, 25);
                l.setText(c);
                labelVBox.getChildren().add(l);
                columnsList.add(l);
                TextField t;
                if(AnnotationsChecker.checkPassword(MappedDTOFactory.getDTOForTable(tableName).getClass(), c))
                    t = new PasswordField();
                else
                    t = new TextField();
                t.setId(c);
                String foreignTable = AnnotationsChecker.getForeignTableForField(MappedDTOFactory.getDTOForTable(tableName).getClass(), c);
                textFieldsList.add(t);
                vbox.getChildren().add(t);
                if(foreignTable != null) {
                    if (!SecurityMonitor.canUserPerformOperation(foreignTable, OperationType.SELECT)) {
                        t.setEditable(false);
                        t.setPromptText("Brak uprawnień");
                    }
                }
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
            }
            columnsListHBox.getChildren().add(labelVBox);
            columnsListHBox.getChildren().add(vbox);
        }
    }

    private List<String> checkIfAllValuesEntered() throws NoSuchFieldException {
        List<String> result = new ArrayList<>();
        for (Label l : columnsList) {
            for (TextField tf : textFieldsList) {
                if(tf.isEditable()) {
                    if (tf.getId().equals(l.getText())) {
                        if (tf.getText().isEmpty()) {
                            if (!AnnotationsChecker.checkNullable(MappedDTOFactory.getDTOForTable(this.tableName).getClass(), tf.getId()))
                                result.add(tf.getId());
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<String> checkTypeConsistency() {
        List<String> result = new ArrayList<>();
        for (Label l : columnsList) {
            for (TextField tf : textFieldsList) {
                if(tf.isEditable()) {
                    if (tf.getId().equals(l.getText())) {
                        if (!MappedDTOFactory.getDTOForTable(this.tableName).checkTypesConsistency(tf.getId(), tf.getText()))
                            result.add(tf.getId());
                    }
                }
            }
        }
        return result;
    }

    public String getValues() throws NoSuchAlgorithmException {
        StringBuilder values = new StringBuilder();
        int counter = 0;
        for(TextField tf: textFieldsList) {
            if(tf.isEditable()) {
                if(tf.getClass().equals(PasswordField.class))
                    values.append("'" + Formatter.hashPassword(tf.getText()) + "'");
                else
                    values.append("'" + tf.getText() + "'");
            }
            else
                values.append("NULL");
            if(counter < columnsList.size() - 1) {
                counter++;
                values.append(", ");
            }
        }
        return values.toString();
    }

    @FXML
    private void okButtonAction() throws IOException, NoSuchFieldException, NoSuchAlgorithmException {
        if(checkIfAllValuesEntered().isEmpty()) {
            if(checkTypeConsistency().isEmpty()) {
                String columns = MappedDTOFactory.getDTOForTable(this.tableName).getColumnsNames();
                try {
                    DbManager.getDao().insertIntoTable(this.tableName, columns, getValues());
                }
                catch(UnableToExecuteStatementException ex) {
                    showBadConstraintsErrorDialog();
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
        controller.setWindowProperties("Brakujące dane", "Nie wprowadzono danych dla " + column + " \"" + cols + "\".");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void showBadConstraintsErrorDialog() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MessageView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 300, 150));
        MessageViewController controller = loader.getController();
        controller.setWindowProperties("Błąd spójności", "Wprowadzono błędne dane!");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
