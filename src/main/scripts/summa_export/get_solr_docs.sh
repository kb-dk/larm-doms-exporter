#!/bin/bash

# Extracts key values from all Solr docs matching a query.

# Requirements: bash, curl, jq, sed

###############################################################################
# CONFIG
###############################################################################

pushd ${BASH_SOURCE%/*} > /dev/null
if [[ -s summarise.conf ]]; then
    source summarise.conf
fi
: ${ID:=recordID}
: ${PAGE:=1000}
: ${REQUEST_EXTRA:=""} # Additional custom params. Prefix with '&'

: ${SOLR:="http://mars.statsbiblioteket.dk:50001/solr/doms.1.stage/select"} # Devel
: ${QUERY:="$1"}

: ${FIELDS:="$2"}
: ${FIELDS:="$ID"}

: ${OUT:="$3"}
: ${OUT:="documents_$(date +%Y%m%d-%H%M%S).dat"} # Use '/dev/stdout' for direct output

: ${FORMAT:="csv"} # Possible values: cvs, json
: ${QUOTE_CSV_STRINGS:=false}

: ${REQUEST_BASE:="wt=json&groups=false&facet=false&hl=false&fl=${FIELDS}${REQUEST_EXTRA}"}
popd > /dev/null

function usage() {
    cat <<EOF

Usage:  ./get_solr_docs.sh query [fields [out]]

query:  Limiting query. Use '*:*' to match all documents
fields: Comma-separated list of fields. Defaults to 'recordID'
out:    Output file. Use /dev/stdout for firect output

See the CONFIG section of this script for extra parameters
EOF
    exit $1
}

check_parameters() {
    if [[ -z "$QUERY" ]]; then
        >&2 echo "Error: No query specified"
        usage 2
    fi
    if [[ "-h" == "$QUERY" || "-?" == "$QUERY" ]]; then
        usage 0
    fi
    if [[ -f "$OUT" || -f "${OUT}.gz" ]]; then
        >&2 echo "Error: $OUT already processed"
        usage 3
    fi
}

################################################################################
# FUNCTIONS
################################################################################

# Input: Source-file
# Output: Requested fields as csv
function get_fields() {
    if [[ "json" == "$FORMAT" ]]; then
        jq -c ' .response.docs[]?' < "$1"
    elif [[ "csv" == "$FORMAT" ]]; then
        local EXPAND=.$(sed 's/,/, ./g' <<< "$FIELDS")
        if [[ "true" == "$QUOTE_CSV_STRINGS" ]]; then
            <$1 jq -c ".response.docs[]? | [$EXPAND]" | sed -e 's/^\[//' -e 's/\]$//'
        else
            <$1 jq -c " .response.docs[]? | [$EXPAND]" | sed -e 's/^\["//' -e 's/","/,/' -e 's/"\]$//'
        fi
    else
        >&2 echo "Error: Unknown FORMAT '$FORMAT"
        usage 5
    fi
}

# Output: HITS
function get_hit_count() {
    HITS=$(curl -s -G "$SOLR?${REQUEST_BASE}&rows=0" --data-urlencode "q=${QUERY}" | jq .response.numFound)
    if [[ ! "$HITS" -gt "0" ]]; then
        >&2 echo "Error: No hits for query '$QUERY' with call"
        >&2 echo "$SOLR?${REQUEST_BASE}&rows=0&q=${QUERY}"
        usage 4
    fi
}

function get_documents() {
    PAGE_BASE="${REQUEST_BASE}&rows=${PAGE}&sort=${ID}+asc"
    T=$(mktemp)

    NEXT_MARK="*"
    COUNT=0

    while true; do
        curl -s -G "$SOLR?${PAGE_BASE}" --data-urlencode "q=${QUERY}" --data-urlencode "cursorMark=$NEXT_MARK" > $T
        get_fields "$T"
        OLD_MARK="$NEXT_MARK"
        NEXT_MARK=$(jq -r .nextCursorMark < "$T")
        if [[ "$NEXT_MARK" == "null" ]]; then
            >&2 echo "Error: Got 'null' as cursorMark for query $SOLR?${PAGE_BASE}&q=${QUERY}&cursorMark=$OLD_MARK"
            exit 6
        fi
        if [ ".$OLD_MARK" == ".$NEXT_MARK" ]; then
            break
        fi
        >&2 echo -ne "\033[2K$COUNT/$HITS $NEXT_MARK\r"
        #    echo -ne "\033[2K$COUNT/$HITS\r"
        COUNT=$((COUNT+$PAGE))
    done
    >&2 echo ""
    rm $T
}

###############################################################################
# CODE
###############################################################################

check_parameters "$@"
get_hit_count
>&2 echo "Total hits for query '${QUERY}': ${HITS} documents"
get_documents > "$OUT"
>&2 echo "Finished extracting data for $HITS documents, result stored in $OUT"
