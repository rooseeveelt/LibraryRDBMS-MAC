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
public class Wypozyczenie extends MappedDTO implements Serializable {

    @PrimaryKey
    public Integer id;
    public Date dataRozpoczecia;
    public Date dataZakonczenia;
    @ForeignKey(tableName = "czytelnik")
    public Integer czytelnik;
    @ForeignKey(tableName = "pracownik")
    public Integer pracownik;

    public Wypozyczenie() {
        this.id = null;
        this.dataRozpoczecia = null;
        this.dataZakonczenia = null;
        this.czytelnik = null;
        this.pracownik = null;
        this.types.put("id", Integer.class);
        this.types.put("dataRozpoczecia", Date.class);
        this.types.put("dataZakonczenia", Date.class);
        this.types.put("czytelnik", Integer.class);
        this.types.put("pracownik", Integer.class);
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Date getDataRozpoczecia() {
        return dataRozpoczecia;
    }
    public void setDataRozpoczecia(Date dataRozpoczecia) {
        this.dataRozpoczecia = dataRozpoczecia;
    }
    public Date getDataZakonczenia() {
        return dataZakonczenia;
    }
    public void setDataZakonczenia(Date dataZakonczenia) {
        this.dataZakonczenia = dataZakonczenia;
    }
    public Integer getCzytelnik() {
        return czytelnik;
    }
    public void setCzytelnik(Integer czytelnik) {
        this.czytelnik = czytelnik;
    }
    public Integer getPracownik() {
        return pracownik;
    }
    public void setPracownik(Integer pracownik) {
        this.pracownik = pracownik;
    }

    @Override
    public String toString() {
        return id +
                ", " + dataRozpoczecia +
                ", " + dataZakonczenia +
                ", " + czytelnik +
                ", " + pracownik;
    }

    @Override
    public Wypozyczenie map(Map<String, Object> obj) {
        this.id = (Integer)obj.get("id");
        this.dataRozpoczecia = (Date)obj.get("dataRozpoczecia");
        this.dataZakonczenia = (Date)obj.get("dataZakonczenia");
        this.czytelnik = (Integer)obj.get("czytelnik");
        this.pracownik = (Integer)obj.get("pracownik");
        return this;
    }

    public ObservableList<String> asObservableList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add(id.toString());
        list.add(dataRozpoczecia.toString());
        list.add(dataZakonczenia.toString());
        list.add(czytelnik.toString());
        list.add(pracownik.toString());
        return list;
    }

    @Override
    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        TableColumn idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<Wypozyczenie, Integer>("id"));
        columns.add(idCol);
        TableColumn dataRCol = new TableColumn<>("Data rozpoczecia");
        dataRCol.setCellValueFactory(new PropertyValueFactory<Wypozyczenie, Date>("dataRozpoczecia"));
        columns.add(dataRCol);
        TableColumn dataZCol = new TableColumn<>("Data zakonczenia");
        dataZCol.setCellValueFactory(new PropertyValueFactory<Wypozyczenie, Date>("dataZakonczenia"));
        columns.add(dataZCol);
        TableColumn idCzytCol = new TableColumn<>("Id czytelnika");
        idCzytCol.setCellValueFactory(new PropertyValueFactory<Wypozyczenie, Integer>("czytelnik"));
        columns.add(idCzytCol);
        TableColumn idPracCol = new TableColumn<>("Id pracownika");
        idPracCol.setCellValueFactory(new PropertyValueFactory<Wypozyczenie, Integer>("pracownik"));
        columns.add(idPracCol);
        return columns;
    }

    @Override
    public String getTablename() {
        return "Wypozyczenie";
    }

    @Override
    public String getColumnsNames() {
        return "dataRozpoczecia, dataZakonczenia, czytelnik, pracownik";
    }
}
