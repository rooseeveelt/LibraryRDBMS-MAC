package helper;

import annotations.ForeignKey;
import annotations.Password;
import annotations.PrimaryKey;
import annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marcin on 15.05.2018.
 */
public class AnnotationsChecker<T> {

    public static <T> boolean checkPassword(Class<T> klass, String fieldName) throws NoSuchFieldException {
        Field f = klass.getField(fieldName);
        return (f.getAnnotation(Password.class) != null);
    }

    public static <T> boolean checkPrimaryKey(Class<T> klass, String fieldName) throws NoSuchFieldException {
        Field f = klass.getField(fieldName);
        return (f.getAnnotation(PrimaryKey.class) != null);
    }

    public static <T> List<String> getPrimaryKeys(Class<T> klass) throws NoSuchFieldException {
        List<Field> fields = Arrays.asList(klass.getFields());
        List<String> result = new ArrayList<>();
        for(Field f : fields){
            if(checkPrimaryKey(klass, f.getName()))
                result.add(f.getName());
        }
        return result;
    }

    public static <T> String getForeignTableForField(Class<T> klass, String fieldName) throws NoSuchFieldException {
        Field f = klass.getField(fieldName);
        if(f.getAnnotation(ForeignKey.class) != null)
            return f.getAnnotation(ForeignKey.class).tableName();
        else
            return null;
    }

    public static <T> List<String> getForeignKeys(Class<T> klass) throws NoSuchFieldException {
        List<Field> fields = Arrays.asList(klass.getFields());
        List<String> result = new ArrayList<>();
        for(Field f : fields){
            if(getForeignTableForField(klass, f.getName()) != null)
                result.add(f.getName());
        }
        return result;
    }

    public static <T> List<String> getNullables(Class<T> klass) throws NoSuchFieldException {
        List<Field> fields = Arrays.asList(klass.getFields());
        List<String> result = new ArrayList<>();
        for(Field f : fields){
            if(checkNullable(klass, f.getName()))
                result.add(f.getName());
        }
        return result;
    }

    public static <T> boolean checkNullable(Class<T> klass, String fieldName) throws NoSuchFieldException {
        Field f = klass.getField(fieldName);
        return(f.getAnnotation(Nullable.class) != null);
    }
}
