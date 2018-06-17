package controllers;

import db.dto.MappedDTO;
import db.dto.MappedDTOFactory;
import helper.OperationType;
import helper.SecurityMonitor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

/**
 * Created by Marcin on 17.05.2018.
 */
public class ForeignTableViewController {
    @FXML
    TableView tableView;
    boolean isReady = false;
    Integer id;
    String tableName;

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
            this.tableName = tableName;
            ((Stage)tableView.getScene().getWindow()).setTitle("Wybierz klucz obcy dla: " + tableName);
        }
    }

    @FXML
    public void handleOkButton() {
        if(tableView.getSelectionModel().getSelectedItem() != null) {
            MappedDTO item = (MappedDTO)tableView.getSelectionModel().getSelectedItem();
            Map<String, Object> selectedObjectMap = helper.Formatter.getMapFromDTO(item, true);
            id = (Integer)selectedObjectMap.get("id");
            isReady = true;
            ((Stage)tableView.getScene().getWindow()).close();
        }
    }

    public Integer getId() {
        return this.id;
    }

    public boolean isReady() {
        return this.isReady;
    }
}
