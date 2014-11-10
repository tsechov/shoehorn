#!/bin/bash
if [ "$WERCKER_DEPLOYTARGET_NAME" == "perform-release" ]; then

    git checkout master
    mkdir ~/.ssh && touch ~/.ssh/known_hosts && ssh-keyscan -H github.com >> ~/.ssh/known_hosts && chmod 600 ~/.ssh/known_hosts
    echo -e $GITHUB_KEY_PRIVATE > ~/.ssh/id_rsa && chmod 600 ~/.ssh/id_rsa
    echo -e $GITHUB_KEY_PUBLIC > ~/.ssh/id_rsa.pub && chmod 600 ~/.ssh/id_rsa.pub
    git config user.email "tsechov@gmail.com"
    git config user.name "tsechov"
    ./github-release-latest.sh
else
    echo "not on perform-release target, not releasing"
fi