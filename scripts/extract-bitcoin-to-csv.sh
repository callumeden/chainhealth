#!/bin/bash

SECONDS=0

if [ $# -eq 0 ]
    then
        echo usage:
        echo -e '\t fromheight \t toheight'

    else
        if [ $# -ne 2 ]; then echo three parameters needed; fi

        if [ $# -eq 2 ]
            then
                echo `curl -s --user max:01cecfb951713cc9ac820f8c2e0b695b http://localhost:9090/admin/extract/bitcoin/neo4j/$1/$2`

                duration=$SECONDS
                echo "$(($duration / 60)) minutes and $(($duration % 60)) seconds elapsed."
        fi
fi

