/*
 * Copyright (c) 2021 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.jdkbutler.tools;


import io.foojay.api.discoclient.pkg.OperatingSystem;
import io.foojay.api.discoclient.pkg.SemVer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class JDKFinder {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(openjdk|java) version \"(.*?)\"");
    private static final boolean IS_WINDOWS      = System.getProperty("os.name").toLowerCase().startsWith("windows");

    public JDKFinder() {
        try {
            SemVer semver = SemVer.fromText(Runtime.version().toString()).getSemVer1();
            System.out.println("Semver: " + semver);

            /*
            // Windows: where java -> C:\Apps\Java\jdk1.8.0_31\bin\java.exe
            // Unix   : which java -> /usr/bin/java
            ProcessBuilder processBuilder = new ProcessBuilder("which", "java").redirectErrorStream(true);
            Process p = processBuilder.start();
            Streamer str = new Streamer(p.getInputStream(), System.out::println);
            service.submit(str);
            */


            List<String>   commands = new ArrayList<>();
            if (IS_WINDOWS) {
                commands.add("java.exe");
                commands.add("-version");
            } else {
                commands.add("java");
                commands.add("-version");
            }
            ProcessBuilder builder = new ProcessBuilder(commands).redirectErrorStream(true);

            Process         process  = builder.start();
            Streamer        streamer = new Streamer(process.getInputStream(), r -> {
                final String            versionText    = r.toLowerCase();
                final Matcher           versionMatcher = VERSION_PATTERN.matcher(r);
                final List<MatchResult> results        = versionMatcher.results().collect(Collectors.toList());

                if (results.isEmpty()) { return; }

                MatchResult result = results.get(0);
                final String distribution;
                if (versionText.contains("zulu")) {
                    distribution = "Zulu";
                } else if (versionText.contains("adoptopenjdk")) {
                    distribution = "Adopt OpenJDK";
                } else if (versionText.contains("dragonwell")) {
                    distribution = "Dragonwell";
                } else if (versionText.contains("corretto")) {
                    distribution = "Corretto";
                } else if (versionText.contains("sapmachine")) {
                    distribution = "SAP Machine";
                } else if (result.group(1).toLowerCase().equals("java")) {
                    distribution = "Oracle";
                } else {
                    distribution = "Build of OpenJDK";
                }

                SemVer semVer = SemVer.fromText(result.group(2)).getSemVer1();
                System.out.println(distribution + " " + semVer);
            });


            ExecutorService service  = Executors.newSingleThreadExecutor();
            service.submit(streamer);
            service.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new JDKFinder();
    }
}
