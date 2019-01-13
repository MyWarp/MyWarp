#!/bin/bash
shopt -s extglob

set -e

binaries_to_store=("mywarp-bukkit/build/libs/mywarp-bukkit-*-all.jar" "mywarp-bukkit/build/libs/mywarp-bukkit-!(*javadoc|*sources|*-all).jar" "mywarp-core/build/libs/mywarp-core-!(*javadoc|*sources).jar")

ci_name="Travis"
gh_pages_branch="src"
binary_destination="source/files/${TRAVIS_BUILD_NUMBER}_${MYWARP_COMMIT_HASH_SHORT}"
yml_parent_folder="data/builds"

if [[ ("$TRAVIS_BRANCH" == "master" || "$TRAVIS_BRANCH" == "$TRAVIS_TAG") && \
    ( "$TRAVIS_REPO_SLUG" == "MyWarp/MyWarp" ) && \
    ( "$TRAVIS_PULL_REQUEST" == "false" ) ]]; then

    echo "Deploying binaries from build #${TRAVIS_BUILD_NUMBER} to GH Pages..."

    # Clone the GP Pages repository
    git config --global user.email "deploy@travis-ci.org"
    git config --global user.name "Deployment Bot"
    git clone --quiet --branch=${gh_pages_branch} https://${GITHUB_TOKEN}@github.com/MyWarp/mywarp.github.io $HOME/web > /dev/null
    cd $HOME/web

    echo "GH Pages repository cloned (branch: $gh_pages_branch)."

    # Create a YML file with build information
    build_date=$(date +'%d/%m/%Y')
    author_name=${MYWARP_COMMIT_AUTHOR_NAME}
    if [ -z ${author_name+x} ];
        then author_name="n/a";
    fi
    yml_path="${yml_parent_folder}/${TRAVIS_BUILD_NUMBER}_${MYWARP_COMMIT_HASH_SHORT}.yml"

    mkdir -p ${yml_parent_folder}
    #the following should probably make use of yq (or jq)
    cat > ${yml_path} << EOF
build:
  by: "${ci_name}"
  number: ${TRAVIS_BUILD_NUMBER}
  successful: true
  date: "${build_date}"
commit:
  short_hash: "${MYWARP_COMMIT_HASH_SHORT}"
  message: "${MYWARP_COMMIT_SUBJECT}"
  author: "${author_name}"
  tags: "${TRAVIS_TAG}"
EOF

    if [ ${#binaries_to_store[@]} -eq 0 ]; then
        echo "artifacts: []" >> ${yml_path}
    else
        echo "artifacts:" >> ${yml_path}
        for binary in "${binaries_to_store[@]}"; do
            name=$(basename ${binary})
            echo "  - ${name}" >> ${yml_path}
        done
    fi

    git add -f ${yml_path}
    echo "Info YML '${yml_path}' created and added."

    # Copy the binaries
    mkdir -p ${binary_destination}
    for binary in "${binaries_to_store[@]}"; do
        cp -Rf ${TRAVIS_BUILD_DIR}/${binary} ${binary_destination}
        echo -e "Copied '${binary}' to '${binary_destination}'."
    done
    git add -f ${binary_destination}

    # Commit and push changes
    git commit -m "Automatic deployment of binaries from build #${TRAVIS_BUILD_NUMBER}."
    git push -fq origin ${gh_pages_branch} > /dev/null

    echo "Successfully deployed binaries to mywarp.github.io."

else
    echo "Skipping deployment."

fi
