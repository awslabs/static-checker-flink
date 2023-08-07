package com.amazonaws.kda.flinkchecker;

import org.apache.maven.model.Dependency;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This checks if the Flink app jar is using aws-msk-iam-auth version 1.1.4 or less
 * and attempts to warn the user if that is the case. MSK IAM Auth library has a known incompatibility with Flink
 * https://github.com/aws/aws-msk-iam-auth/issues/36
 * which was fixed in version https://github.com/aws/aws-msk-iam-auth/releases/tag/v1.1.5
 */
public class MskIamVersionCheck extends Check {

    private static final String CHECK_NAME = "MSK IAM Library Version Check";

    private CheckResult checkMskIamVersion(CheckParams params) {
        List<Dependency> mskIamDeps = params.dependencies.stream().filter(dep -> dep.getGroupId().equals("software.amazon.msk") &&
                dep.getArtifactId().equals("aws-msk-iam-auth")).collect(Collectors.toList());

        boolean issueFound = false;
        for (Dependency dep : mskIamDeps) {
            String[] majorMinorPatch = dep.getVersion().split("\\.");
            if (majorMinorPatch.length != 3) {
                String msg = "Unknown version format for MSK IAM Library, expected format 'x.y.z' but found " + dep.getVersion();
                params.log.warn(msg);
                return new CheckResult().success(false).checkMessage(msg).checkName(CHECK_NAME);
            }
            int major = Integer.parseInt(majorMinorPatch[0]);
            int minor = Integer.parseInt(majorMinorPatch[1]);
            int patch = Integer.parseInt(majorMinorPatch[2]);
            if (major <= 1 && minor <=1 && patch < 5) {
                issueFound = true;
                break;
            }
        }
        if (issueFound) {
            String msg = "❌ You are using " +
                    "aws-msk-iam-auth < v1.1.5 which has a known issue: " +
                    "https://github.com/aws/aws-msk-iam-auth/issues/36";
            params.log.warn(msg);
            return new CheckResult().success(false).checkMessage(msg).checkName(CHECK_NAME);
        }
        String msg = "✅ MSK IAM check pass!";
        params.log.info(msg);
        return new CheckResult().success(true).checkMessage(msg).checkName(CHECK_NAME);
    }

    @Override
    public CheckResult check(CheckParams params) {
        return checkMskIamVersion(params);
    }
}
