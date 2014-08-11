#!/bin/bash
#export VERSION=$(cat version.sbt |sed 's/.*"\(.*\)"$/\1/')
VERSION=$(git describe --abbrev=0 --tags|sed 's/^v//')
API_JSON=$(printf '{"tag_name": "v%s","target_commitish": "master","name": "v%s","body": "Release of version %s","draft": false,"prerelease": false}' $VERSION $VERSION $VERSION)

FILENAME=shoehorn_2.10-${VERSION}

UPLOAD_URL_TEMPLATE=$(curl --data "$API_JSON" https://api.github.com/repos/tsechov/shoehorn/releases?access_token=${GITHUB_TOKEN} |jq -r ".upload_url" )

EXT="jar"
for EXT in jar jar.md5 jar.sha1 pom pom.md5 pom.sha1; do

    UPLOAD_URL=$(echo ${UPLOAD_URL_TEMPLATE} |sed "s/{?name}/?name=${FILENAME}.${EXT}/")
    echo "uploading to: "${UPLOAD_URL}
    curl -XPOST -H "Authorization: token ${GITHUB_TOKEN}" -H "Content-Type: application/zip" --data-binary target/publish/shoehorn/shoehorn_2.10/${VERSION}/${FILENAME}.${EXT} ${UPLOAD_URL}
done


