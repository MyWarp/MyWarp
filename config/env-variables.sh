#!/bin/bash
#
# Custom environment variables

export MYWARP_COMMIT_HASH_SHORT="$(git rev-parse --short HEAD)"  
export MYWARP_COMMIT_AUTHOR_NAME="$(git log -1 $TRAVIS_COMMIT --pretty="%aN")"
