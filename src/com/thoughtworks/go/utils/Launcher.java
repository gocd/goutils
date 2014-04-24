/*************************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************GO-LICENSE-END***********************************/

package com.thoughtworks.go.utils;

public class Launcher {

    public static void main(String[] args) {
        String message = "Go Utilities\n" +
                "--------------\n\n" +
                "Usage: java -cp goutils.jar [options]\n\n" +
                "options-\n" +
                "com.thoughtworks.go.utils.primer.H2Primer\t\t\t\tMigrate Go's H2 database up to a schema version.\n" +
                "com.thoughtworks.go.utils.export.tablebased.H2ToTableBasedSql\t\tExport data from Go's H2 database.\n";
        System.out.println(message);

    }
}
