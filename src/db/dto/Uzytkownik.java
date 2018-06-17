package db.dto;

import annotations.ForeignKey;
import annotations.Password;
import annotations.PrimaryKey;
import annotations.Nullable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcin on 29.03.2018.
 */
public class Uzytkownik extends MappedDTO implements Serializable {

    @PrimaryKey
    public Integer id;
    public String login;
    @Password
    public String haslo;
    public Integer etykieta;

    public Uzytkownik() {
        this.id = null;
        this.login = null;
        this.haslo = null;
        this.etykieta = null;
        this.types.put("id", Integer.class);
        this.types.put("login", String.class);
        this.types.put("haslo", String.class);
        this.types.put("etykieta", Integer.class);
    }

    public Uzytkownik(String login, String haslo, Integer etykieta) {
        this.id = null;
        this.login = login;
        this.haslo = haslo;
        this.etykieta = etykieta;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getHaslo() { return haslo; }
    public void setHaslo(String haslo) { this.haslo = haslo; }
    public Integer getEtykieta() {
        return etykieta;
    }
    public void setEtykieta(Integer etykieta) {
        this.etykieta = etykieta;
    }

    @Override
    public String toString() {
        return id +
                ", " + login +
                ", " + haslo +
                ", " + etykieta;
    }

    public String getColumnsNames() {
        return "login, haslo, etykieta";
    }

    public ObservableList<String> asObservableList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add(id.toString());
        list.add(login);
        list.add(haslo);
        list.add(etykieta.toString());
        return list;
    }

    @Override
    public Uzytkownik map(Map<String, Object> obj) {
        this.id = (Integer)obj.get("id");
        this.login = (String)obj.get("login");
        this.haslo = (String)obj.get("haslo");
        this.etykieta = (Integer)obj.get("etykieta");
        return this;
    }

    @Override
    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        TableColumn idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<Uzytkownik, Integer>("id"));
        columns.add(idCol);
        TableColumn loginCol = new TableColumn<>("Login");
        loginCol.setCellValueFactory(new PropertyValueFactory<Uzytkownik, String>("login"));
        columns.add(loginCol);
        TableColumn hasloCol = new TableColumn<>("Hasło");
        hasloCol.setCellValueFactory(new PropertyValueFactory<Uzytkownik, String>("haslo"));
        columns.add(hasloCol);
        TableColumn etykietaCol = new TableColumn<>("Etykieta");
        etykietaCol.setCellValueFactory(new PropertyValueFactory<Uzytkownik, Integer>("etykieta"));
        columns.add(etykietaCol);
        return columns;
    }

    @Override
    public String getTablename() {
        return "Uzytkownik";
    }
}
