#!/usr/bin/env bash
cp ci-resources/toolchains.xml $HOME/.m2

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -in ci-resources/codesigning.asc.enc -out ci-resources/codesigning.asc -d -pass pass:$CODESIGNING_AES_PASSWORD
    gpg --batch --quiet --fast-import ci-resources/codesigning.asc
fi