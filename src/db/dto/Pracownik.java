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
public class Pracownik extends MappedDTO implements Serializable {

    @PrimaryKey
    public Integer id;
    public String imie;
    public String nazwisko;
    public String pesel;

    public Pracownik() {
        this.id = null;
        this.imie = null;
        this.nazwisko = null;
        this.pesel = null;
        this.types.put("id", Integer.class);
        this.types.put("imie", String.class);
        this.types.put("nazwisko", String.class);
        this.types.put("pesel", String.class);
    }


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getImie() {
        return imie;
    }
    public void setImie(String imie) {
        this.imie = imie;
    }
    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }
    public String getPesel() { return pesel; }
    public void setPesel(String pesel) { this.pesel = pesel; }

    @Override
    public String toString() {
        return id +
                ", " + imie +
                ", " + nazwisko +
                ", " + pesel;
    }

    public ObservableList<String> asObservableList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add(id.toString());
        list.add(imie);
        list.add(nazwisko);
        list.add(pesel);
        return list;
    }

    @Override
    public Pracownik map(Map<String, Object> obj) {
        this.id = (Integer) obj.get("id");
        this.imie = (String) obj.get("imie");
        this.nazwisko = (String) obj.get("nazwisko");
        this.pesel = (String) obj.get("pesel");
        return this;
    }

    @Override
    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        TableColumn idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<Pracownik, Integer>("id"));
        columns.add(idCol);
        TableColumn imieCol = new TableColumn<>("Imie");
        imieCol.setCellValueFactory(new PropertyValueFactory<Pracownik, String>("imie"));
        columns.add(imieCol);
        TableColumn nazwiskoCol = new TableColumn<>("Nazwisko");
        nazwiskoCol.setCellValueFactory(new PropertyValueFactory<Pracownik, String>("nazwisko"));
        columns.add(nazwiskoCol);
        TableColumn miastoCol = new TableColumn<>("PESEL");
        miastoCol.setCellValueFactory(new PropertyValueFactory<Pracownik, String>("pesel"));
        columns.add(miastoCol);
        return columns;
    }

    @Override
    public String getTablename() {
        return "Pracownik";
    }

    @Override
    public String getColumnsNames() {
        return "imie, nazwisko, pesel";
    }
}
