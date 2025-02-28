.PHONY: build start dev

build:
	@./gradlew clean build

start:
	@java -jar build/libs/api-1.0.0.jar

dev:
	@./gradlew bootRun

lint:
	ktlint --format
