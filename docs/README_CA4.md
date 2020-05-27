# Compiler Assignment 4
Group D: Herold, Jakobitsch, Lercher

## Project Overview
Most of the project's structure is defined by the assignment handouts. The YAPL compiler is located at `yapl/compiler/Yapl.jjt`. We used JJTree to have more detailed information about the nodes, especially token location and type information with a modified `ASTLiteral`. The Ant build file is located in the root directory of the project and extended to compile JJTree-related classes. Needed libraries are stored in the lib folder.

## Implementation
We did not use the provided `Attrib` and `CodeGen` interfaces but implemented our own type hierarchy by using inheritance. We extended the `Yapl.jj` file from assignment 3 to use JJTree and check the required semantic rules for the YAPL type system.
Unfortunately, one of the old test files (parser/test13) still does not pass, as it uses an array length operator directly on the return value of a procedure. This is currently not allowed regarding the semantic rules in 3.2.

## Ant
The ant build file is located at `./build.xml`.

### Mentionable Targets

| Ant target       | Description                                                 | Parameters to overwrite                                      | Notes                    |
|------------------|-------------------------------------------------------------|--------------------------------------------------------------|--------------------------|
| `clean`          | Removes all generated output                                |                                                              |                          |
| `compile-jjtree` | Creates the JJTree classes and the Yapl.jj from Yapl.jjt    | javacc (path to the folder containing the JavaCC zip or jar) |                          |
| `compile-java`   | Creates Java class files based on the Yapl.jj               | javacc (path to the folder containing the JavaCC zip or jar) | default                  |
| `run`            | Runs the parser for a given YAPL file                       | yapl (path to the file)                                      | depends `compile-java`   |
| `run-scanner`    | Runs the scanner for a given YAPL file                      | yapl (path to the file)                                      | depends `compile-java`   |

Notes: <br>
We used the `eval-all` target with the `version` parameter set to `typecheck` to test our code with the new test files.
