SHELL := /bin/bash

.PHONY: help menu full \
	p1-test p1-coverage p1-mutation p1-evosuite-generate p1-evosuite-copy p1-evosuite-run p1-full \
	p2-test p2-coverage p2-mutation p2-evosuite p2-full

ROOT_DIR := $(CURDIR)

# Override if needed:
#   make JAVA17=/path/to/jdk17 JAVA8=/path/to/jdk8 p1-test
JAVA17 ?= /usr/lib/jvm/java-17-openjdk-amd64
JAVA8 ?= /usr/lib/jvm/java-8-openjdk-amd64

MVN ?= mvn

P1_DIR := Projeto1/SpotifyUM
P1_POM := $(P1_DIR)/pom.xml
P2_DIR := Projeto2

# Helper: run a command with a specific JAVA_HOME
define WITH_JAVA
JAVA_HOME=$(1) PATH="$(1)/bin:$$PATH"
endef

help:
	@echo "ATS Makefile (root)"
	@echo ""
	@echo "Projeto 1 (Maven):"
	@echo "  make p1-test              # JUnit + jqwik (Java 17)"
	@echo "  make p1-coverage           # JaCoCo (Java 17)"
	@echo "  make p1-mutation           # PIT (Java 17)"
	@echo "  make p1-evosuite-generate  # EvoSuite generate+export (Java 8)"
	@echo "  make p1-evosuite-copy      # Copy generated tests into src/test/java"
	@echo "  make p1-evosuite-run       # Run only ESTest (Java 8)"
	@echo "  make p1-full               # test + coverage + mutation"
	@echo ""
	@echo "Projeto 2 (Gradle):"
	@echo "  make p2-test               # JUnit + jqwik"
	@echo "  make p2-coverage            # JaCoCo"
	@echo "  make p2-mutation            # PIT"
	@echo "  make p2-evosuite            # EvoSuite (Docker)"
	@echo "  make p2-full                # atsFullPipeline"
	@echo ""
	@echo "Ambos:"
	@echo "  make full                  # p1-full + p2-full"
	@echo "  make menu                  # interactive numeric menu"

menu:
	@bash scripts/ats_menu.sh

# -------------------------
# Projeto 1 (Maven)
# -------------------------

p1-test:
	@cd "$(ROOT_DIR)" && $(call WITH_JAVA,$(JAVA17)) $(MVN) -f "$(P1_POM)" clean test

p1-coverage:
	@cd "$(ROOT_DIR)" && $(call WITH_JAVA,$(JAVA17)) $(MVN) -f "$(P1_POM)" clean verify

p1-mutation:
	@cd "$(ROOT_DIR)" && $(call WITH_JAVA,$(JAVA17)) $(MVN) -f "$(P1_POM)" org.pitest:pitest-maven:mutationCoverage

p1-evosuite-generate:
	@cd "$(ROOT_DIR)" && rm -rf "$(P1_DIR)/.evosuite" && $(call WITH_JAVA,$(JAVA8)) $(MVN) -f "$(P1_POM)" -P evosuite-generate -Dmaven.test.skip=true clean test

p1-evosuite-copy:
	@cd "$(ROOT_DIR)" && \
		if [ ! -d "$(P1_DIR)/target/generated-test-sources/evosuite/org" ]; then \
			echo "[ATS] EvoSuite export not found. Run: make p1-evosuite-generate"; \
			exit 2; \
		fi && \
		find "$(P1_DIR)/src/test/java" -type f \( -name "*_ESTest.java" -o -name "*_ESTest_scaffolding.java" \) -delete && \
		cp -r "$(P1_DIR)/target/generated-test-sources/evosuite/org" "$(P1_DIR)/src/test/java/"

p1-evosuite-run:
	@cd "$(ROOT_DIR)" && $(call WITH_JAVA,$(JAVA8)) $(MVN) -f "$(P1_POM)" -P evosuite-run -Dmaven.test.failure.ignore=true clean test

p1-full: p1-test p1-coverage p1-mutation

# -------------------------
# Projeto 2 (Gradle)
# -------------------------

p2-test:
	@cd "$(P2_DIR)" && bash ./gradlew clean test --no-daemon

p2-coverage:
	@cd "$(P2_DIR)" && bash ./gradlew test jacocoTestReport --no-daemon

p2-mutation:
	@cd "$(P2_DIR)" && bash ./gradlew pitest --no-daemon

p2-evosuite:
	@cd "$(P2_DIR)" && bash ./gradlew evosuiteGenerate --no-daemon

p2-full:
	@cd "$(P2_DIR)" && bash ./gradlew atsFullPipeline --no-daemon

# -------------------------
# Both projects
# -------------------------

full: p1-full p2-full
