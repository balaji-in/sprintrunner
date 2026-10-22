# SprintRunner

GitHub project to demo the capabilities of DPR

This repo enables the end to end demo using Jira, ServiceNow and GitHub Repository and Actions.

## Quality demo

Both Maven workflows are started manually from the GitHub Actions page. They compile a small
sprint-planning feature, publish its Maven-generated test results to ServiceNow, and retain the
following evidence as a GitHub Actions artifact:

The sample domain includes backlog tracking, sprint capacity, velocity, burndown, delivery risk,
release planning, team allocation, work prioritization, iteration metrics, and deployment
windows. Each of those ten production classes has a dedicated JUnit class with ten test methods.
Together with the original planner and greeting tests, the current workflow executes 106 unit
tests, one functional test, and one performance test.

- JUnit XML unit-test results in `target/unit-test-reports`
- JUnit XML functional smoke-test results in `target/functional-test-reports`
- JUnit XML load-test results in `target/performance-test-reports`
- JaCoCo HTML, XML, and CSV coverage reports in `target/site/jacoco`

The official ServiceNow DevOps Test Report action reads these Maven report directories directly;
the workflows do not create intermediate ServiceNow payloads. The official ServiceNow Sonar
action publishes software-quality results when an onboarded SonarQube or SonarCloud tool and its
repository variables are configured.

Publishing requires a configured GitHub orchestration tool and the `INSTANCE_URL`, `TOOL_ID`, and
`DEVOPS_INTEGRATION_TOKEN` GitHub Actions secrets.
