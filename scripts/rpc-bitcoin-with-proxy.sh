#!/usr/bin/env bash

echo Get blockchain info

BITCOIN_HOSTNAME=192.168.122.1
BITCOIN_PORT=8332
BITCOIN_UN=bitcoinrpc
BITCOIN_PW=1F41F6F6EE0363A19F487E9E5E63866CC83776BB0F1529B5C92CC643E282EBFA

PROXY_HOSTNAME=localhost
PROXY_PORT=9999

result=`curl -w "\n\n" --socks5-hostname ${PROXY_HOSTNAME}:${PROXY_PORT} --user ${BITCOIN_UN}:${BITCOIN_PW} --data-binary '{"jsonrpc": "1.0", "id":"curltest", "method": "getrawtransaction", "params": ["13bf8627f25cde3f7db974f4ca233198d0c7fbc7792618eccc7ace0f8d2b35cf"] }' -H 'content-type: text/plain;' http://${BITCOIN_HOSTNAME}:${BITCOIN_PORT}`
echo ${result}