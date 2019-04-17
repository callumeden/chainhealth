#!/usr/bin/env bash

if [ $# -eq 0 ]
    then
        echo usage:
        echo -e '\t 1:path to neo4j'

    else
        if [ $# -ne 1 ]; then echo path to neo4j bin needed; fi

        if [ $# -eq 1 ]
            then

            address_files_all="./import/headers/address-header.csv,./import/data/sample-address-data-unique.csv"

            echo "Address files are ${address_files_all}"
            echo "======================================"

            block_files_regex="./import/data/blocks-with-exchange-rate/output-*/sample-block-data-*"
            block_files_all="./import/headers/block-header.csv"

            for file in $block_files_regex; do
                block_files_all=("${block_files_all},${file}")
            done

            echo "Block files are ${block_files_all}"
            echo "======================================"

            coinbase_files_regex="./import/data/bitcoin-csv-block-*/sample-coinbase-data-*"
            coinbase_files_all="./import/headers/coinbase-header.csv"

            for file in $coinbase_files_regex; do
                coinbase_files_all=("${coinbase_files_all},${file}")
            done

            echo "Coinbase files are ${coinbase_files_all}"
            echo "======================================"

            output_files_regex="./import/data/bitcoin-csv-block-*/sample-output-data-unique.csv"
            output_files_all="./import/headers/output-header.csv"

            for file in $output_files_regex; do
                output_files_all=("${output_files_all},${file}")
            done

            echo "Output files are ${output_files_all}"
            echo "======================================"

            transaction_files_regex="./import/data/bitcoin-csv-block-*/sample-transaction-data-unique.csv"
            transaction_files_all="./import/headers/transaction-header.csv"

            for file in $transaction_files_regex; do
                transaction_files_all=("${transaction_files_all},${file}")
            done

            echo "Transaction files are ${transaction_files_all}"
            echo "======================================"

            relation_files_regex="./import/relations/bitcoin-csv-block-*/*"
            relation_files_all="./import/headers/relations-header.csv,./import/relations/entity-relationships.csv"

            for file in $relation_files_regex; do
                relation_files_all=("${relation_files_all},${file}")
            done

            echo "Relation files are ${relation_files_All}"
            echo "======================================"


            $1/bin/neo4j-admin import --nodes="${address_files_all}"\
                                    --nodes="${block_files_all}"\
                                    --nodes="${coinbase_files_all}"\
                                    --nodes="${output_files_all}"\
                                    --nodes="${transaction_files_all}"\
                                    --nodes="./import/headers/entity-header.csv,./import/data/entity-nodes.csv"\
                                    --relationships="${relation_files_all}"\
                                    --ignore-missing-nodes \
                                    --high-io=true
        fi
fi