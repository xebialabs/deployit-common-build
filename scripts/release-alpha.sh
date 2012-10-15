#!/bin/sh

echo "Release Alpha"
echo ""

currentAlphaLine=`grep "version.*alpha" build.gradle`
currentAlphaVersion=`echo $currentAlphaLine | cut -d \' -f 2`
newAlphaVersion=${currentAlphaVersion%-*}-$((${currentAlphaVersion##*-} + 1))

if [[ $(git status --porcelain | wc -l) -gt 0 ]]; then
    echo "Repository is dirty, please commit first"
    exit -1
fi

if [ -f build.gradle.old ]; then
    echo "Old build.gradle.old detected, please check whether you were in the middle of a previous alpha release before trying again."
    exit -1
fi

echo "Ready to release new alpha version ${newAlphaVersion}"
echo "Press enter to continue, ctrl-c to abort"
read

git pull --rebase
if [ ! $? -eq 0 ]; then
    echo "Rebase failed, please fix and try again."
    exit -1
fi

sed -E -i.old -e "s/\/\/[[:space:]]*version/    version/" -e "s/${currentAlphaVersion}/${newAlphaVersion}/" -e "s/[[:space:]]*(version.*SNAPSHOT)/\/\/  \1/" build.gradle
gradle clean test uploadArchives

if [ ! $? -eq 0 ]; then
    echo "Build failed!"
    exit -1
fi

sed -E -i.old -e  "s/\/\/[[:space:]]*version/    version/" -e "s/[[:space:]]*(version.*alpha)/\/\/  \1/" build.gradle
git add build.gradle
git commit -m "$(basename `pwd`) $newAlphaVersion"
rm build.gradle.old

echo "Done. Don't forget to push the updated build.gradle file."

