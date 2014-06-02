# Go utilities

### H2 Database primer

This is used to prime/migrate an instance of H2 database to bring it up to the schema that you desire. You need to make sure you point the primer to the current Go installation's database script directory.

For e.g., if your

- Go installation is at /var/lib/go-server
- H2 database file is located at /tmp/cruise.h2.db

you can run

```bash
java -cp goutils.jar com.thoughtworks.go.utils.primer.H2Primer /var/lib/go-server/db/h2deltas /tmp/cruise
```

> NOTE: Make sure you provide the H2 database file location without the .h2.db extension


### H2 Database exporter

This is used to export all Go data from an H2 instance.

For e.g., if your

- H2 database file is located at /tmp/cruise.h2.db
- Output directory for the dump is at /tmp/output

you can run

```bash
java -cp goutils.jar com.thoughtworks.go.utils.export.tablebased.H2ToTableBasedSql /tmp/cruise /tmp/output
```

> NOTE: Make sure you provide the H2 database file location without the .h2.db extension

### Go Cipher Text generator

You can use Go's cipher key to convert plain text to cipher text. This can be directly fed into Go Configuration before saving.

For e.g., if your

- Go configuration directory is located at /etc/go, your cipher file will be at /etc/go/cipher

you can run

```bash
java -cp goutils.jar:bcprov-jdk16-140.jar com.thoughtworks.go.utils.encryption.Encrypter /etc/go/cipher
```
