.PHONY: initialize application certs help api-build api-run build-api-prod run-api-prod

help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

initialize: application certs
	@sudo ./docs/infra/local/scripts/hosts.sh
	@corepack enable
	@yarn install

application:
	@brew install mkcert nss mysql traefik gradle ktlint node watchman corepack || true
	@npm install -g yarn || true

# install
certs:
	@mkcert -install
	@rm -rf docs/infra/local/cert && mkdir docs/infra/local/cert
	@cd docs/infra/local/cert && mkcert grantly.work '*.grantly.work'

clean-node-modules:
	@rm -rf node_modules

clean: clean-node-modules

docker-up:
	@docker compose up -d

docker-down:
	@docker compose down

docker-restart: docker-down docker-up

build-api:
	@$(MAKE) -C src/api build

run-api:
	@$(MAKE) -C src/api dev

start-api:
	@$(MAKE) -C src/api start

lint-api:
	@$(MAKE) -C src/api lint

build-api-alpine: build-api
	docker build -t grantly:latest -f docs/infra/local/Dockerfile .