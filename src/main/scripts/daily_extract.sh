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

do_export() {
    log "Exporting programs with timestamp after $TIME to $OUT"
    FORMAT=csv ./get_solr_docs.sh "stime:{$TIME TO *]  NOT (lhovedgenre:film NOT (lsubject:miniserie OR no:"miniserie" OR no:"thrillerserie" OR no:"tvfilm")) NOT (channel_name:(canal8sport OR tv2sport1hd OR eurosportdk OR idinvestigation OR tv3max)) AND lma_long:"tv"  " stime,recordID $OUT
    if [[ -s $OUT ]]; then
        sort < $OUT | tail -n 1 | cut -d, -f1 > timestamp.txt
        IDFILE=$OUT.ids.dat
        cut -d, -f2 < $OUT > $IDFILE
        ./get_records.sh $IDFILE $OUTDIR
    else
        log "No new records found since $TIME"
        exit 0
    fi
}

do_transform() {
   for file in $OUTDIR/*xml; do
        local channel_name=`xmllint \
            --xpath "/*[local-name()='record']/*[local-name()='content']/*[local-name()='record']/*[local-name()='metadata']/*[local-name()='DeliverableUnit']/*[local-name()='Metadata']/*[local-name()='PBCoreDescriptionDocument']/*[local-name()='pbcorePublisher']/*[local-name()='publisherRole' and text() = 'channel_name']/../*[local-name()='publisher']/text()" \
            $file`
        if grep -q "\"$channel_name\"" ../config/whitelistedChannels.csv ; then
            log "Transforming $file"
            tempfile=$file.larm.xml
            $XALAN -xsl ../config/XIPToLarm.xsl -in $file -out $tempfile && mv $tempfile $file
        else
            if grep -q "\"$channel_name\"" ../config/blacklistedChannels.csv ; then
                log "Channel $channel_name is blacklisted. Removing $file"
                rm $file
            else
                report_error "Channel $channel_name is not known. Moving $file to $STALLEDDIR. Go to the following page for instructions: $unknownChannelPage"
                mv $file $STALLEDDIR/.
            fi
        fi

   done
}


function usage() {
    cat <<EOF

Usage:  ./daily_extract.sh [timestamp]

timestamp: The timestamp from which to start extraction. The format is like 2018-01-08T16:39:00Z
If the file timestamp.txt exists then the value is read from the file and the parameter is ignored.

In addition the script reads four environment variables from the file summarise.conf:
OUTDIR - the directory for output
STALLEDDIR - the directory for stalled program metadata
SOLR - the Solr endpoint to query
XALAN - path to the xalan jar-file

EOF
    exit $1
}


pushd ${BASH_SOURCE%/*} > /dev/null
if [[ ! -s ../config/summarise.conf ]]; then
    echo "summarise.conf is missing" >&2
fi
source ../config/summarise.conf
source "common.sh"

log "Creating directory $OUTDIR if necessary"
mkdir -p $OUTDIR
log "Creating directory $STALLEDDIR if necessary"
mkdir -p $STALLEDDIR

: ${OUT:="$OUTDIR/documents_$(date +%Y%m%d-%H%M%S).dat"}
TIME=$(head -1 timestamp.txt 2> /dev/null)
: ${TIME:=$1}

check_parameters
cleanup_log
do_export
do_transform
popd > /dev/null
