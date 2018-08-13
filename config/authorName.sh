#!/bin/bash
#
# Make the commit's author avilable as environment variable
# See https://stackoverflow.com/a/39719500

export MYWARP_COMMIT_AUTHOR_NAME="$(git log -1 $TRAVIS_COMMIT --pretty="%aN")"
