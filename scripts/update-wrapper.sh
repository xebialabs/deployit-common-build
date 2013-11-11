#!/bin/bash

wrappers=`find . -name gradle-wrapper.properties`
newgradleVersion="http\:${1#http:}"

echo $wrappers
echo "Updating to new gradle: ${newgradleVersion}"

for wrapper in $wrappers; do
   sed -E -e "s/distributionUrl.*/distributionUrl=${newgradleVersion//\//\\/}/" -i '' $wrapper
done

