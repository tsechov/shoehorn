#!/bin/bash

if [ "$(git rev-parse --abbrev-ref HEAD)" != "master" ] ;then { echo "not on master branch" 1>&2; exit; }; fi

. setenv.sh

sbt "release with-defaults" || { echo "sbt release failed" 1>&2; exit; }

VERSION=$(git describe --abbrev=0 --tags|sed 's/^v//')

git checkout release
git reset ${VERSION}
git push origin release

API_JSON=$(printf '{"tag_name": "v%s","target_commitish": "master","name": "v%s","body": "Release of version %s","draft": false,"prerelease": false}' $VERSION $VERSION $VERSION)

UPLOAD_URL_TEMPLATE=$(curl --data "$API_JSON" https://api.github.com/repos/tsechov/shoehorn/releases?access_token=${GITHUB_TOKEN} |jq -r ".upload_url" )

#FILENAME=shoehorn_2.10-${VERSION}

#EXT="jar"
#for EXT in jar jar.md5 jar.sha1 pom pom.md5 pom.sha1; do
#    UPLOAD_URL=$(echo ${UPLOAD_URL_TEMPLATE} |sed "s/{?name}/?name=${FILENAME}.${EXT}/")
#    echo "uploading to: "${UPLOAD_URL}
#    curl -XPOST -H "Authorization: token ${GITHUB_TOKEN}" -H "Content-Type: application/zip" --data-binary target/publish/shoehorn/shoehorn_2.10/${VERSION}/${FILENAME}.${EXT} ${UPLOAD_URL}
#done


