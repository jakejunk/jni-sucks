FROM gradle:6.6-jdk14

EXPOSE 8080

# Install Node/NPM
ARG nodeMajor=14

RUN apt-get update && \
    apt-get install -y build-essential curl && \
    curl -sL https://deb.nodesource.com/setup_${nodeMajor}.x -o nodesource_setup.sh && \
    bash nodesource_setup.sh && \
    apt-get install -y nodejs
