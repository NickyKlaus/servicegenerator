# All you need is POM

Another part of generation microservices with **Origami** is plugin configuration. All the configuration preferences must be described in plugin configuration section in the project descriptor <code>pom.xml</code>.

Let's add **Origami** plugin to <code>plugins</code> section in our project <code>pom.xml</code>:

```xml linenums="1"
<plugin>
    <groupId>com.home</groupId>
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

There are two main parts of the plugin configuration: *common preferencies* and *transformations*.
*Common preferencies* section has not its own XML tag and includes following configuration items:

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
| `projectOutputDirectory`   | ${project.build.directory}/generated-sources/swagger               |
| `sourcesDirectory`         | /src/main/java                                                     |
| `basePackage`              | ${project.groupId}.${project.artifactId}                           |
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
| `sourceDirectory`          | ${project.build.directory}/generated-sources/swagger/src/main/java |
| `sourceClassPackage`       | io.swagger.configuration                                           |
| `sourceClassName`          | SwaggerDocumentationConfig                                         |
| `targetClassPackage`       | io.swagger.configuration                                           |
| `targetClassName`          | Swagger2SpringBoot                                                 |
| `targetDirectory`          | ${project.build.directory}/generated-sources/swagger/src/main/java |
| `processingSchemaLocation` | ${project.basedir}/src/main/resources                              |
| `processingSchemaClass`    | SchemaClassName                                                    |
| `transformationProperties` | N/A                                                                |
| `dependencies`             | N/A                                                                |

`transformationProperties` contains key and value pairs that can be used in schema (you can get a property value by its name using Context object).