# CoverallsIO Maven Plugin

> The Coveralls.io Maven Plugin supports aggregate execution during a non-aggregate build.

[![Build Status](https://github.com/safris/coverallsio-maven-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/safris/coverallsio-maven-plugin/actions/workflows/build.yml)
[![Coverage Status](https://coveralls.io/repos/github/safris/coverallsio-maven-plugin/badge.svg)](https://coveralls.io/github/safris/coverallsio-maven-plugin)
[![Javadocs](https://www.javadoc.io/badge/org.safris.maven/coverallsio-maven-plugin.svg)](https://www.javadoc.io/doc/org.safris.maven/coverallsio-maven-plugin)
[![Released Version](https://img.shields.io/maven-central/v/org.safris.maven/coverallsio-maven-plugin.svg)](https://mvnrepository.com/artifact/org.safris.maven/coverallsio-maven-plugin)

The Coveralls.io Maven Plugin supports aggregate execution during a non-aggregate build.

### Goals Overview

The CoverallsIO Plugin supports two goals.

* `javadoc:report` Analyzes and submits the coverage report to coveralls.io.

#### Configuration Parameters

The `coverallsio-maven-plugin` supports all of the same configuration parameters as the [`coveralls-maven-plugin`](https://github.com/trautonen/coveralls-maven-plugin/), and provides the following additional parameters:

| **Configuration**              | **Property**               | **Type** | **Use**  | **Description**                                                                                                                                   |
|:-------------------------------|:---------------------------|:---------|:---------|:--------------------------------------------------------------------------------------------------------------------------------------------------|
| `<detectGeneratedSourcePaths>` | detectGeneratedSourcePaths | boolean  | Optional | If `true`, the plugin will detect and include the generated source paths from all subpaths of `target/generated-sources`<br>**Default:** `false`. |

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.