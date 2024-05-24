#!/bin/sh
NAME=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Title: ${NAME}:${VERSION}"
docker build -t "${NAME}:${VERSION}" .
docker tag "${NAME}:${VERSION}" "${NAME}:latest"