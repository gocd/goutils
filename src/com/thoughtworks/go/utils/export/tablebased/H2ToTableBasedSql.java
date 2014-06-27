package com.thoughtworks.go.utils.export.tablebased;

import com.thoughtworks.go.utils.database.H2Database;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.h2.tools.Csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class H2ToTableBasedSql {

    private static List<String> TABLES = Arrays.asList("AGENTS", "ARTIFACTPLANS", "ARTIFACTPROPERTIESGENERATOR",
            "BUILDCAUSEBUFFER", "BUILDS", "BUILDSTATETRANSITIONS", "ENVIRONMENTVARIABLES", "GADGETOAUTHACCESSTOKENS",
            "GADGETOAUTHAUTHORIZATIONCODES", "GADGETOAUTHCLIENTS", "MATERIALS",
            "MODIFICATIONS", "MODIFIEDFILES", "NOTIFICATIONFILTERS", "OAUTHAUTHORIZATIONS", "OAUTHCLIENTS", "OAUTHTOKENS",
            "PIPELINELABELCOUNTS", "PIPELINEMATERIALREVISIONS", "PIPELINES", "PIPELINESELECTIONS", "PROPERTIES",
            "RESOURCES", "SERVERBACKUPS", "STAGEARTIFACTCLEANUPPROHIBITED", "STAGES", "USERS");

    private final BasicDataSource h2;
    private final File outDir;

    public H2ToTableBasedSql(String h2DbName, String outDir) throws Exception {
        this.h2 = connectToH2Db(h2DbName.replace(".h2.db", ""));
        this.outDir = cleanAndCreateDirectory(outDir);
    }

    public static void main(String[] args) throws Exception {
        validArguments(args);
        H2ToTableBasedSql h2ToTableBasedSql = new H2ToTableBasedSql(args[0], args[1]);
        h2ToTableBasedSql.dump();
    }

    private static void validArguments(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: <executing jar> path-to-h2-db-file path-to-output-directory");
            System.err.println("Example: java -jar goutils.jar /tmp/cruise /tmp/output");
            System.exit(1);
        }
    }

    public void dump() throws Exception {
        generatePgSqlImportScript(outDir);
        generateTableSQL(h2, outDir);
    }

    private File cleanAndCreateDirectory(String argsOutputFile) throws IOException {
        File outputFile = new File(argsOutputFile);
        if (outputFile.exists()) {
            System.out.println("Deleting existing file/directory with name " + outputFile.getAbsolutePath());
            FileUtils.deleteQuietly(outputFile);
        }
        System.out.println("Creating directory " + outputFile.getAbsolutePath());
        FileUtils.forceMkdir(outputFile);
        return outputFile;
    }


    private void generatePgSqlImportScript(File outDir) throws Exception {
        String sqlFileName = "import_csv.sql";
        File outFile = new File(outDir, sqlFileName);
        System.out.println("Creating PGSQL import script " + outFile.getAbsolutePath());
        FileOutputStream os = new FileOutputStream(outFile);
        generateTruncateAndDisableTriggerStatements(os);
        generateCopyStatements(os);
        constructView(os);
        resetSequences(os);
        generateEnableTriggerStartements(os);
        os.close();
    }

    private void generateEnableTriggerStartements(FileOutputStream os) throws Exception {
        for (int i = 0; i < TABLES.size(); i++) {
            IOUtils.writeLines(Arrays.asList("ALTER TABLE " + TABLES.get(i) + " ENABLE TRIGGER ALL"), ";\n", os);
        }
    }

    private void generateCopyStatements(FileOutputStream os) throws IOException {
        for (int i = 0; i < TABLES.size(); i++) {
            String table = TABLES.get(i);
            IOUtils.writeLines(Arrays.asList("\\COPY " + table + " FROM '" + table + ".csv' WITH CSV HEADER"), ";\n", os);
        }
    }

    private void generateTruncateAndDisableTriggerStatements(FileOutputStream os) throws IOException {
        for (int i = 0; i < TABLES.size(); i++) {
            IOUtils.writeLines(Arrays.asList("TRUNCATE TABLE " + TABLES.get(i) + " CASCADE"), ";\n", os);
            IOUtils.writeLines(Arrays.asList("ALTER TABLE " + TABLES.get(i) + " DISABLE TRIGGER ALL"), ";\n", os);
        }
    }

    private void generateTableSQL(BasicDataSource h2, File outDir) throws Exception {
        System.out.println("Dumping CSV files.... ");
        Connection h2c = h2.getConnection();
        for (int i = 0; i < TABLES.size(); i++) {
            String table = TABLES.get(i);
            File outFile = new File(outDir, table + ".csv");
            System.out.println(String.format("Dumping Table %s of %s - %s to %s.csv", i + 1, TABLES.size(), table, table));
            migrateToCSV(table, h2c, outFile);
        }
    }

    private void resetSequences(FileOutputStream os) throws IOException {
        List<String> sequencesQuery = Arrays.asList("select setval('agents_id_seq', (select max(id) from AGENTS))",
                "select setval('artifactPlans_id_seq', (select max(id) from artifactPlans))", "select setval('artifactPropertiesGenerator_id_seq', " +
                "(select max(id) from artifactPropertiesGenerator))", "select setval('buildcausebuffer_id_seq', (select max(id) from BuildCauseBuffer))",
                "select setval('builds_id_seq', (select max(id) from builds))",
                "select setval('buildStateTransitions_id_seq', (select max(id) from buildStateTransitions))",
                "select setval('environmentVariables_id_seq', (select max(id) from environmentvariables))",
                "select setval('gadgetOauthAccessTokens_id_seq', (select max(id) from gadgetoauthaccesstokens))",
                "select setval('gadgetOauthAuthorizationCodes_id_seq', (select max(id) from gadgetoauthauthorizationcodes))",
                "select setval('gadgetOauthClients_id_seq', (select max(id) from gadgetoauthclients))",
                "select setval('newMaterials_id_seq', (select max(id) from materials))",
                "select setval('modifications_id_seq', (select max(id) from modifications))",
                "select setval('modifiedFiles_id_seq', (select max(id) from modifiedFiles))",
                "select setval('notificationfilters_id_seq', (select max(id) from notificationfilters))",
                "select setval('oauthauthorizations_id_seq', (select max(id) from oauthauthorizations))",
                "select setval('oauthclients_id_seq', (select max(id) from oauthclients))",
                "select setval('oauthtokens_id_seq', (select max(id) from oauthtokens))",
                "select setval('pipelineLabelCounts_id_seq', (select max(id) from pipelinelabelcounts))",
                "select setval('pipelinematerialrevisions_id_seq', (select max(id) from pipelinematerialrevisions))",
                "select setval('pipelines_id_seq', (select max(id) from pipelines))",
                "select setval('pipelineSelections_id_seq', (select max(id) from pipelineselections))",
                "select setval('properties_id_seq', (select max(id) from properties))",
                "select setval('resources_id_seq', (select max(id) from resources))",
                "select setval('serverBackups_id_seq', (select max(id) from serverbackups))",
                "select setval('stageArtifactCleanupProhibited_id_seq', (select max(id) from stageartifactcleanupprohibited))",
                "select setval('stages_id_seq', (select max(id) from stages))", "select setval('usersettings_id_seq', (select max(id) from users))");
        IOUtils.writeLines(sequencesQuery, ";\n", os);
    }

    private static void constructView(FileOutputStream os) throws IOException {
        String dropBuildsView = "DROP VIEW _builds;";
        String dropStagesView = "DROP VIEW _stages;";
        String buildsView = "CREATE VIEW _builds AS\n" +
                "SELECT b.*,\n" +
                "  p.id pipelineId, p.name pipelineName, p.label pipelineLabel, p.counter pipelineCounter,\n" +
                "  s.name stageName, s.counter stageCounter, s.fetchMaterials, s.cleanWorkingDir, s.rerunOfCounter, s.artifactsDeleted\n" +
                "FROM builds b\n" +
                "  INNER JOIN stages s ON s.id = b.stageId\n" +
                "  INNER JOIN pipelines p ON p.id = s.pipelineId;";
        String stagesView = "CREATE VIEW _stages AS\n" +
                "SELECT s.*,\n" +
                "  p.name pipelineName, p.buildCauseType, p.buildCauseBy, p.label pipelineLabel, p.buildCauseMessage, p.counter pipelineCounter, p.locked, p.naturalOrder\n" +
                "FROM stages s\n" +
                "  INNER JOIN pipelines p ON p.id = s.pipelineId;";
        IOUtils.writeLines(Arrays.asList(dropBuildsView, dropStagesView, stagesView, buildsView), ";\n", os);
    }


    private static void migrateToCSV(String table, Connection h2c, File outFile) throws SQLException, IOException {
        new Csv().write(h2c, outFile.getAbsolutePath(), "Select * from " + table, "UTF-8");
    }

    private BasicDataSource connectToH2Db(String h2DbName) throws Exception {
        String connectionString = String.format("jdbc:h2:%s;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", h2DbName);
        System.out.println("Connecting to H2 DB at : " + connectionString);
        return new H2Database().createDataSource(connectionString);
    }
}
