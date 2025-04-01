.PHONY: build start dev

build:
	@./gradlew clean build -x test

start:
	@java -jar -Dspring.profiles.active=prod build/libs/api-1.0.0.jar

dev:
	@SPRING_PROFILES_ACTIVE=local ./gradlew bootRun

lint:
	ktlint --format

test:
	@./gradlew test
