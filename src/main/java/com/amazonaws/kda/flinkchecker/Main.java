package com.amazonaws.kda.flinkchecker;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY)
public class Main extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(defaultValue = "LENIENT", property = "mode")
    Mode mode;

    List<Check> checks;
    {
        checks = new ArrayList<>();
        checks.add(new MskIamVersionCheck());
        checks.add(new FlinkKinesisVersionCheck());
        checks.add(new FlinkKafkaVersionCheck());
    }

    @Override
    public void execute() throws MojoFailureException {
        boolean allCheckSucceed = true;

        List<Dependency> dependencies = project.getDependencies();
        CheckParams params = new CheckParams(getLog(), project, dependencies);
        JUnitReporter reporter = new JUnitReporter(params);
        for (Check check : checks) {
            CheckResult result = check.check(params);
            reporter.appendResult(result);

            allCheckSucceed &= result.success;
        }

        try {
            Files.createDirectory(Paths.get("target/"));
        } catch (IOException e) {
        }
        reporter.writeResultsToPath(Paths.get("target/flink-readiness-checker-report.xml"));

        if (Mode.STRICT.equals(mode) && ! allCheckSucceed) {
            throw new MojoFailureException("Failing build because of check failures. Please check logs or report.");
        }
    }
}
