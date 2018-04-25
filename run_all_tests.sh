#!/usr/bin/env bash
sbt clean compile coverage test it:test coverageReport
python dependencyReport.py api-platform-test-login-frontend
