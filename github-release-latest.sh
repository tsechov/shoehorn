#!/bin/bash
#export VERSION=$(cat version.sbt |sed 's/.*"\(.*\)"$/\1/')
VERSION=$(git describe --abbrev=0 --tags|sed 's/^v//')
API_JSON=$(printf '{"tag_name": "v%s","target_commitish": "master","name": "v%s","body": "Release of version %s","draft": false,"prerelease": false}' $VERSION $VERSION $VERSION)

FILENAME=shoehorn_2.10-${VERSION}.jar

UPLOAD_URL=$(curl --data "$API_JSON" https://api.github.com/repos/tsechov/shoehorn/releases?access_token=${GITHUB_TOKEN} |jq ".upload_url"|sed "s/{?name}/?name=${FILENAME}/")

curl -XPOST -s -H "Authorization: token ${GITHUB_TOKEN}" -H "Content-Type: application/zip" --data-binary target/publish/shoehorn/shoehorn_2.10/${VERSION}/${FILENAME} ${UPLOAD_URL}