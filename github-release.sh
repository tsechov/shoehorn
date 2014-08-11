#!/bin/bash
export VERSION=$(cat version.sbt |sed 's/.*"\(.*\)"$/\1/')
export API_JSON=$(printf '{"tag_name": "v%s","target_commitish": "master","name": "v%s","body": "Release of version %s","draft": false,"prerelease": false}' $VERSION $VERSION $VERSION)

curl --data "$API_JSON" https://api.github.com/repos/tsechov/shoehorn/releases?access_token=${GITHUB_TOKEN}