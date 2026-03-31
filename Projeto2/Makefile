SHELL := /bin/bash

.PHONY: test coverage mutation property evosuite full clean

test:
	./gradlew test --no-daemon

coverage:
	./gradlew test jacocoTestReport --no-daemon

mutation:
	./gradlew pitest --no-daemon

property:
	./gradlew test --tests "*PropertyTest" --no-daemon

evosuite:
	./gradlew evosuiteGenerate --no-daemon

full:
	./scripts/ats_full_pipeline.sh

clean:
	./gradlew clean --no-daemon
