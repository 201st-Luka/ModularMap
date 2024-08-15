# Coding guidelines

---

## Naming conventions

### Directory names

- Use `SnakeCase` for directory names.
- Use a singular noun or a compound noun for directory names.
- If a directory structure requires multiple words, create subdirectories.

### Class names

- Use `PascalCase` for class names.
- Interface names should start with an `I` followed by a `PascalCase` class name.
- Abstract class names should start with an `Abstract` followed by a `PascalCase` class name.

#### Class field names

- Use `CamelCase` for class field names.
- Field names should start with an underscore.
- Field names should give a clear indication of the field's purpose and of the object type it holds.

### Method names

- Use `CamelCase` for method names.
- Method names should be a verb or a verb phrase that describes the method's action.
- Getter methods should start with `get`, unless they are boolean, in which case they should start with `is`.

#### Method parameter names

- Use `CamelCase` for method parameter names.
- Parameter names should give a clear indication of the parameter's purpose and of the object type it holds.

### Variable names

- Use `CamelCase` for variable names.
- Use `UPPER_SNAKE_CASE` for static constants.
- Variable names should give a clear indication of the variable's purpose and of the object type it holds.

---

## Code style

### Indentation

- Use 4 spaces for indentation.
- Do not use tabs for indentation.
- Use blank lines to separate blocks of code.

### Parentheses, ...

- Curly brackets should be on the same line as the statement they belong to.
- Brackets should contain a single statement, unless the statement is a boolean or arithmetic expression.
- Avoid using curly brackets for single-line statements.

### Lines of code

- Keep lines of code under 120 characters.
- Do not chain more than 2 method calls in a line.
- Do not nest more than 2 levels deep.

### Classes

- Apply the single responsibility principle.
- Keep classes under 200 lines, if they are longer it is likely that they do not have a single responsibility or can
  be broken down into smaller classes.
- Do not use public fields, use properties instead (except for constants or the classes in the
  `luka.modularmap.client.config` package).
- Do not use static classes, use singletons instead (except for classes in the `luka.modularmap.client.event` package).
- Do not use class nesting, use composition instead.
- The order of class members should be as follows:
    1. Constants
    2. Fields

       Use fields without the `this` keyword, unless it is not possible.
    3. Constructors
    4. Properties
    5. Methods (private, protected, public in that order)

       Methods that override a parent method should be placed at the end.

### Methods

- Apply the single responsibility principle.
- Keep methods under 20 lines, if they are longer it is likely that they do not have a single responsibility or can
  be broken down into smaller methods.
- Create separate methods for separate logic.
- Do not use static methods, use instance methods instead.
- Use `@Override` annotation when overriding a parent method.
- Apply defensive programming.
- Apply the DRY (Don't Repeated Yourself) principle.

### Variables

- Apply the DRY (Don't Repeated Yourself) principle.

### Annotations

- Use annotations to provide metadata about the code (e.g. `@Override`, `@Nullable`, `@NotNull`).

---

## Naming patterns

### Classes

- Use `*Service` for classes that provide a service.
- Use `*Repository` for classes that provide access to a data source.
- Use `*Factory` for classes that create objects.
- Use `*Builder` for classes that build objects.
- Use `*Mapper` for classes that map objects.
- Use `*Validator` for classes that validate objects.
- Use `*Converter` for classes that convert objects.
- Use `*Handler` for classes that handle events.
- Use `*Listener` for classes that listen for events.
- Use `*Manager` for classes that manage objects.
- Use `*Controller` for classes that control the flow of the application.

---

## Applied principles

- Single responsibility principle
- Don't repeated yourself principle
- Defensive programming
