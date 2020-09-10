#!/bin/bash

# Takes a list of recordIDs and extracts the full record tree.
#
# The ID of records that could not be fetched are listed in the file
# failed.dat in the output folder.

# Requirements: bash, xmllint, curl, sed

###############################################################################
# CONFIG
###############################################################################

pushd ${BASH_SOURCE%/*} > /dev/null
if [[ ! -s ../config/summarise.conf ]]; then
    report_error "summarise.conf is missing"
fi
source ../config/summarise.conf
source "common.sh"

: ${SOURCE:="$1"}
: ${OUT_FOLDER:="$2"}
: ${OUT_FOLDER:="records_$(date +%Y%m%d-%H%M%S)"} # This will be a folder

# Note: EXPAND currently has no effect
: ${EXPAND:="true"} # true = expand parent/child, false = single-level records
: ${FORCE:="false"} # If true, already fetched files are overwritten
popd > /dev/null

function usage() {
    cat <<EOF

Usage:  ./get_records.sh record_file [destination_folder]

record_file:        File with the recordIDs to fetch, one id/line
destination_folder: Where to store the fetched records

See the CONFIG section of this script for extra parameters
EOF
    exit $1
}

check_parameters() {
    if [[ -z "$SOURCE" ]]; then
        report_error "Error: No source file specified"
        usage 2
    fi
}

################################################################################
# FUNCTIONS
################################################################################

# Fetch a record from Summa Storage service and store it on the local file system
# Input: A single recordID
get_record() {
    local RECORD_ID="$1"
    local OUT_NAME=$(sed 's/[^a-zA-Z_0-9.]/_/g' <<< "$RECORD_ID").xml
    if [[ "false" == "$FORCE" && -s "$OUT_FOLDER/$OUT_NAME" ]]; then
        log " - Skipping $RECORD_ID as is has already been fetched"
        return
    fi
    log " - Fetching $OUT_FOLDER/$OUT_NAME"
    if [[ "true" == "$EXPAND" ]]; then
        # expand is ignored

        # Fetch the raw response (entity escaped record XML wrapped in a SOAP envelope)
        local RESPONSE=$(curl -s -G "$SUMMA_STORAGE?method=getCustomRecord&expand=false&legacyMerge=false&escapeContent=false&" --data-urlencode "id=${RECORD_ID}")

        # Check that there is content inside of the SOAP envelope
        if [[ "." != ".$( tr '\n' ' ' <<< "$RESPONSE" | grep "<ns1:getCustomRecordReturn[^>]*>.\{10,\}<\/ns1:getCustomRecordReturn")" ]]; then
            echo "$RESPONSE" | sed -e 's/<soapenv.*<ns1:getCustomRecordReturn[^>]*>//' -e 's/<\/ns1:getCustomRecordReturn.*//' -e 's/&lt;/</g' -e 's/&gt;/>/g' -e 's/&quot;/"/g' -e 's/&amp;/&/g' | xmllint --format - > "$OUT_FOLDER/$OUT_NAME"
        else
            report_error "Error: The record with the ID '$RECORD_ID' could not be fetched"
            echo "$RECORD_ID" >> "$OUT_FOLDER/failed.dat"
            FAILED=$(( FAILED+1 ))
        fi
    else
        report_error "EXPAND=false not supported yet"
        exit 4
    fi
}

# Fetch all records for the IDs stored in the file $SOURCE (first command line argument)
get_records() {
    mkdir -p "$OUT_FOLDER"
    FAILED=0
    while read -r RECORD_ID; do
        if [[ "." == ".$RECORD_ID" ]]; then
            continue
        fi
        get_record "$RECORD_ID"
    done < "$SOURCE"
}

###############################################################################
# CODE
###############################################################################

check_parameters "$@"
get_records
log "Finished extracting $(wc -l < "$SOURCE") records ($FAILED failed), result stored in folder $OUT_FOLDER"
