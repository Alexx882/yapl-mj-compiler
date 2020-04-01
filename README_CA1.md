# Compiler Assignment 1
Group D: Herold, Jakobitsch, Lercher

## Ant
The ant build file is located at `/src/yapl/test/backend/sm/build-dist.xml`.

### Targets

| Ant target     | Description                                     | Parameters to overwrite                                                                        | Notes                  |
|----------------|-------------------------------------------------|------------------------------------------------------------------------------------------------|------------------------|
| `run-backend`  | generate MJ bytecode                            | mainclass/testname<br>(which java file to run)<br>outfile<br>(where to write the bytecode)     |                        |
| `run-mj`       | run MJ code with mjvm                           | outfile<br>(which bytecode file to run)                                                        | depends `run-backend`  |
| `eval`         | run a single test file<br>(generate mj and run) | mainclass/testname<br>(which java file to run)<br>runtimeoutput<br>(where to write the output) |                        |
| `eval-all`     | run all tests                                   |                                                                                                | depends `eval`         |
| `clean`        | remove generated files                          |                                                                                                |                        |
| `decode`       | decode generated MJ file                        | outfile<br>(which bytecode file to decode)                                                     | prints output          |
| `coverage`     | run coverage for a single file                  | mainclass/testname<br>(which java file to run)                                                 | uses JaCoCo            |
| `coverage-all` | run coverage for all tests                      |                                                                                                | depends `coverage`     |
| `cov-report`   | create full coverage report                     |                                                                                                | depends `coverage-all` |

Note: `mainclass` accepts a full classpath, while `testname` just requires the name of the test class under `yapl.test.backend.sm`.

## Project Files
With the exception of tests, all new code is in `yapl.impl`.

- `BackendMJ` implements BackendBinSM
- `ByteUtils` helper methods for byte manipulation
- `Instruction` enum of MJ opcodes with byte values
- `OperandType` enum of explicit MJ operand types (s8, s16, s32)
- `Procedure` represents a defined procedure. keeps track of allocated local variables

Tests are located in `yapl.test.backend.sm`.

## Coverage
Reported coverage for `yapl.impl`: 100.0%

### Method used
Generating coverage report for `yapl.impl` over all tests using Java Code Coverage (JaCoCo). See ant target `cov-report`.
