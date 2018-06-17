package db.dto;

/**
 * Created by Marcin on 28.03.2018.
 */
public class MappedDTOFactory {
    public static MappedDTO getDTOForTable(String tableName) {
        switch(tableName.toLowerCase()) {
            case "czytelnik":
                return new Czytelnik();
            case "ksiazka":
                return new Ksiazka();
            case "obiekt":
                return new Obiekt();
            case "pozycja":
                return new Pozycja();
            case "pracownik":
                return new Pracownik();
            case "uzytkownik":
                return new Uzytkownik();
            case "wypozyczenie":
                return new Wypozyczenie();
        }
        return null;
    }
}
