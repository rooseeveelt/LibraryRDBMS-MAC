package db.dto;

import annotations.PrimaryKey;
import com.sun.istack.internal.NotNull;
import annotations.Nullable;
import com.sun.javafx.beans.IDProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcin on 29.03.2018.
 */
public class Obiekt extends MappedDTO implements Serializable {

    @PrimaryKey
    public Integer id;
    public String nazwa;
    public Integer etykieta;


    public Obiekt(){
        this.id = null;
        this.nazwa = null;
        this.etykieta = null;
        this.types.put("id", Integer.class);
        this.types.put("nazwa", String.class);
        this.types.put("etykieta", Integer.class);
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNazwa() {
        return nazwa;
    }
    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }
    public Integer getEtykieta() {
        return etykieta;
    }
    public void setEtykieta(Integer etykieta) {
        this.etykieta = etykieta;
    }

    @Override
    public String toString() {
        return id +
                ", " + nazwa +
                ", " + etykieta;
    }

    public ObservableList<String> asObservableList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add(id.toString());
        list.add(nazwa);
        list.add(etykieta.toString());
        return list;
    }

    @Override
    public Obiekt map(Map<String, Object> obj) {
        this.id = (Integer)obj.get("id");
        this.nazwa = (String)obj.get("nazwa");
        this.etykieta = (Integer)obj.get("etykieta");
        return this;
    }

    @Override
    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        TableColumn idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<Obiekt, Integer>("id"));
        columns.add(idCol);
        TableColumn nazwaCol = new TableColumn<>("Nazwa");
        nazwaCol.setCellValueFactory(new PropertyValueFactory<Obiekt, String>("nazwa"));
        columns.add(nazwaCol);
        TableColumn etykietaCol = new TableColumn<>("Etykieta");
        etykietaCol.setCellValueFactory(new PropertyValueFactory<Obiekt, Integer>("etykieta"));
        columns.add(etykietaCol);
        return columns;
    }

    @Override
    public String getTablename() {
        return "Obiekt";
    }

    @Override
    public String getColumnsNames() {
        return "nazwa, etykieta";
    }
}
