#!/usr/bin/env bash

API="https://api.prod.larm.fm/v6/EZSearch/Get?q=&facets=\{Search\}.Kanal%3AUkendt&pageIndex=[INDEX]&pageSize=[SIZE]&format=json2"

function getTotalCount() {
    local API_ZERO_SIZE="${API/\[INDEX\]/0}"
    local API_ZERO_SIZE="${API_ZERO_SIZE/\[SIZE\]/0}"

    #echo $API_ZERO_SIZE
    local RESULT=`curl -s "$API_ZERO_SIZE"`
    #echo $RESULT

    TOTAL_COUNT=`echo $RESULT | jq '.Body.TotalCount' -`
}

function printUnknownChannels() {
    local PAGE_SIZE=1000
    local LAST_PAGE=$(($TOTAL_COUNT / $PAGE_SIZE))

    local API_100_SIZE="${API/\[SIZE\]/$PAGE_SIZE}"

    for i in $(seq 0 $LAST_PAGE); do
        local API_100_SIZE_INDEX_I="${API_100_SIZE/\[INDEX\]/$i}"

        local RESULT=`curl -s "$API_100_SIZE_INDEX_I"`
        local COUNT=`echo $RESULT | jq '.Body.Count' -`

        for j in $(seq 0 $((COUNT-1))); do
            local FIRST=`echo $RESULT | jq ".Body.Results[$j].Identifier" -`
            echo "$FIRST" >> identifiers.txt
        done



    done
}

getTotalCount
printUnknownChannels


#API2="https://api.prod.larm.fm/v6/EZSearch/Get?q=&facets=\{Search\}.Kanal%3AUkendt&pageIndex=0&pageSize=1&format=json2"
#RESULT2=`curl ${API2}`
#echo $RESULT2