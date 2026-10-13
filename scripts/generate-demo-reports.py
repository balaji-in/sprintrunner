#!/usr/bin/env python3
"""Create ServiceNow-shaped demo payloads from Maven and JaCoCo reports."""

import csv
import json
import os
import re
from datetime import datetime, timezone
from pathlib import Path
from xml.etree import ElementTree


ROOT = Path(__file__).resolve().parents[1]
TARGET = ROOT / "target"
OUTPUT = TARGET / "demo-reports"


def read_test_report(directory):
    totals = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0, "duration": 0.0}
    cases = []
    output = []
    files = sorted((TARGET / directory).glob("TEST-*.xml"))
    if not files:
        raise RuntimeError("No JUnit XML reports found in {}".format(directory))

    for report_file in files:
        root = ElementTree.parse(str(report_file)).getroot()
        suites = [root] if root.tag == "testsuite" else list(root.findall("testsuite"))
        for suite in suites:
            for field in ("tests", "failures", "errors", "skipped"):
                totals[field] += int(suite.attrib.get(field, 0))
            totals["duration"] += float(suite.attrib.get("time", 0.0))
            for case in suite.findall("testcase"):
                cases.append(float(case.attrib.get("time", 0.0)))
            for stream in suite.findall(".//system-out"):
                output.append(stream.text or "")

    totals["passed"] = (
        totals["tests"] - totals["failures"] - totals["errors"] - totals["skipped"]
    )
    totals["cases"] = cases
    totals["output"] = "\n".join(output)
    return totals


def read_coverage():
    report = TARGET / "site" / "jacoco" / "jacoco.csv"
    if not report.exists():
        raise RuntimeError("JaCoCo CSV report was not generated")

    missed = 0
    covered = 0
    with report.open(newline="", encoding="utf-8") as csv_file:
        for row in csv.DictReader(csv_file):
            missed += int(row["LINE_MISSED"])
            covered += int(row["LINE_COVERED"])
    total = missed + covered
    percentage = round((covered / total * 100.0) if total else 100.0, 1)
    return {"lines": total, "covered": covered, "missed": missed, "percentage": percentage}


def github_metadata():
    return {
        "toolId": "mock-github-tool",
        "buildNumber": os.getenv("GITHUB_RUN_NUMBER", "local"),
        "buildId": os.getenv("GITHUB_RUN_ID", "local"),
        "attemptNumber": os.getenv("GITHUB_RUN_ATTEMPT", "1"),
        "workflow": os.getenv("GITHUB_WORKFLOW", "Java CI with Maven"),
        "repository": os.getenv("GITHUB_REPOSITORY", "balaji-in/sprintrunner"),
        "pipelineName": os.getenv("GITHUB_WORKFLOW", "Java CI with Maven"),
        "demoData": True,
    }


def test_payload(name, test_type, stage_name, result, timestamp):
    payload = github_metadata()
    payload.update({
        "stageName": stage_name,
        "testType": test_type,
        "testSummaries": [{
            "name": name,
            "passedTests": result["passed"],
            "failedTests": result["failures"] + result["errors"],
            "skippedTests": result["skipped"],
            "ignoredTests": 0,
            "blockedTests": 0,
            "totalTests": result["tests"],
            "startTime": timestamp,
            "endTime": timestamp,
            "duration": round(result["duration"], 6),
            "passingPercent": round(result["passed"] / result["tests"] * 100.0, 1),
            "testType": test_type,
            "suites": [],
        }],
    })
    return payload


def performance_payload(result, timestamp):
    metric_match = re.search(
        r"iterations=(\d+) durationSeconds=([0-9.]+) throughputPerSecond=([0-9.]+)",
        result["output"],
    )
    iterations = int(metric_match.group(1)) if metric_match else 100_000
    duration = float(metric_match.group(2)) if metric_match else result["duration"]
    throughput = float(metric_match.group(3)) if metric_match else iterations / max(duration, 0.000001)
    if metric_match:
        case_millis = [duration * 1000.0 / iterations]
    else:
        case_millis = [case * 1000.0 for case in result["cases"]] or [duration * 1000.0]

    payload = github_metadata()
    payload.update({
        "stageName": "Performance tests",
        "testType": "Load",
        "name": "Sprint planner load test",
        "duration": round(duration, 6),
        "maximumVirtualUsers": 1,
        "throughput": "{:.2f}/sec".format(throughput),
        "maximumTime": round(max(case_millis), 6),
        "minimumTime": round(min(case_millis), 6),
        "averageTime": round(sum(case_millis) / len(case_millis), 6),
        "ninetyPercent": round(sorted(case_millis)[int((len(case_millis) - 1) * 0.9)], 6),
        "standardDeviation": 0.0,
        "totalOperations": iterations,
        "startTime": timestamp,
        "finishTime": timestamp,
    })
    return payload


def quality_payload(coverage):
    # The non-coverage values are intentionally stable mock values for demo widgets.
    measures = {
        "vulnerabilities": "2",
        "security_rating": "2.0",
        "coverage": str(coverage["percentage"]),
        "ncloc": str(coverage["lines"]),
        "lines_to_cover": str(coverage["lines"]),
        "uncovered_lines": str(coverage["missed"]),
        "bugs": "3",
        "reliability_rating": "2.0",
        "sqale_rating": "1.0",
        "code_smells": "5",
        "sqale_index": "35",
        "duplicated_lines_density": "1.2",
    }
    return {
        "demoData": True,
        "component": {
            "key": "com.sprintrunner:dpr",
            "name": "SprintRunner",
            "qualifier": "TRK",
            "measures": [
                {"metric": metric, "value": value, "bestValue": False}
                for metric, value in measures.items()
            ],
        },
        "mockFindings": {
            "vulnerabilities": [
                {"severity": "HIGH", "status": "OPEN", "message": "Mock input validation finding"},
                {"severity": "MEDIUM", "status": "OPEN", "message": "Mock dependency finding"},
            ],
            "bugs": 3,
            "codeSmells": 5,
            "technicalDebtMinutes": 35,
        },
    }


def write_json(filename, payload):
    with (OUTPUT / filename).open("w", encoding="utf-8") as output_file:
        json.dump(payload, output_file, indent=2)
        output_file.write("\n")


def main():
    OUTPUT.mkdir(parents=True, exist_ok=True)
    unit = read_test_report("unit-test-reports")
    functional = read_test_report("functional-test-reports")
    performance = read_test_report("performance-test-reports")
    coverage = read_coverage()
    timestamp = datetime.now(timezone.utc).isoformat().replace("+00:00", "Z")

    write_json("servicenow-unit-tests.json", test_payload(
        "SprintRunner unit tests", "JUnit", "Unit tests", unit, timestamp))
    write_json("servicenow-functional-tests.json", test_payload(
        "SprintRunner smoke tests", "Smoke", "Functional tests", functional, timestamp))
    write_json("servicenow-performance-tests.json", performance_payload(performance, timestamp))
    write_json("sonar-quality-summary.json", quality_payload(coverage))

    summary = """# SprintRunner quality summary

| Widget | Demo result |
|---|---:|
| Unit tests | {unit_passed}/{unit_total} passed |
| Functional tests | {functional_passed}/{functional_total} passed |
| Performance tests | {performance_passed}/{performance_total} passed |
| Overall coverage | {coverage}% |
| Lines of code | {lines} |
| Lines covered | {covered} |
| Vulnerabilities (mock) | 2 |
| Bug count (mock) | 3 |
| Code smells (mock) | 5 |
| Technical debt (mock) | 35 minutes |
| Duplications (mock) | 1.2% |

All test and coverage values are generated by this run. Quality findings are explicitly mocked.
""".format(
        unit_passed=unit["passed"], unit_total=unit["tests"],
        functional_passed=functional["passed"], functional_total=functional["tests"],
        performance_passed=performance["passed"], performance_total=performance["tests"],
        coverage=coverage["percentage"], lines=coverage["lines"], covered=coverage["covered"],
    )
    (OUTPUT / "summary.md").write_text(summary, encoding="utf-8")
    print(summary)


if __name__ == "__main__":
    main()
