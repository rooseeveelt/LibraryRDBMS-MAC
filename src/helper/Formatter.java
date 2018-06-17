package helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import db.DbManager;
import db.dto.MappedDTO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by Marcin on 28.03.2018.
 */
public class Formatter {
    public static String userName;

    public static void setUserName(String newUserName) {
        userName = newUserName;
    }

    public static String getUsername() {
        return userName;
    }

    public static Map<String, Object> getMapFromDTO(MappedDTO obj, boolean withId) {
        List<String> columnNames = Arrays.asList(obj.getColumnsNames().split(", "));
        columnNames = new ArrayList<>(columnNames);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(obj, Map.class);
        Map<String, Object> resultMap = new HashMap<>();
        if(withId) {
            if(map.containsKey("id"))
                columnNames.add("id");
        }
        for(String property : columnNames) {
            resultMap.put(property, map.get(property));
        }
        return resultMap;
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] pwd = password.getBytes();
        md.update(pwd);
        pwd = md.digest();
        String passwordHashed = Formatter.bytesToHex(pwd);
        return passwordHashed;
    }
}
