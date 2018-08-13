#!/bin/bash
#
# Publish SNAPSHOT binaries to https://mywarp.github.io/builds/

destinationBranch="src"
binaryDestination="source/builds/${TRAVIS_BUILD_NUMBER}_${TRAVIS_COMMIT}"
storeBinaries=("mywarp-core/build/libs/mywarp-core-3.0-SNAPSHOT.jar" "mywarp-bukkit/build/libs/mywarp-bukkit-3.0-SNAPSHOT-all.jar")
ciName="Travis"

if [ "$TRAVIS_REPO_SLUG" == "MyWarp/MyWarp" ] && \
   [ "$TRAVIS_PULL_REQUEST" == "false" ] &&  \
   [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing builds...\n"
  
  # Cloning the website's repository
  git config --global user.email "deploy@travis-ci.org"
  git config --global user.name "Deployment Bot"
  git clone --quiet --branch=${destinationBranch} https://${GITHUB_TOKEN}@github.com/MyWarp/mywarp.github.io $HOME/web > /dev/null
  cd $HOME/web

  echo -e "Repository cloned (branch $destinationBranch).\n"
  
  # Create a YAML file with build information...
  filename="data/builds/${TRAVIS_BUILD_NUMBER}_${TRAVIS_COMMIT}.yml"
  buildDate=$(date +'%d\%m\%Y')
  authorName=$MYWARP_COMMIT_AUTHOR_NAME
  if [ -z ${authorName+x} ];
    then authorName="n/a";
  fi

  touch $filename
  echo "build:" >> $filename
  echo "  by: ${ciName}" >> $filename
  echo "  number: ${TRAVIS_BUILD_NUMBER}" >> $filename
  echo "  succesfull: true" >> $filename #For now we only publish succesfull builds
  echo "  date: ${buildDate}">> $filename
  echo "commit:" >> $filename
  echo "  shorthash: ${TRAVIS_COMMIT}">> $filename
  echo "  message: ${TRAVIS_COMMIT_MESSAG}">> $filename
  echo "  author: ${authorName}">> $filename
  echo "" >> $filename
  echo -e "$filename written.\n"

  # Copy the binaries
  for binary in $"{storeBinaries[@]}"; do
    mkdir -p ${binaryDestination}
    git rm -rf ${binaryDestination}/*

    cp -Rf $TRAVIS_BUILD_DIR/$binary ${binaryDestination}
    git add -f ${binaryDestination}
    echo -e "'${binary}' copied to '${binaryDestination}'."
  done

  # Commit and push changes
  git commit -m "Binaries of successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to mywarp.github.io."
  git push -fq origin $destinationBranch > /dev/null

  echo -e "Published binaries to mywarp.github.io.\n"

fi
