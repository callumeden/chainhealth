#!/usr/bin/env bash

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

relation_chained_from_regex="./import/relations/bitcoin-csv-block-*/relation-chained-from-*.csv"
relation_chained_from_files="./import/headers/relation-header.csv"

for file in $relation_chained_from_regex; do
	relation_chained_from_files=("${relation_chained_from_files},${file}")
done

echo "Relation chained from files are ${relation_chained_from_files}"
echo "======================================"

relation_coinbase_regex="./import/relations/bitcoin-csv-block-*/relation-coinbase-*.csv"
relation_coinbase_files="./import/headers/relation-header.csv"

for file in $relation_coinbase_regex; do
	relation_coinbase_files=("${relation_coinbase_files},${file}")
done

echo "Relation coinbase files are ${relation_coinbase_files}"
echo "======================================"

relation_inputs_regex="./import/relations/bitcoin-csv-block-*/relation-inputs-*.csv"
relation_inputs_files="./import/headers/relation-header.csv"

for file in $relation_inputs_regex; do
	relation_inputs_files=("${relation_inputs_files},${file}")
done


echo "Relation input files are ${relation_inputs_files}"
echo "======================================"


relation_locked_to_regex="./import/relations/bitcoin-csv-block-*/relation-locked-to-*.csv"
relation_locked_to_files="./import/headers/relation-header.csv"

for file in $relation_locked_to_regex; do
	relation_locked_to_files=("${relation_locked_to_files},${file}")
done


echo "Relation locked to files are ${relation_locked_to_files}"
echo "======================================"


relation_mined_in_regex="./import/relations/bitcoin-csv-block-*/relation-mined-in-*.csv"
relation_mined_in_files="./import/headers/relation-header.csv"

for file in $relation_mined_in_regex; do
	relation_mined_in_files=("${relation_mined_in_files},${file}")
done


echo "Relation mined in files are ${relation_mined_in_files}"
echo "======================================"


relation_outputs_regex="./import/relations/bitcoin-csv-block-*/relation-outputs-*.csv"
relation_outputs_files="./import/headers/relation-header.csv"

for file in $relation_outputs_regex; do
	relation_outputs_files=("${relation_outputs_files},${file}")
done


echo "Relation outputs files are ${relation_outputs_files}"
echo "======================================"

