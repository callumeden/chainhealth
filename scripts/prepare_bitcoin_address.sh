#!/usr/bin/env bash

echo "Concat of address files into one"

cat ./import/data/test-*/*-address-data-*.csv > ./import/data/sample-address-data.csv
echo "De duping address file"

#sort -u ./import/data/sample-address-data.csv -o ./import/data/sample-address-data.csv

#echo "Done de-dupe"
