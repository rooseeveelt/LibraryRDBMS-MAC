package db;

import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;
import java.util.Map;

/**
 * Created by Marcin on 21.03.2018.
 */
@UseStringTemplate3StatementLocator
public abstract class  LepiejSieNieDAO {

    public final String host = "localhost";

    /**
     * Zapytanie zwracające wszystkie wartości podanej kolumny w wybranej tabeli.
     * @param columnName
     * @param tableName
     * @return
     */
    @SqlQuery("SELECT <columnName> FROM <tableName>")
    public abstract List<String> getColumns(@Define("columnName") String columnName,
                                            @Define("tableName") String tableName);

    /**
     * Zapytanie zwracające wszystkie dane z wybranej tabeli.
     * @param tableName = nazwa tabeli
     * @return
     */
    @SqlQuery("SELECT * FROM <tableName>")
    public abstract List<Map<String, Object>> getTable(@Define("tableName") String tableName);

    /**
     * Pobiera nazwę aktualnego użytkownika.
     * @return
     */
    @SqlQuery("SELECT CURRENT_USER()")
    public abstract String getUsername();

    /**
     * Pobiera etykiete bezpieczeństwa dla podanego użytkownika.
     * @param username
     * @return
     */
    @SqlQuery("SELECT etykieta FROM Uzytkownik WHERE login = :username")
    public abstract Integer getSecurityLabelForUser(@Bind("username") String username);

    /**
     * Pobiera etykiete bezpieczeństwa dla podanej tabeli.
     * @param tableName
     * @return
     */
    @SqlQuery("SELECT etykieta FROM Obiekt WHERE nazwa = :tableName")
    public abstract Integer getSecurityLabelForTable(@Bind("tableName") String tableName);

    /**
     * Tworzy nowego użytkownika w bazie danych.
     * @param userName
     * @param host
     * @param password
     */
    @SqlUpdate("CREATE USER :userName@:host " +
            "IDENTIFIED BY :password")
    public abstract void createUser(@Bind("userName") String userName,
                                    @Bind("host") String host,
                                    @Bind("password") String password);

    @SqlUpdate("DROP USER :userName@:host")
    public abstract void dropUser(@Bind("userName") String userName,
                                  @Bind("host") String host);

    /**
     * Dodaje nowy wpis do podanej tabeli.
     * @param tableName
     * @param columns
     * @param values
     */
    @SqlUpdate("INSERT INTO <tableName> (<columns>) VALUES (<values>)")
    public abstract void insertIntoTable(@Define("tableName") String tableName,
                                         @Define("columns") String columns,
                                         @Define("values") String values);

    @SqlUpdate("GRANT ALL ON *.* TO :userName@:host")
    public abstract void grantPrivileges(@Bind("userName") String userName, @Bind("host") String host);

    @SqlCall("FLUSH PRIVILEGES")
    public abstract void flushPrivileges();

    @Transaction
    public boolean createNewUser(String login, String password, String columnNames, String values) {
        boolean result = true;
        if(!checkIfUserExists(login)) {
            try {
                createUser(login, this.host, password);
                insertIntoTable("Uzytkownik", columnNames, values);
                grantPrivileges(login, host);
            } catch (UnableToExecuteStatementException ex) {
                dropUser(login, this.host);
                throw ex;
            } finally {
                flushPrivileges();
            }
        }
        else
            result = false;
        return result;
    }

    public boolean checkIfUserExists(String login) {
        List<String> queryResult = selectColumnFromTable("login", "Uzytkownik", "login = '" + login + "'");
        if(queryResult.isEmpty())
            return false;
        else
            return queryResult.get(0).equals(login);
    }

    public List<String> getTables() {
        return getColumns("nazwa", "Obiekt");
    }

    @SqlQuery("SELECT * FROM <tableName><condition>")
    public abstract List<Map<String, Object>> selectFromTable(@Define("tableName") String tableName,
                                                              @Define("condition") String condition);

    @SqlQuery("SELECT <columnName> FROM <tableName> WHERE <condition>")
    public abstract List<String> selectColumnFromTable(@Define("columnName") String columnName,
                                                       @Define("tableName") String tableName,
                                                       @Define("condition") String condition);

    @SqlUpdate("UPDATE <tableName> SET <dataSet><condition>")
    public abstract void updateTable(@Define("tableName") String tableName,
                                     @Define("dataSet") String dataSet,
                                     @Define("condition") String condition);

    @SqlUpdate("DELETE FROM <tableName><condition>")
    public abstract void deleteFromTable(@Define("tableName") String tableName,
                                         @Define("condition") String condition);

    public boolean checkUserCredentials(String userName, String password) {
        List<Map<String, Object>> user = selectFromTable("Uzytkownik", " WHERE login = '" + userName + "' AND haslo = '" + password + "'");
        return !user.isEmpty();
    }
}
