# Walking through the jungle

Every *Processing schema* class we made can be imagined as a set of actions with the AST nodes. We can walk from one source code file to another, look through its AST structure and change the nodes according to the 'algorithm' we get from predefined *processing schema*.

There are *pre-* and *post-* process actions which can be applied to the currently processed node one-by-one:
``` mermaid
graph LR
  A[<b>Node</b> v1] --> |<i>preProcess</i><b>NodeType</b>| B[<b>Node</b> v2];
  B --> |<i>postProcess</i><b>NodeType</b>| C[<b>Node</b> v3];
```
As you can see we can deal with up to 3 different versions of one node during processing the source code and the resulted version of node will be saved to the target `.class` file.

Of course, you can add any operations to your schema action (e.g. logging) or even keep the node's state effectively immutable.

Let's take a look to the example of *Processing schema* declaration:

```java linenums="1"
public class SomeProcessingSchema implements ASTProcessingSchema {
	@Override
    public BiFunction<CompilationUnit, Context, CompilationUnit> preProcessCompilationUnit() {
        return (CompilationUnit n, Context context) -> {
            // some code
            return n;
        };
    }

    @Override
    public BiFunction<CompilationUnit, Context, CompilationUnit> preProcessClassOrInterfaceDeclaration() {
        return (ClassOrInterfaceDeclaration n, Context context) -> {
            // some code
            return n;
        };
    }

    @Override
    public BiFunction<ClassOrInterfaceDeclaration, Context, ClassOrInterfaceDeclaration> postProcessClassOrInterfaceDeclaration() {
        return (ClassOrInterfaceDeclaration n, Context context) -> {
            // some code
            return n;
        };
    }
}
```

All the operations with the node `n` must be declared in corresponding `BiFunction` body. Plugin expects that you will return the same node object that you got as a result of every processing action.

According to the *"Convention over configuration"* principle you should declare only the actions you really need to do during the scheme processing. 