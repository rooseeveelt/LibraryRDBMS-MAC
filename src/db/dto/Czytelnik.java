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
public class Czytelnik extends MappedDTO implements Serializable {

    @PrimaryKey
    public Integer id;
    public String imie;
    public String nazwisko;
    public String miasto;
    public String ulica;
    public String numer;

    public Czytelnik() {
        this.id = null;
        this.imie = null;
        this.nazwisko = null;
        this.miasto = null;
        this.ulica = null;
        this.numer = null;
        this.types.put("id", Integer.class);
        this.types.put("imie", String.class);
        this.types.put("nazwisko", String.class);
        this.types.put("miasto", String.class);
        this.types.put("ulica", String.class);
        this.types.put("numer", String.class);
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
    public String getMiasto() { return miasto; }
    public void setMiasto(String miasto) { this.miasto = miasto; }
    public String getUlica() { return ulica; }
    public void setUlica(String ulica) { this.ulica = ulica; }
    public String getNumer() { return numer; }
    public void setNumer(String numer) { this.numer = numer; }

    @Override
    public String toString() {
        return id +
                ", " + imie +
                ", " + nazwisko +
                ", " + miasto +
                ", " + ulica +
                ", " + numer;
    }

    public ObservableList<String> asObservableList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add(id.toString());
        list.add(imie);
        list.add(nazwisko);
        list.add(miasto);
        list.add(ulica);
        list.add(numer);
        return list;
    }

    @Override
    public Czytelnik map(Map<String, Object> obj) {
        this.id = (Integer) obj.get("id");
        this.imie = (String) obj.get("imie");
        this.nazwisko = (String) obj.get("nazwisko");
        this.miasto = (String) obj.get("miasto");
        this.ulica = (String) obj.get("ulica");
        this.numer = (String) obj.get("numer");
        return this;
    }

    @Override
    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        TableColumn idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<Czytelnik, Integer>("id"));
        columns.add(idCol);
        TableColumn imieCol = new TableColumn<>("Imie");
        imieCol.setCellValueFactory(new PropertyValueFactory<Czytelnik, String>("imie"));
        columns.add(imieCol);
        TableColumn nazwiskoCol = new TableColumn<>("Nazwisko");
        nazwiskoCol.setCellValueFactory(new PropertyValueFactory<Czytelnik, String>("nazwisko"));
        columns.add(nazwiskoCol);
        TableColumn miastoCol = new TableColumn<>("Miasto");
        miastoCol.setCellValueFactory(new PropertyValueFactory<Czytelnik, String>("miasto"));
        columns.add(miastoCol);
        TableColumn ulicaCol = new TableColumn<>("Ulica");
        ulicaCol.setCellValueFactory(new PropertyValueFactory<Czytelnik, String>("ulica"));
        columns.add(ulicaCol);
        TableColumn numerCol = new TableColumn<>("Numer");
        numerCol.setCellValueFactory(new PropertyValueFactory<Czytelnik, String>("numer"));
        columns.add(numerCol);
        return columns;
    }

    @Override
    public String getTablename() {
        return "Czytelnik";
    }

    @Override
    public String getColumnsNames() {
        return "imie, nazwisko, miasto, ulica, numer";
    }
}
