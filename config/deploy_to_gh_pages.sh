#!/bin/bash
shopt -s extglob

set -e

binaries_to_store=("mywarp-bukkit/build/libs/mywarp-bukkit-*-all.jar")
development_binaries_to_store=("mywarp-bukkit/build/libs/mywarp-bukkit-!(*javadoc|*sources|*-all).jar" "mywarp-core/build/libs/mywarp-core-!(*javadoc|*sources).jar")

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
    git clone --quiet --branch=${gh_pages_branch} "https://${GITHUB_TOKEN}@github.com/MyWarp/mywarp.github.io" "$HOME/web" > /dev/null
    cd "$HOME/web"

    echo "GH Pages repository cloned (branch: $gh_pages_branch)."

    # Create a YML file with build information
    build_date="$(date +'%d/%m/%Y')"
    author_name="${MYWARP_COMMIT_AUTHOR_NAME}"
    if [ -z ${author_name+x} ];
        then author_name="n/a";
    fi
    yml_path="${yml_parent_folder}/${TRAVIS_BUILD_NUMBER}_${MYWARP_COMMIT_HASH_SHORT}.yml"
    mkdir -p ${yml_parent_folder}
    touch yml_path

    # write the YML
    yq new build.by ${ci_name} >> yml_path

    yq w yml_path build.number "${TRAVIS_BUILD_NUMBER}"
    yq w yml_path build.successful "true"
    yq w yml_path build.date "${build_date}"

    yq w yml_path commit.short_hash "${MYWARP_COMMIT_HASH_SHORT}"
    yq w yml_path commit.message "${MYWARP_COMMIT_SUBJECT}"
    yq w yml_path commit.author "${author_name}"
    yq w yml_path commit.tags "${TRAVIS_TAG}"

    yq w yml_path artifacts []
    yq w yml_path development_artifacts []
    #artifact information will be added later

    echo "Info YML '${yml_path}' created."

    # Copy the binaries
    mkdir -p "${binary_destination}"
    for binary in "${binaries_to_store[@]}"; do
        cp -Rf ${TRAVIS_BUILD_DIR}/${binary} "${binary_destination}"
        echo -e "Copied '${binary}' to '${binary_destination}'."

        binary_name=$(find "${binary_destination}" -wholename "${binary}" -printf "%f\n")
        yq w yml_path "artifacts[+]" "${binary_name}"
    done

    # Copy the development binaries
    for dev_binary in "${development_binaries_to_store[@]}"; do
        cp -Rf ${TRAVIS_BUILD_DIR}/${dev_binary} "${binary_destination}"
        echo -e "Copied '${dev_binary}' to '${binary_destination}'."

        dev_binary_name=$(find "${binary_destination}" -wholename "${dev_binary}" -printf "%f\n")
        yq w yml_path "development_artifacts[+]" "${dev_binary_name}"
    done

    # Add artifacts and YML to git
    git add -f ${binary_destination}
    git add -f ${yml_path}
    echo "Artifact information written to YML '${yml_path}' and added to git."

    # Commit and push changes
    git commit -m "Automatic deployment of binaries from build #${TRAVIS_BUILD_NUMBER}."
    git push -fq origin ${gh_pages_branch} > /dev/null

    echo "Successfully deployed binaries to mywarp.github.io."

else
    echo "Skipping deployment."

fi
