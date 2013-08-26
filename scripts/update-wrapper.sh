#!/bin/bash

wrappers=`find . -name gradle-wrapper.properties`
newgradleVersion="http\:${1#http:}"

echo $wrappers
echo "Updating to new gradle: ${newgradleVersion}"

currentDir=`pwd`
failed=""


for wrapper in $wrappers; do
    echo "Updating: $wrapper"
    sed -E -e "s/distributionUrl.*/distributionUrl=${newgradleVersion//\//\\/}/" -i '' $wrapper
    dir=${wrapper%/gradle/wrapper/gradle-wrapper.properties}
    if [ -d "$dir/.git" ]; then
        cd $dir
        git add gradle/wrapper/gradle-wrapper.properties
        git commit -m "New wrapper ($newgradleVersion)"
        git pull --rebase
        if [ ! $? -eq 0 ]; then
            echo "Rebase failed for ${dir}, please fix and try again."
            failed="$failed, ${dir}"
        else
            git push
        fi
        cd $currentDir
    fi
done

if [ -n "$failed" ]; then
    echo -e "\n\n*** Updating failed for: $failed"
else
    echo -e "\n\n*** Update ok."
fi

