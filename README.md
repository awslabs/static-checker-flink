### Why this project exists

The goal of this project is to catch certain issues with Apache Flink applications fast (during build/packaging).

Covered cases:

- Kinesis connector compatibility issues
- Kafka connector compatibility issues
- MSK IAM Auth library issues

For instance did you know that you have to use AWS Kinesis Connector 1.15.4 or above for Flink 1.15 apps? This plugin
is there to stop you from building an app that has such incompatible connector versions.

## What this project is

Maven plugin to find issues with Apache Flink applications at build time.

### Example usage:

```
# Check out this project and install it locally
cd ${PATH_TO_THIS_STATIC_CHECKER_PROJECT}
mvn clean install


# Go to your Apache Flink (Maven) project and run it
cd ${PATH_TO_FLINK_PROJECT}
mvn software.amazon.kinesis:static-checker-flink:0.0.1-SNAPSHOT:check
```

Checker results should also appear in `target/` folder in jUnit format, so you can integrate it with a CI tool.

### How to include in project

Include plugin in `pom.xml`
```
...
<build>
    <plugins>
        <plugin>
            <groupId>software.amazon.kinesis</groupId>
            <artifactId>static-checker-flink</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Verify project
```
mvn clean verify
```

### Generate javadocs:

```
mvn javadoc:javadoc
```

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This project is licensed under the Apache-2.0 License.

