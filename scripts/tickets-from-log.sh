#!/bin/bash
FROM=$1
TO=$2
if [ -z "$FROM" ]; then
	cat -<<EOF

Usage: $(basename $0) <from-version> [to-version]

This script expects to be run from the directory above the checked out
source trees of plugin-api engine bundled-plugins and deployit.

EOF
	exit 1
fi	
for i in plugin-api engine bundled-plugins deployit; do
	if [ ! -d "$i" ]; then
		echo "Directory $i not found!"
	else
		( 
			cd "$i" && git log ${i}-${FROM}..${TO:+${i}-${TO}} 
		)
	fi
done | egrep -i -o '(DEPLOYIT(PB)?-[0-9]+|DEPL-[0-9]+)' | tr [:lower:] [:upper:] | sed -E 's/DEPLOYIT(PB)?-/DEPL-/g' | sort -u
