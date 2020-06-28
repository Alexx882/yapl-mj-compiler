# Compiler Assignment 5
Group D: Herold, Jakobitsch, Lercher

## Implementation Overview
`yapl.impl.CodeGenBinSM` has been introduced (as suggested by the provided interfaces) 
as intermediate between the grammar productions in `/src/yapl/compiler/Yapl.jjt`
and the low-level bytecode generation in `yapl.impl.BackendMJ` (from CA1).  

## Usage
```shell
java Yapl [src_path] -o out_path
```

Where `src_path` points to a yapl source file,
and `out_path` to the compiled binary MJ executable.  
If no `src_path` is provided, input will be read from standard input.

## Ant
The ant build file is located at `/build.xml`.

### Mentionable Targets

| Ant target       | Description                                                  | Parameters to overwrite                                                                 | Notes                          |
|------------------|--------------------------------------------------------------|-----------------------------------------------------------------------------------------|--------------------------------|
| `clean`          | Removes all generated files                                  |                                                                                         |                                |
| `init`           | Creates build directory                                      |                                                                                         |                                |
| `compile-java`   | Runs jjTree and JavaCC and compiles the resulting Java files | javacc (path to the folder containing the JavaCC zip or jar)                            | default target, depends `init` |
| `run`            | Runs the compiler for a given YAPL file                      | yapl (.yapl source path), outfile (compiled .mj path), log (compiler message .log path) | depends `compile-java`         |
| `exec`           | Runs the compiled bytecode using the MJVM                    | mj-jar (mjvm.jar path), execlog (runtime output log path)                               | depends `run`                  |

### Notes
We used the provided target `eval-all-codegen` to test our code with the new provided test files,
as well as `eval-all` for the previous tests.
