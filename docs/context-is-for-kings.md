# Context is for Kings

*Processing Schemas* can use any data contained in [*Context*](https://github.com/NickyKlaus/origami-api/blob/master/src/main/java/com/home/origami/api/context/Context.java) in a type-safe way:
```
context.get([property_name], [value_class])
```
For example, in the schema below we request some data from *Processing Context*:
```java hl_lines="5 6 7" linenums="1"
public class SomeProcessingSchema implements ASTProcessingSchema {
    @Override
    public BiFunction<CompilationUnit, Context, CompilationUnit> preProcessCompilationUnit() {
        return (CompilationUnit n, Context context) -> {
            var model = context.get(PIPELINE_ID.name(), Name.class);
            var dbType = context.get(DB_TYPE.name(), Storage.DbType.class);
            var varFromTransformation = context.get("varFromTransformation", String.class);

            // some code
            
            return n;
        };
    }    
}
```
Of course, the schema must know about the type of the data contained in context.
It means that you should add such classes as `Name.class`, `Storage.class` to classpath of **Origami** to process `SomeProcessingSchema`. 

Note that property `varFromTransformation` was initialized in `<transformationProperty>` section in `pom.xml`:
```xml hl_lines="9 10" linenums="1"
<configuration>
    ...
    <transformations>
        <transformation>
            ...
            <processingSchemaClass>SomeProcessingSchema</processingSchemaClass>
            <transformationProperties>
                <transformationProperty>
                    <name>varFromTransformation</name>
                    <value>HelloFromTransformation</value>
                </transformationProperty>
                <!-- another transformation properties -->
            </transformationProperties>
        </transformation>
        <!-- another transformations -->
    </transformations>
</configuration>
```