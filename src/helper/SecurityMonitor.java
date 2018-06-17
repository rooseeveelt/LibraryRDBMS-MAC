package helper;
import db.DbManager;

/**
 * Created by Marcin on 2018-04-29.
 */
public class SecurityMonitor {

    public static boolean canUserPerformOperation(String tableName, OperationType operationType) {
        Integer userLabel = DbManager.getDao().getSecurityLabelForUser(helper.Formatter.getUsername());
        Integer tableLabel = DbManager.getDao().getSecurityLabelForTable(tableName);
        int security = securityPolicyComparator(userLabel, tableLabel);
        boolean result = false;
        switch(security) {
            case -1:
                if(operationType.equals(OperationType.SELECT))
                    result = true;
                else
                    result = false;
                break;
            case 0:
                result = true;
                break;
            case 1:
                if(operationType.equals(OperationType.INSERT))
                    result = true;
                else
                    result = false;
                break;
        }
        return result;
    }

    public static int securityPolicyComparator(Integer clearance, Integer classification) {
        if(clearance > classification)
            return 1; //we can write to object

        else if(clearance < classification)
            return -1; //we can read from object

        else
            return 0; //we can do both
    }
}
