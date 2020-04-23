# Compiler Assignment 2
Group D: Herold, Jakobitsch, Lercher

## Project Overview
Most of the project's structure is defined by the assignment handouts. The simple compiler is located at `yapl/compiler/simple/Grammar21.jj`. The YAPL compiler is located at `yapl/compiler/Yapl.jj`. The Ant build file was renamed to `build.xml` and is located in the root directory of the project. Needed libraries are stored in the lib folder.

## Ant
The ant build file is located at `./build.xml`. It was renamed to match the submission guidelines.

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
`run` requires the name of the YAPL source code file and works according to task 2.3. <br>
`run-scanner` is called like `run` but will only print the tokens according to task 2.2. <br>
`run-simple` checks an input passed by the parameter according to task 2.1.f.

