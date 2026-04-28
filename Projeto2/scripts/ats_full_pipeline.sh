#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "[ATS] Running full pipeline: tests + coverage + mutation + evosuite"
bash ./gradlew test jacocoTestReport pitest evosuiteGenerate --no-daemon

echo "[ATS] Pipeline finished"
echo "[ATS] Reports:"
echo " - Tests:   build/reports/tests/test/index.html"
echo " - JaCoCo:  build/reports/jacoco/test/html/index.html"
echo " - PIT:     build/reports/pitest/index.html"
