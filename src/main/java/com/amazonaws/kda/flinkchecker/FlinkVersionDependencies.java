package com.amazonaws.kda.flinkchecker;

import org.apache.maven.model.Dependency;

import java.util.Arrays;
import java.util.Optional;

/**
 * <pre>
 * | Flink version | Recommended Kinesis connector                                 |
 * | ------------- | ------------------------------------------------------------- |
 * | 1.15          | org.apache.flink:flink-connector-kinesis:1.15.4               |
 * | 1.13          | org.apache.flink:flink-connector-kinesis_2.12:1.13.6          |
 * | 1.11          | software.amazon.kinesis:amazon-kinesis-connector-flink:2.4.1  |
 * | 1.8           | software.amazon.kinesis:amazon-kinesis-connector-flink:1.6.1  |
 * | 1.6           | org.apache.flink:flink-connector-kinesis_2.11:1.6.4           |
 * </pre>
 */
public enum FlinkVersionDependencies {
	FLINK_1_15("1.15") {
		@Override
		public String getKinesisVersion() { return "1.15.4"; }
	},
	FLINK_1_13("1.13") {
		@Override
		public String getScalaVersion() { return "2.12"; }
		@Override
		public String getKinesisVersion() { return "1.13.6"; }
	},
	FLINK_1_11("1.11") {
		@Override
		public String getGroupId() { return KINESIS_EFO_CONNECTOR_GROUP_ID; };
		@Override
		public String getArtifactId() { return KINESIS_EFO_CONNECTOR_ARTIFACT_ID; };
		@Override
		public String getKinesisVersion() { return "2.4.1"; }
	},
	FLINK_1_8("1.8") {
		@Override
		public String getGroupId() { return KINESIS_EFO_CONNECTOR_GROUP_ID; };
		@Override
		public String getArtifactId() { return KINESIS_EFO_CONNECTOR_ARTIFACT_ID; };
		@Override
		public String getKinesisVersion() { return "1.6.1"; }
	},
	FLINK_1_6("1.6") {
		@Override
		public String getScalaVersion() { return "2.11"; }
		@Override
		public String getKinesisVersion() { return "1.6.4"; }
	};

	private static String KINESIS_CONNECTOR_GROUP_ID = "org.apache.flink";
	public static String KINESIS_CONNECTOR_ARTIFACT_ID = "flink-connector-kinesis";

	private static String KINESIS_EFO_CONNECTOR_GROUP_ID = "software.amazon.kinesis";
	public static String KINESIS_EFO_CONNECTOR_ARTIFACT_ID = "amazon-kinesis-connector-flink";

	private String version;

	public String getGroupId() {
		return KINESIS_CONNECTOR_GROUP_ID;
	};

	public String getScalaVersion() {
		return null;
	};

	public String getArtifactId() {
		String scalaVersion = getScalaVersion();
		return scalaVersion == null
				? KINESIS_CONNECTOR_ARTIFACT_ID : KINESIS_CONNECTOR_ARTIFACT_ID + "_" + scalaVersion;
	};

	abstract public String getKinesisVersion();

	public Dependency getDependency() {
		Dependency dep = new Dependency();
		dep.setGroupId(getGroupId());
		dep.setArtifactId(getArtifactId());
		dep.setVersion(getKinesisVersion());
		return dep;
	}

	FlinkVersionDependencies(String version) {
		this.version = version;
	}

	public static Optional<FlinkVersionDependencies> from(String version) {
		return Arrays.stream(FlinkVersionDependencies.values())
				.filter(versionDeps -> version.startsWith(versionDeps.version)).findAny();
	}
}
