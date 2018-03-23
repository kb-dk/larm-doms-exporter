#!/usr/bin/env bash

#
# As its name implies, this script is the entry point for the daily extraction of data for larm export.
#


check_parameters() {
    if [[ -z "$TIME" ]]; then
        >&2 echo "Error: Initial time must be specified as parameter or in timestamp.txt"
        usage 2
    fi
}

do_stuff() {
##:
    echo Working with $OUT , $TIME
    FORMAT=csv ./get_solr_docs.sh "stime:{$TIME TO *]  NOT (lhovedgenre:film NOT (lsubject:miniserie OR no:"miniserie" OR no:"thrillerserie" OR no:"tvfilm")) NOT (channel_name:(canal8sport OR tv2sport1hd OR eurosportdk OR idinvestigation OR tv3max)) AND lma_long:"tv"  " stime,recordID $OUT
    sort < $OUT | tail -n 1 | cut -d, -f1 > timestamp.txt
    IDFILE=$OUT.ids.dat
    cut -d, -f2 < $OUT > $IDFILE
    ./get_records.sh $IDFILE $OUTDIR
}

function usage() {
    cat <<EOF

Usage:  ./daily_extract.sh [timestamp]

timestamp: The timestamp from which to start extraction. The format is like 2018-01-08T16:39:00Z
If the timestamp parameter is absent, the script attempts to read the timestamp from the file timestamp.txt

See the CONFIG section of this script for extra parameters

EOF
    exit $1
}


pushd ${BASH_SOURCE%/*} > /dev/null
if [[ -s summarise.conf ]]; then
    source summarise.conf
fi
: ${OUTDIR:="."}
echo creating directory $OUTDIR if necessary
mkdir -p $OUTDIR
: ${OUT:="$OUTDIR/documents_$(date +%Y%m%d-%H%M%S).dat"}

TIME=$(head -1 timestamp.txt 2> /dev/null)
: ${TIME:=$1}

check_parameters
do_stuff
popd > /dev/null

