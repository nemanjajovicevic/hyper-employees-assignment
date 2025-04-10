#!makefile

.PHONY: build up down clean clean-ryuk

# Build the Docker image using Spring Boot Maven plugin
build:
	mvn spring-boot:build-image

# Start Docker Compose, but first ensure the image is built
up: build
	docker-compose up -d

# Stop and remove Docker Compose containers
down:
	docker-compose down

# The 'clean-ryuk' target is defined to manually remove lingering Testcontainers Ryuk containers.
# Testcontainers starts a helper container (Ryuk) to monitor and cleanup orphaned containers.
# However, in some environments (like IDEs or long-lived JVM sessions), Ryuk may persist after tests.
# This target forces removal of any container whose name contains 'testcontainers-ryuk-' to ensure a clean state.
clean-ryuk:
	@echo "Removing lingering Testcontainers Ryuk containers..."
	@CONTAINERS=$$(podman ps -a -q --filter "name=testcontainers-ryuk-"); \
	if [ -n "$$CONTAINERS" ]; then \
	  podman rm -f $$CONTAINERS; \
	else \
	  echo "No Ryuk containers found."; \
	fi

all-down: down clean-ryuk
