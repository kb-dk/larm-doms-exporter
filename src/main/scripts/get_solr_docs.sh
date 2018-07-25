#!/bin/bash

# Extracts key values from all Solr docs matching a query.

# Requirements: bash, curl, jq, sed

###############################################################################
# CONFIG
###############################################################################

pushd ${BASH_SOURCE%/*} > /dev/null
if [[ ! -s ../config/summarise.conf ]]; then
    echo "summarise.conf is missing" >&2
fi
source ../config/summarise.conf
source "common.sh"

: ${ID:=recordID}
: ${PAGE:=1000}
: ${REQUEST_EXTRA:=""} # Additional custom params. Prefix with '&'
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
        report_error "Error: No query specified"
        usage 2
    fi
    if [[ "-h" == "$QUERY" || "-?" == "$QUERY" ]]; then
        usage 0
    fi
    if [[ -f "$OUT" || -f "${OUT}.gz" ]]; then
        report_error "Error: $OUT already processed"
        usage 3
    fi
    if ! [ -x "$(command -v $JQ)" ]; then
      report_error 'Error: jq is not installed and executable at the specified location ($JQ)' >&2
      usage 4
    fi
}

################################################################################
# FUNCTIONS
################################################################################

# Input: Source-file
# Output: Requested fields as csv
function get_fields() {
    if [[ "json" == "$FORMAT" ]]; then
        $JQ -c ' .response.docs[]' < "$1"
    elif [[ "csv" == "$FORMAT" ]]; then
        local EXPAND=.$(sed 's/,/, ./g' <<< "$FIELDS")
        if [[ "true" == "$QUOTE_CSV_STRINGS" ]]; then
            <$1 $JQ -c ".response.docs[] | [$EXPAND]" | sed -e 's/^\[//' -e 's/\]$//'
        else
            <$1 $JQ -c " .response.docs[] | [$EXPAND]" | sed -e 's/^\["//' -e 's/","/,/' -e 's/"\]$//'
        fi
    else
        report_error "Error: Unknown FORMAT '$FORMAT"
        usage 5
    fi
}

# Output: HITS
function get_hit_count() {
    HITS=$(curl -s -G "$SOLR?${REQUEST_BASE}&rows=0" --data-urlencode "q=${QUERY}" | $JQ .response.numFound)
#    if [[ ! "$HITS" -gt "0" ]]; then
#        >&2 echo "Error: No hits for query '$QUERY' with call"
#        >&2 echo "$SOLR?${REQUEST_BASE}&rows=0&q=${QUERY}"
#        usage 4
#    fi
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
        NEXT_MARK=$($JQ -r .nextCursorMark < "$T")
        if [[ "$NEXT_MARK" == "null" ]]; then
            report_error "Error: Got 'null' as cursorMark for query $SOLR?${PAGE_BASE}&q=${QUERY}&cursorMark=$OLD_MARK"
            exit 6
        fi
        if [ ".$OLD_MARK" == ".$NEXT_MARK" ]; then
            break
        fi
        # Comment this in for debugging
        #>&2 echo -ne "\033[2K$COUNT/$HITS $NEXT_MARK\r"
        #    echo -ne "\033[2K$COUNT/$HITS\r"
        COUNT=$((COUNT+$PAGE))
    done
    #>&2 echo ""
    rm $T
}

###############################################################################
# CODE
###############################################################################

check_parameters "$@"
get_hit_count
log "Total hits for query '${QUERY}': ${HITS} documents"
get_documents > "$OUT"
log "Finished extracting data for $HITS documents, result stored in $OUT"
