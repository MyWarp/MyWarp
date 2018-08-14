#!/bin/bash
#
# Deploy binaries to https://mywarp.github.io/builds/
set -e

binaries_to_store=("mywarp-core/build/libs/mywarp-core-3.0-SNAPSHOT.jar" "mywarp-bukkit/build/libs/mywarp-bukkit-3.0-SNAPSHOT-all.jar")

ci_name="Travis"
gh_pages_branch="src"
binary_destination="source/builds/${TRAVIS_BUILD_NUMBER}_${MYWARP_COMMIT_HASH_SHORT}"
yml_parent_folder="data/builds"

if [ "$TRAVIS_REPO_SLUG" == "MyWarp/MyWarp" ] && \
   [ "$TRAVIS_PULL_REQUEST" == "false" ] &&  \
   [ "$TRAVIS_BRANCH" == "master" ]; then

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
    cat > ${yml_path} << EOF
build:
  by: ${ci_name}
  number: ${TRAVIS_BUILD_NUMBER}
  successful: true
  date: ${build_date}
commit:
  short_hash: ${MYWARP_COMMIT_HASH_SHORT}
  message: ${MYWARP_COMMIT_SUBJECT}
  author: ${author_name}
EOF

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

fi
