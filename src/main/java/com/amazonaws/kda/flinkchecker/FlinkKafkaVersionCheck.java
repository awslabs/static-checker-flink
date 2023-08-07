package com.amazonaws.kda.flinkchecker;

import org.apache.maven.model.Dependency;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This ensures that the version of `kafka-clients` being used is compatible with the version Flink.
 * For Flink 1.15: kafka-clients version `3.x` is recommended. A warning is issued if this is not the case.
 * For Flink 1.13: kafka-clients version `2.x` is recommended. A warning is issued if this is not the case.
 * Other Flink versions are not yet covered.
 */
public class FlinkKafkaVersionCheck extends Check {

    private static final String CHECK_NAME = "Flink - Kafka Client Version Check";

    @Override
    public CheckResult check(CheckParams params) {
        // assuming there is 1 flink-core in dependencies, get the first one's version
        Optional<Dependency> flinkCore = params.dependencies.stream().filter(dep -> dep.getGroupId().equals("org.apache.flink") &&
                dep.getArtifactId().equals("flink-streaming-java")).findFirst();
        CheckResult result = new CheckResult().checkName(CHECK_NAME);
        if (! flinkCore.isPresent()) {
            params.log.debug("No flink-streaming-java dependency found");
            return result.success(true);
        }
        String flinkVer = flinkCore.get().getVersion();

        List<Dependency> kafkaClis = params.dependencies.stream().filter(dep -> dep.getGroupId().equals("org.apache.kafka") &&
                dep.getArtifactId().equals("kafka-clients")).collect(Collectors.toList());

        if (kafkaClis.size() == 0) {
            return result.success(true);
        }
        String requiredKafkaCliVersionPrefix = "";
        if (flinkVer.startsWith("1.13")) {
            requiredKafkaCliVersionPrefix = "2";
        }
        if (flinkVer.startsWith("1.15")) {
            requiredKafkaCliVersionPrefix = "3";
        }

        boolean success = false;
        for (Dependency kafkaCli : kafkaClis) {
            if (kafkaCli.getVersion().startsWith(requiredKafkaCliVersionPrefix)) {
                success = true;
                break;
            }
        }
        CheckResult checkResult = new CheckResult().checkMessage(CHECK_NAME).success(success);
        if (! success) {
            String msg = String.format("❌ The recommended version for org.apache.kafka:kafka-clients for Flink %s is %s",
            flinkVer, String.format("%s.x.y", requiredKafkaCliVersionPrefix));
            params.log.warn(msg);
            return checkResult.checkMessage(msg);
        }

        params.log.info("kafka-clients check pass");
        return checkResult;
    }
}
