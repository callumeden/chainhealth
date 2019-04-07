#!/usr/bin/env bash

if [ $# -eq 0 ]
    then
        echo usage:
        echo -e '\t 1:path to neo4j'

    else
        if [ $# -ne 1 ]; then echo path to neo4j bin needed; fi

        if [ $# -eq 1 ]
            then

            echo "Concat of address files into one"

            cat ./import/data/*-address-data-*.csv > ./import/data/sample-address-data.csv
            echo "De duping address file"

            sort -u ./import/data/sample-address-data.csv -o ./import/data/sample-address-data.csv

            echo "Done de-dupe"
            $1/bin/neo4j-admin import --nodes="./import/headers/address-header.csv,./import/data/sample-address-data.csv"\
                                    --nodes="./import/headers/block-header.csv,./import/data/sample-block-data.*"\
                                    --nodes="./import/headers/coinbase-header.csv,./import/data/sample-coinbase-data.*"\
                                    --nodes="./import/headers/output-header.csv,./import/data/sample-output-data.*"\
                                    --nodes="./import/headers/transaction-header.csv,./import/data/sample-transaction-data.*"\
                                    --relationships="./import/headers/relations-header.csv,./import/relations/.*"\
                                    --ignore-missing-nodes
        fi
fi