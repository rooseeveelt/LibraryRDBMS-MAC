package db.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.TableColumn;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.time.Instant;
import java.util.*;

/**
 * Created by Marcin on 28.03.2018.
 */
public abstract class MappedDTO {
    Map<String, Class> types = new HashMap<>();
    public abstract MappedDTO map(Map<String, Object> obj);
    public abstract List<TableColumn> getColumns();
    public abstract String getTablename();
    public abstract String getColumnsNames();
    public abstract String toString();
    public boolean checkTypesConsistency(String columnName, String value) {
        boolean result = true;
        try {
            if(types.get(columnName).equals(Integer.class))
                Integer.parseInt(value);
            else if(types.get(columnName).equals(BigDecimal.class))
                BigDecimal.valueOf(Double.parseDouble(value));
            else if(types.get(columnName).equals(Boolean.class))
                Boolean.valueOf(value);
            else if(types.get(columnName).equals(Date.class)) {
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.parse(value);
            }
            else if(types.get(columnName).equals(Time.class)) {
                List<String> tmp = Arrays.asList(value.split(":"));
                if(tmp.size() == 2 && value.length() == 5)
                    value+=":00";
                Time.valueOf(value);
            }
        }
        catch(Exception ex) {
            result = false;
        }
        finally {
            return result;
        }
    }
}
