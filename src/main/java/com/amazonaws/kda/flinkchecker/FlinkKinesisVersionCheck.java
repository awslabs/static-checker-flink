package com.amazonaws.kda.flinkchecker;

import org.apache.maven.model.Dependency;

import java.util.Optional;

/**
 * Ensures that the version of the Kinesis connector `flink-connector-kinesis` includes critical fixes
 * required for production readiness.
 * For Flink 1.15: flink-connector-kinesis `1.15.4` is recommended. A warning is issued if this is not the case.
 * flink-connector-kinesis `1.15.4` includes a fix for a critical bug where Flink Kinesis EFO Consumer can fail to stop
 * gracefully (FLINK-31183)
 * See {@link FlinkVersionDependencies} for the recommended Kinesis connectors matrix for all Flink versions
 */
public class FlinkKinesisVersionCheck extends Check {

	@Override
	public CheckResult check(CheckParams params) {
		Dependency flinkDep = params.dependencies.stream().filter(dep -> dep.getGroupId().equals("org.apache.flink") &&
				dep.getArtifactId().startsWith("flink-streaming-java")).findAny().orElse(null);

		CheckResult result = new CheckResult().checkName("Kinesis Connector Version Check").success(true);
		if (flinkDep == null) {
			params.log.debug("No Flink runtime dependency found");
			return result;
		}

		String flinkVersion = flinkDep.getVersion();

		Optional<FlinkVersionDependencies> flinkVersionDeps = FlinkVersionDependencies.from(flinkVersion);

		if (!flinkVersionDeps.isPresent()) {
			return result;
		}

		Dependency requiredDep = flinkVersionDeps.get().getDependency();

		Dependency kinesisDep = params.dependencies.stream()
				.filter(dep -> {
					String artifactId = dep.getArtifactId();
					return artifactId.startsWith(FlinkVersionDependencies.KINESIS_CONNECTOR_ARTIFACT_ID)
							|| artifactId.startsWith(FlinkVersionDependencies.KINESIS_EFO_CONNECTOR_ARTIFACT_ID);
				})
				.findAny()
				.orElse(null);

		if (kinesisDep == null) {
			params.log.debug("No Flink kinesis connector dependency found");
			return result;
		}

		String requiredVersion = requiredDep.getVersion();
		if (!kinesisDep.getGroupId().equals(requiredDep.getGroupId())
				|| !kinesisDep.getArtifactId().equals(requiredDep.getArtifactId())) {
			params.log.warn(String.format("❌ For Flink %s, the recommended Kinesis connector dependency is %s:%s:%s",
					flinkVersion, requiredDep.getGroupId(), requiredDep.getArtifactId(), requiredVersion));
			return result.success(false);
		}

		if (!kinesisDep.getVersion().equals(requiredVersion)) {
			params.log.warn(String.format("❌ For Flink %s, the recommended version for %s:%s is %s or later",
					flinkVersion, requiredDep.getGroupId(), requiredDep.getArtifactId(), requiredVersion));
			return result.success(false);
		}

		params.log.info("✅ Flink Kinesis connector version check passed.");
		return result;
	}
}
