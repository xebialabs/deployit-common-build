This project contains the data on open source libraries and licenses used in XL products.

# Generating a license overview

The following command can be used to generate a CSV file with libraries and licenses:

    cat src/main/resources/license-data.json | jq '.artifacts|with_entries(.value = .value["license"])|to_entries|map(.key + "," + .value)|.[]' > ~/tmp/licenses.csv
