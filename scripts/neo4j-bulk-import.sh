#!/usr/bin/env bash

for file in ./import/data/*-address-data-*.csv; do
    echo "De-duping $file"
    sort -u $file -o $file
 done

~/Applications/neo4j-community-3.5.3/bin/neo4j-admin import --nodes="./import/headers/address-header.csv,./import/data/sample-address-data.*"\
                        --nodes="./import/headers/block-header.csv,./import/data/sample-block-data.*"\
                        --nodes="./import/headers/coinbase-header.csv,./import/data/sample-coinbase-data.*"\
                        --nodes="./import/headers/output-header.csv,./import/data/sample-output-data.*"\
                        --nodes="./import/headers/transaction-header.csv,./import/data/sample-transaction-data.*"\
                        --relationships="./import/headers/relations-header.csv,./import/relations/.*"\
                        --ignore-missing-nodes
