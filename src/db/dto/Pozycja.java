package db.dto;

import annotations.ForeignKey;
import annotations.PrimaryKey;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.Serializable;
import java.sql.Time;
import java.util.*;

/**
 * Created by Marcin on 21.03.2018.
 */
public class Pozycja extends MappedDTO implements Serializable {

    @PrimaryKey
    public Integer id;
    @ForeignKey(tableName = "wypozyczenie")
    public Integer wypozyczenie;
    @ForeignKey(tableName = "ksiazka")
    public Integer ksiazka;

    public Pozycja() {
        this.id = null;
        this.wypozyczenie = null;
        this.ksiazka = null;
        this.types.put("id", Integer.class);
        this.types.put("wypozyczenie", Integer.class);
        this.types.put("ksiazka", Integer.class);
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getWypozyczenie() {
        return wypozyczenie;
    }
    public void setWypozyczenie(Integer wypozyczenie) {
        this.wypozyczenie = wypozyczenie;
    }
    public Integer getKsiazka() {
        return ksiazka;
    }
    public void setKsiazka(Integer ksiazka) {
        this.ksiazka = ksiazka;
    }

    @Override
    public String toString() {
        return id +
                ", " + wypozyczenie +
                ", " + ksiazka;
    }

    @Override
    public Pozycja map(Map<String, Object> obj) {
        this.id = (Integer)obj.get("id");
        this.wypozyczenie = (Integer)obj.get("wypozyczenie");
        this.ksiazka = (Integer)obj.get("ksiazka");
        return this;
    }

    public ObservableList<String> asObservableList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add(id.toString());
        list.add(wypozyczenie.toString());
        list.add(ksiazka.toString());
        return list;
    }

    @Override
    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        TableColumn idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<Pozycja, Integer>("id"));
        columns.add(idCol);
        TableColumn idWypCol = new TableColumn<>("Id wypozyczenia");
        idWypCol.setCellValueFactory(new PropertyValueFactory<Pozycja, Integer>("wypozyczenie"));
        columns.add(idWypCol);
        TableColumn idKsCol = new TableColumn<>("Id ksiazki");
        idKsCol.setCellValueFactory(new PropertyValueFactory<Pozycja, Integer>("ksiazka"));
        columns.add(idKsCol);
        return columns;
    }

    @Override
    public String getTablename() {
        return "Pozycja";
    }

    @Override
    public String getColumnsNames() {
        return "wypozyczenie, ksiazka";
    }
}
