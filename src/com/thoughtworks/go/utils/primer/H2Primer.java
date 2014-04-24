package com.thoughtworks.go.utils.primer;

import com.thoughtworks.go.utils.database.H2Database;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.File;

public class H2Primer extends DatabasePrimer {

    public static void main(String[] args) throws Exception {
        System.out.println("********** Go Database Utilities : H2 Database Primer **********");
        if (args.length != 2) {
            System.err.println("Usage: <executing jar> [path-to-go-db-deltas-directory] [path-to-h2-database-file-without-extension]");
            System.err.println("Example: java -jar goutils.jar /var/lib/go-server/db/h2deltas /tmp/cruise");
            System.exit(1);
        }
        String goDbDeltasPath = args[0];
        String h2Path = args[1];
        File deltasAt = validateInstallationDirectoryAndReturnDeltasPath(goDbDeltasPath);
        BasicDataSource source = validateAndGetH2Connection(h2Path);
        SchemaPrimer primer = new SchemaPrimer(deltasAt, source);
        primer.prime("sa", "CHANGELOG");
        System.out.println("********** May the force be with you **********");
    }

    private static BasicDataSource validateAndGetH2Connection(String h2Path) {
        String connectionString = String.format("jdbc:h2:%s;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", h2Path);
        System.out.println("Connecting to DB at : " + connectionString);
        return new H2Database().createDataSource(connectionString);
    }
}
