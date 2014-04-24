package com.thoughtworks.go.utils.primer;

import net.sf.dbdeploy.InMemory;
import net.sf.dbdeploy.database.syntax.MySQLDbmsSyntax;
import net.sf.dbdeploy.exceptions.DbDeployException;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaPrimer {
    private final File deltasAt;
    private final BasicDataSource dataSource;

    public SchemaPrimer(File deltasAt, BasicDataSource dataSource) {
        this.deltasAt = deltasAt;
        this.dataSource = dataSource;
    }

    public void prime(String migratingUser, String changelog) throws SQLException, IOException, ClassNotFoundException, DbDeployException {
        createChangeLogTable(changelog);
        migrate(migratingUser);
    }

    private void migrate(String migratingUser) throws ClassNotFoundException, IOException, DbDeployException, SQLException {
        InMemory dbDeploy = new InMemory(dataSource, new MySQLDbmsSyntax(), deltasAt, "DDL");
        String migrationSql = dbDeploy.migrationSql();
        migrationSql = migrationSql.replace("USER(),", String.format("'%s',", migratingUser));
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(migrationSql);
    }

    private void createChangeLogTable(String changeLogTableName) throws SQLException {
        Connection connection = dataSource.getConnection();

        ResultSet changelog = connection.getMetaData().getTables(null, null, changeLogTableName, null);
        if (!changelog.next()) {
            System.out.println("[DB] Changelog does not exist. Creating new table.");
            Statement statement = connection.createStatement();
            String changelogCreate = "CREATE TABLE changelog (change_number INTEGER NOT NULL, delta_set VARCHAR(10) NOT NULL, start_dt TIMESTAMP NOT NULL, complete_dt TIMESTAMP NULL, applied_by VARCHAR(100) NOT NULL, description VARCHAR(500) NOT NULL)";
            String constraint = "ALTER TABLE changelog ADD CONSTRAINT Pkchangelog PRIMARY KEY (change_number, delta_set)";
            statement.execute(changelogCreate);
            statement.execute(constraint);
        } else {
            System.out.println("[DB] Changelog exists. Skipping creation of a new table.");
        }
    }
}
