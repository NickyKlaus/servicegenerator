# origami

[![Publish package](https://github.com/NickyKlaus/origami/actions/workflows/maven.yml/badge.svg?branch=plugin-dev&event=push)](https://github.com/NickyKlaus/origami/actions/workflows/maven.yml)

## About

**Origami** is a Maven plugin which allows of generation Java RESTful microservices using its definition in OpenAPI (Swagger) Specification format from standalone JSON file or being produced by any remote service description provider.
   
**This plugin is experimental and should not be used in production.**

For more information please see [documentation](https://nickyklaus.github.io/origami/).

## Plugin configuration

All the configuration preferences must be described in plugin configuration section in the project descriptor <code>pom.xml</code>.

```xml linenums="1"
<plugin>
    <groupId>com.github</groupId>
    <artifactId>origami-maven-plugin</artifactId>
    <version>${origami-plugin.version}</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
                <goals>
                <goal>generate-service</goal>
            </goals>
        </execution>
        <configuration>
            <!-- configuration should be here -->
        </configuration>
    </executions>
</plugin>
```

There are two main parts of the plugin configuration: *common preferences* and *transformations*.
*Common preferences* section has not its own XML tag and includes following configuration items:

```xml linenums="1"
<configuration>
    <projectOutputDirectory>${project.build.directory}/generated-sources/swagger</projectOutputDirectory>
    <sourcesDirectory>/src/main/java</sourcesDirectory>
    <basePackage>${project.groupId}.${project.artifactId}</basePackage>
    <controllerPackage>api</controllerPackage>
    <modelPackage>model</modelPackage>
    <configurationPackage>invoker</configurationPackage>
    <dbType>mongo</dbType>
    <transformations>
        ...
    </transformations>
</configuration>
```
with following default values (might be ignored):

| XML tag                    | Default value                                                      |
| :------------------------: | :----------------------------------------------------------------  |
| `projectOutputDirectory`   | [project.build.directory]/generated-sources/swagger               |
| `sourcesDirectory`         | /src/main/java                                                     |
| `basePackage`              | [project.groupId].[project.artifactId]                           |
| `controllerPackage`        | api                                                                |
| `modelPackage`             | model                                                              |
| `configurationPackage`     | invoker                                                            |
| `dbType`                   | --                                                                 |
| `transformations`          | N/A                                                                | 

*Transformations* section structure:

```xml linenums="1"
<configuration>
    ...
    <transformations>
        <transformation>
            <sourceDirectory>${project.build.directory}/generated-sources/swagger/src/main/java</sourceDirectory>
            <sourceClassPackage>io.swagger.configuration</sourceClassPackage>
            <sourceClassName>SwaggerDocumentationConfig</sourceClassName>
            <targetClassPackage>io.swagger.configuration</targetClassPackage>
            <targetClassName>Swagger2SpringBoot</targetClassName>
            <targetDirectory>${project.build.directory}/generated-sources/swagger/src/main/java</targetDirectory>
            <processingSchemaLocation>${project.basedir}/src/main/resources</processingSchemaLocation>
            <processingSchemaClass>SchemaClassName</processingSchemaClass>
            <transformationProperties>
                <transformationProperty>
                    <name>propertyName</name>
                    <value>propertyValue</value>
                </transformationProperty>
                <!-- another transformation properties -->
            </transformationProperties>
            <dependencies>
                <dependency>
                    <groupId>...</groupId>
                    <artifactId>...</artifactId>
                    <version>...</version>
                </dependency>
            </dependencies>
        </transformation>
        <!-- another transformations -->
    </transformations>
</configuration>
```
with following default values (might be ignored):

| XML tag                    | Default value                                                      |
| :------------------------: | :----------------------------------------------------------------  |
| `sourceDirectory`          | [project.build.directory]/generated-sources/swagger/src/main/java |
| `sourceClassPackage`       | io.swagger.configuration                                           |
| `sourceClassName`          | SwaggerDocumentationConfig                                         |
| `targetClassPackage`       | io.swagger.configuration                                           |
| `targetClassName`          | Swagger2SpringBoot                                                 |
| `targetDirectory`          | [project.build.directory]/generated-sources/swagger/src/main/java |
| `processingSchemaLocation` | [project.basedir]/src/main/resources                              |
| `processingSchemaClass`    | SchemaClassName                                                    |
| `transformationProperties` | N/A                                                                |
| `dependencies`             | N/A                                                                |

`transformationProperties` contains key and value pairs that can be used in schema (you can get a property value by its name using Context object).

## Special thanks

- [JavaParser](https://javaparser.org) for their excellent [javaparser](https://github.com/javaparser/javaparser) library
- [Dizitart](https://www.dizitart.org/) for lightweight [Nitrite Database](https://github.com/nitrite/nitrite-java)
- [hekailiang](https://github.com/hekailiang) for so powerful [Squirrel State Machine](http://hekailiang.github.io/squirrel) library 
