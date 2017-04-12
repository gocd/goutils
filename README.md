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

On Linux/Mac OSX, you can run

```bash
java -cp goutils.jar:bcprov-jdk16-140.jar com.thoughtworks.go.utils.encryption.Encrypter /etc/go/cipher
```

On Windows,

```bash
java -cp goutils.jar;bcprov-jdk16-140.jar com.thoughtworks.go.utils.encryption.Encrypter /etc/go/cipher
```

## License

```plain
Copyright 2017 ThoughtWorks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
