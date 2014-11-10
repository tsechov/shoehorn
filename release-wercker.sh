#!/bin/bash
if [ "$WERCKER_DEPLOYTARGET_NAME" == "perform-release" ]; then

    git checkout master
    mkdir ~/.ssh && touch ~/.ssh/known_hosts && ssh-keyscan -H github.com >> ~/.ssh/known_hosts && chmod 600 ~/.ssh/known_hosts
    ./github-release-latest.sh
else
    echo "not on perform-release target, not releasing"
fi