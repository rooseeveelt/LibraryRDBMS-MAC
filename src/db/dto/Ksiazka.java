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
public class Ksiazka extends MappedDTO implements Serializable {

    @PrimaryKey
    public Integer id;
    public String nazwa;
    public String autor;
    public String gatunek;

    public Ksiazka() {
        this.id = null;
        this.nazwa = null;
        this.autor = null;
        this.gatunek = null;
        this.types.put("id", Integer.class);
        this.types.put("nazwa", String.class);
        this.types.put("autor", String.class);
        this.types.put("gatunek", String.class);
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
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getGatunek() { return gatunek; }
    public void setGatunek(String gatunek) { this.gatunek = gatunek; }

    @Override
    public String toString() {
        return id +
                ", " + nazwa +
                ", " + autor +
                ", " + gatunek;
    }

    public ObservableList<String> asObservableList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add(id.toString());
        list.add(nazwa);
        list.add(autor);
        list.add(gatunek);
        return list;
    }

    @Override
    public Ksiazka map(Map<String, Object> obj) {
        this.id = (Integer) obj.get("id");
        this.nazwa = (String) obj.get("nazwa");
        this.autor = (String) obj.get("autor");
        this.gatunek = (String) obj.get("gatunek");
        return this;
    }

    @Override
    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        TableColumn idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<Ksiazka, Integer>("id"));
        columns.add(idCol);
        TableColumn nazwaCol = new TableColumn<>("Nazwa");
        nazwaCol.setCellValueFactory(new PropertyValueFactory<Ksiazka, String>("nazwa"));
        columns.add(nazwaCol);
        TableColumn autorCol = new TableColumn<>("Autor");
        autorCol.setCellValueFactory(new PropertyValueFactory<Ksiazka, String>("autor"));
        columns.add(autorCol);
        TableColumn gatunekCol = new TableColumn<>("Gatunek");
        gatunekCol.setCellValueFactory(new PropertyValueFactory<Ksiazka, String>("gatunek"));
        columns.add(gatunekCol);
        return columns;
    }

    @Override
    public String getTablename() {
        return "Ksiazka";
    }

    @Override
    public String getColumnsNames() {
        return "nazwa, autor, gatunek";
    }
}
