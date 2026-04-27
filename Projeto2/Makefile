SHELL := /bin/bash

.PHONY: test coverage mutation property evosuite full clean

test:
	bash ./gradlew test --no-daemon

coverage:
	bash ./gradlew test jacocoTestReport --no-daemon

mutation:
	bash ./gradlew pitest --no-daemon

property:
	bash ./gradlew test --tests "*PropertyTest" --no-daemon

evosuite:
	bash ./gradlew evosuiteGenerate --no-daemon

full:
	./scripts/ats_full_pipeline.sh

clean:
	bash ./gradlew clean --no-daemon
