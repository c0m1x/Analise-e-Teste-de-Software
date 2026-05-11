#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

print_menu() {
  cat <<'EOF'

ATS Menu (Makefile)

Projeto 1 (Maven)
  1) JUnit + jqwik (p1-test)
  2) JaCoCo (p1-coverage)
  3) PIT (p1-mutation)
  4) EvoSuite generate+export (p1-evosuite-generate)
  5) Copy EvoSuite tests into src/test/java (p1-evosuite-copy)
  6) Run EvoSuite tests (p1-evosuite-run)

Projeto 2 (Gradle)
  7) JUnit + jqwik (p2-test)
  8) JaCoCo (p2-coverage)
  9) PIT (p2-mutation)
 10) EvoSuite generate (p2-evosuite)
 11) Full pipeline (p2-full)

Ambos
 12) Full (p1-full + p2-full) (full)

  0) Exit
EOF
}

run_target() {
  local target="$1"
  echo
  echo "[ATS] Running: make $target"
  make "$target"
}

while true; do
  print_menu
  read -r -p "Option: " opt
  case "$opt" in
    1) run_target p1-test ;;
    2) run_target p1-coverage ;;
    3) run_target p1-mutation ;;
    4) run_target p1-evosuite-generate ;;
    5) run_target p1-evosuite-copy ;;
    6) run_target p1-evosuite-run ;;
    7) run_target p2-test ;;
    8) run_target p2-coverage ;;
    9) run_target p2-mutation ;;
    10) run_target p2-evosuite ;;
    11) run_target p2-full ;;
    12) run_target full ;;
    0) echo "Bye"; exit 0 ;;
    *) echo "Invalid option: $opt" ;;
  esac

done
