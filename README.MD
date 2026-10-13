# SprintRunner

GitHub project to demo the capabilities of DPR

This repo enables the end to end demo using Jira, ServiceNow and GitHub Repository and Actions.

## Quality demo

Both Maven workflows are started manually from the GitHub Actions page. They compile a small
sprint-planning feature and produce the following evidence without calling ServiceNow,
SonarQube, or a package registry:

- JUnit XML unit-test results in `target/unit-test-reports`
- JUnit XML functional smoke-test results in `target/functional-test-reports`
- JUnit XML load-test results in `target/performance-test-reports`
- JaCoCo HTML, XML, and CSV coverage reports in `target/site/jacoco`
- ServiceNow-shaped test payloads and a Sonar-shaped mock quality response in
  `target/demo-reports`

The workflow publishes these files as a GitHub Actions artifact and renders a quality table in
the workflow run summary. Test counts, timings, and coverage are calculated from the current
run. Vulnerabilities, bugs, reliability, maintainability, code smells, technical debt, and
duplication are stable mock values and are marked as demo data.

Populating the live ServiceNow widgets requires a configured GitHub orchestration tool and test
type mappings for `JUnit`, `Smoke`, and `Load`. The generated JSON is intentionally kept local
until those integrations and credentials are available.
