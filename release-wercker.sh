#!/bin/bash
if [ "$WERCKER_DEPLOYTARGET_NAME" == "perform-release" ]; then

    git checkout master
    ssh-keyscan -H github.com >> ~/.ssh/known_hosts
    ./github-release-latest.sh
else
    echo "not on perform-release target, not releasing"
fi