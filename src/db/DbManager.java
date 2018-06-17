package db;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.DefaultMapper;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;

/**
 * Created by Marcin on 18.03.2018.
 */
public class DbManager {
    private static DBI dbConnection = null;
    private static LepiejSieNieDAO dao = null;

    protected DbManager() {
    }

    public static DBI getDbConnection() {
        if(dbConnection == null) {
            try {
                dbConnection = new DBI("jdbc:mysql://sql7.freesqldatabase.com/sql7240641", "sql7240641", "ublQGFkv5P");
                dbConnection.registerMapper(new DefaultMapper());
                dao = dbConnection.open(LepiejSieNieDAO.class);
            }
            catch(UnableToObtainConnectionException ex)
            {
                dbConnection = null;
            }
        }
        return dbConnection;
    }

    public static LepiejSieNieDAO getDao() { return dao; }

    public static void close() {
        if(dbConnection != null) {
            dbConnection.close(dao);
            dbConnection = null;
            dao = null;
        }
    }
}
