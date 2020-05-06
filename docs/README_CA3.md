# Compiler Assignment 3
Group D: Herold, Jakobitsch, Lercher

## Project Overview
Most of the project's structure is defined by the assignment handouts. The YAPL compiler is located at `yapl/compiler/Yapl.jj`. The Ant build file is located in the root directory of the project. Needed libraries are stored in the lib folder.

## Implementation
We use the provided interfaces only partially as we needed more parameters for error handling. We extended the `Yapl.jj` file from assignment 2 to also check the required semantic rules.
Unfortunately, one of the old test files (test13) does not pass any longer, as it uses an array length operator directly on the return value of a procedure. This is currently not allowed regarding the semantic rules in 3.2.

## Ant
The ant build file is located at `./build.xml`.

### Mentionable Targets

| Ant target       | Description                                                 | Parameters to overwrite                                      | Notes                    |
|------------------|-------------------------------------------------------------|--------------------------------------------------------------|--------------------------|
| `clean`          | Removes all generated output                                |                                                              |                          |
| `compile-java`   | Creates Java source and class files from the main jj file   | javacc (path to the folder containing the JavaCC zip or jar) | default                  |
| `run`            | Runs the parser for a given YAPL file                       | yapl (path to the file)                                      | depends `compile-java`   |
| `run-scanner`    | Runs the scanner for a given YAPL file                      | yapl (path to the file)                                      | depends `compile-java`   |
| `compile-simple` | Creates Java source and class files from our simple scanner | javacc (path to the folder containing the JavaCC zip or jar) |                          |
| `run-simple`     | Runs the simple parser with some input                      | simple-input (the input, eg. "abc")                          | depends `compile-simple` |

Notes: <br>
We used the `eval-all` target with the `version` parameter set to `symbolcheck` to test our code with the new test files.

