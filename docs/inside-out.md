# Origami. Inside out

**Origami** contains a *Finite-state machine* under the hood. It considers all the registered *Processing stages* (of both types *internal* and *external*) as the set of available states and moves from one the another according to the order they were registered.

Basically, you cannot affect to the internal stages' order itself, but you allow to change any line of code produced by internal schemas' processing.

!!! tip ""

    Be sure that you register the schemas in the same order that you expect in the code of each of them.

    In example, a class being generated in schema *A* and being used in schema *B* must be available at the moment when schema *B* will be processed. That means an order for schema registration *A before B*. 