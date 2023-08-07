package com.amazonaws.kda.flinkchecker;

import org.apache.maven.model.Dependency;

/**
 * Ensures that the version of the Kinesis connector `flink-connector-kinesis` includes critical fixes
 * required for production readiness.
 * For Flink 1.15: flink-connector-kinesis `1.15.4` is recommended. A warning is issued if this is not the case.
 * flink-connector-kinesis `1.15.4` includes a fix for a critical bug where Flink Kinesis EFO Consumer can fail to stop gracefully (FLINK-31183)
 * Other Flink versions are not yet covered.
 */
public class FlinkKinesisVersionCheck extends Check {

	@Override
	public CheckResult check(CheckParams params) {
		Dependency flinkDep = params.dependencies.stream().filter(dep -> dep.getGroupId().equals("org.apache.flink") &&
				dep.getArtifactId().equals("flink-streaming-java")).findAny().orElse(null);

		CheckResult result = new CheckResult().checkName("Kinesis Connector Version Check").success(true);
		if (flinkDep == null) {
			params.log.debug("No Flink runtime dependency found");
			return result;
		}

		String flinkVersion = flinkDep.getVersion();

		Dependency kinesisDep = params.dependencies.stream().filter(dep -> dep.getGroupId().equals("org.apache.flink") &&
				dep.getArtifactId().equals("flink-connector-kinesis")).findAny().orElse(null);

		if (kinesisDep == null) {
			params.log.debug("No Flink kinesis connector dependency found");
			return result;
		}

		if (flinkVersion.startsWith("1.15")) {
			String requiredKinesisConnectorVersion = "1.15.4";
			if (kinesisDep.getVersion().equals(requiredKinesisConnectorVersion)) {
				params.log.info("✅ Flink Kinesis connector version check passed.");
				return result;
			}

			params.log.warn(String.format("❌ For Flink %s, the recommended version for org.apache.flink:flink-connector-kinesis is %s or later",
					flinkVersion, requiredKinesisConnectorVersion));
			return result.success(false);
		}

		if (flinkVersion.startsWith("1.13")) {
			return result;
		}

		if (flinkVersion.startsWith("1.11")) {
			return result;
		}

		if (flinkVersion.startsWith("1.8")) {
			return result;
		}

		if (flinkVersion.startsWith("1.6")) {
			return result;
		}

		return result;
	}
}
