package com.amazonaws.kda.flinkchecker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.jamesmurty.utils.XMLBuilder2;

public class JUnitReporter {

    List<CheckResult> results;
    CheckParams params;

    public JUnitReporter(CheckParams params) {
        results = new ArrayList<>();
        this.params = params;
    }

    public void appendResult(CheckResult result) {
        results.add(result);
    }

    public void writeResultsToPath(Path path) {
        XMLBuilder2 builder = XMLBuilder2.create("testsuite").a("tests", String.valueOf(results.size()));
        for (CheckResult result : results) {
           builder = builder.e("testcase").a("name", result.checkName);
           if (! result.success) {
               builder = builder.e("failure").a("message", result.checkMessage);
               builder = builder.up();
           }
           builder = builder.up();
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(path.toFile()));
        } catch (FileNotFoundException e) {
            params.log.warn("Failed to write KDA Checker JUnit output to file " + path);
        }
        builder.toWriter(writer, new Properties());
    }
}
