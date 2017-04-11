#!/bin/bash
#
# Publish Javadoc of successful CI builds to https://mywarp.github.io/javadoc
# See https://web.archive.org/web/20150107174657/http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/

destinationBranch="src";
javadocDestination="source/javadocs/"

if [ "$TRAVIS_REPO_SLUG" == "MyWarp/MyWarp" ] && \
   [ "$TRAVIS_JDK_VERSION" == "oraclejdk8" ] && \
   [ "$TRAVIS_PULL_REQUEST" == "false" ] &&  \
   [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing javadoc...\n"

  git config --global user.email "deploy@travis-ci.org"
  git config --global user.name "Deployment Bot"
  git clone --quiet --branch=${destinationBranch} https://${GITHUB_TOKEN}@github.com/MyWarp/mywarp.github.io $HOME/web > /dev/null
  cd $HOME/web

  echo -e "Repository cloned (branch $destinationBranch)."

  for module in mywarp-core mywarp-bukkit; do
    mkdir -p ./javadoc/$module
    git rm -rf ./javadoc/$module/*

    finalDestination=.${javadocDestination}/$module;

    cp -Rf $TRAVIS_BUILD_DIR/$module/build/docs/javadoc/ ${finalDestination}
    git add -f ./javadoc/$module
    echo -e "Javadocs for '$module' copied to ${finalDestination}."
  done

  git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to mywarp.github.io."
  git push -fq origin $destinationBranch > /dev/null

  echo -e "Published Javadoc to mywarp.github.io.\n"

fi
