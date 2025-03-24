# Spring Qualifier Demo

## Overview
This repository demonstrates a subtle issue with Spring's `@Qualifier` annotation behavior in parent-child application context hierarchies. The project shows how qualifier resolution can unexpectedly differ between standard contexts and hierarchical contexts.

### Resolution Order
When Spring attempts to resolve dependencies via autowiring, it follows a specific order of resolution strategies.

1. **Exact Qualifier Match**: Spring first attempts to find a bean that matches the specified `@Qualifier` value exactly
2. **Bean Name Match**: If no qualifier is specified, Spring looks for a bean with the same name as the field/parameter
3. **Type Match with @Primary**: If multiple beans of the required type exist, Spring will select the one marked with `@Primary`
4. **Type Match by Name**: Spring will try to match by bean type and use the field/variable name to disambiguate
5. **Any Type Match**: If only one bean of the required type exists, Spring will use it

## The Problem

### Expected Behavior
In a standard Spring context, when using `@Qualifier`, the qualifier value takes precedence over the field name. For example:

```java
@Autowired
@Qualifier("leftBean")  // This should determine which bean gets injected
TestInterface rightField;  // Field name should not affect resolution
```

Spring should inject the bean named "leftBean" regardless of the field being named "rightField".

### Actual Behavior in Parent-Child Contexts
When using a parent-child context hierarchy, where beans are defined in the parent context and injected into beans in the child context, the qualifier resolution may not work as expected. In some cases, the field name seems to influence which bean gets injected, despite the presence of a `@Qualifier` annotation.

### Demonstration Code
The issue can be reproduced with this simplified test:

```java
// In parent context
@Bean({"open.left", "left"})
public TestInterface left() {
    return () -> true;  // Returns true
}

@Bean({"open.right", "right"})
public TestInterface right() {
    return () -> false;  // Returns false
}

// In child context
private static class TestClass {
    @Autowired
    @Qualifier("open.left")  // Should inject bean that returns true
    TestInterface right;  // Field name suggests "right" bean
    
    public boolean doit() {
        return right.doItRight();
    }
}

// Test method
@Test
public void testInterface() {
    AnnotationConfigApplicationContext subCtx = new AnnotationConfigApplicationContext();
    subCtx.setParent(context);
    subCtx.register(SubConfiguration.class);
    subCtx.refresh();
    
    // This assertion may fail - returns false instead of true
    Assertions.assertTrue(subCtx.getBean(TestClass.class).doit());
}
```

## Project Structure

This project contains:

- `src/main/java/com/example/qualifier/demo/DemoApplication.java` - Spring Boot application entry point
- `src/test/java/com/example/qualifier/demo/` - Test cases demonstrating the issue:
    - `SingleContextTest.java` - Shows correct qualifier behavior in a single context
    - `ParentChildContextTest.java` - Demonstrates the issue in parent-child hierarchy
    - `ParentChildContextSolutionTest.java` - Shows potential workarounds

## Requirements
- Java 11 or higher
- Maven 3.6 or higher

## Running the Tests

To run the tests and see the different behaviors:

```bash
./mvnw test
```