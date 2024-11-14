# Mizool

[![mizool Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mizool/mizool/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.mizool/mizool/)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.mizool%3Amizool&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.mizool%3Amizool)

[![javadoc](https://javadoc.io/badge2/com.github.mizool/mizool-core/javadoc.svg)](https://javadoc.io/doc/com.github.mizool/mizool-core)

## Updating dependencies

1. Determine applicable dependency updates using the following commands:
    1. `mvn versions:display-plugin-updates` for plugins
    2. `mvn versions:display-dependency-updates`
2. Before bumping anything, if "develop" has any unreleased changes, create a new Mizool release to get those out first.
3. Perform the following steps in order, creating a separate new Mizool release for each step:
    1. all minor version changes of dependencies ➜ one new *minor* Mizool version
    2. all major version changes of dependencies ➜ one new *major* Mizool version
    3. if a Java upgrade for Mizool was discussed and agreed by the team and is scheduled for this month, perform the
       upgrade ➜ new *major* Mizool version

Note: Only the newest Mizool release is maintained this way. This means once a Mizool X.0 was released, anything < X
will not receive minor/major dependency updates or Java LTS updates anymore.
