[![Test2](https://github.com/cscenter/kotlin-test-2/actions/workflows/Test2.yml/badge.svg)](https://github.com/cscenter/kotlin-test-2/actions/workflows/Test2.yml)

### Test #2

In this test you need to complete one task:
- [Task#1](./task1/README.md)

See the provided links for more details.

**If you have two red builds, you will receive half of the available points.**

The project has tests that must be fully passed before the assignment is submitted. Additionally, the project has style checks configured by Detekt and Diktat that must be passed (using the Suppress annotation is prohibited).

To run the tests you could use predefined run configurations or run them manually:

* To run all tests locally, you can use:`./gradlew test`;

* To run task #1 tests locally, you can use:`./gradlew :task1:test`;

* To run Detekt locally, you can use: `./gradlew detektCheckAll`;

* To run Diktat locally, you can use: `./gradlew diktatCheckAll`.
