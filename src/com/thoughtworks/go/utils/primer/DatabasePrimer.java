package com.thoughtworks.go.utils.primer;

import java.io.File;

public class DatabasePrimer {

    static File validateInstallationDirectoryAndReturnDeltasPath(String goDbDeltasPath) {
        File goDbDeltas = new File(goDbDeltasPath);
        if (!goDbDeltas.exists()) {
            throw new RuntimeException(goDbDeltasPath + " is not a valid go db deltas directory. Usually found under directory : /var/lib/go-server/db");
        }
        return goDbDeltas;
    }

}
