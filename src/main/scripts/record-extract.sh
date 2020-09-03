#!/usr/bin/env bash

#
# This script mimics daily_extract.sh but works on specific records, rather than all in a daily export.
# This is for use in recovery, when specific records need to be uploaded to LARM
#

check_parameters() {
    if [[ -f "$IDFILE" ]]; then
        >&2 echo "Error: Filename needs to be specified and file needs to exist"
        usage 2
    fi
}

do_export() {
    log "Exporting programs with ids for $IDFILE"
    ./get_records.sh $IDFILE $EXPORTDIR
}

do_transform() {
    if [ -z "$(find $EXPORTDIR -name '*xml')" ]; then
        log "No records found in $IDFILE"
        exit 0
    fi

    for file in $EXPORTDIR/*xml; do
        local channel_name=`xmllint \
            --xpath "/*[local-name()='record']/*[local-name()='content']/*[local-name()='record']/*[local-name()='metadata']/*[local-name()='DeliverableUnit']/*[local-name()='Metadata']/*[local-name()='PBCoreDescriptionDocument']/*[local-name()='pbcorePublisher']/*[local-name()='publisherRole' and text() = 'channel_name']/../*[local-name()='publisher']/text()" \
            $file`
        local uuid=`xmllint \
            --xpath "/*[local-name()='record']/*[local-name()='content']/*[local-name()='record']/*[local-name()='metadata']/*[local-name()='DeliverableUnit']/*[local-name()='DeliverableUnitRef']/text()" \
            $file`
        if grep -q "\"$channel_name\"" ../config/whitelistedChannels.csv ; then
            log "Transforming $file"
            tempfile=$file.larm.xml
            $XALAN -xsl ../config/XIPToLarm.xsl -in $file -out $tempfile
            if grep -q "###NO_OBJECT_TYPE###" "$tempfile"; then
                report_error "formatMediaType not recognised. Moving $file to $STALLEDDIR."
                mv $file $STALLEDDIR/.
                rm $tempfile
            elif grep -q "<PublicationChannel/>" "$tempfile"; then
                report_error "Someone forgot to update XIPToLarm.xsl with channel '$channel_name'. Moving $file to $STALLEDDIR."
                mv $file $STALLEDDIR/.
                rm $tempfile
            else
                mv $tempfile $TRANSFORMEDDIR/$uuid.xml && rm $file
            fi
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

Usage:  ./record_extract.sh <idfile>

idfile: File containing one id per line. The format is like pvica_radioTV:du:016825ca-e54a-4afe-8a8b-8072088d84c8

In addition the script reads four environment variables from the file summarise.conf:
EXPORTDIR - the directory for exported metadata
TRANSFORMEDDIR - the directory for transformed metadata
STALLEDDIR - the directory for stalled program metadata
XALAN - path to the xalan jar-file

EOF
    exit $1
}


: ${IDFILE:=$(readlink -f $1)}

pushd ${BASH_SOURCE%/*} > /dev/null
if [[ ! -s ../config/summarise.conf ]]; then
    echo "summarise.conf is missing" >&2
fi
source ../config/summarise.conf
source "common.sh"

log "Creating directory $EXPORTDIR if necessary"
mkdir -p $EXPORTDIR
log "Creating directory $TRANSFORMEDDIR if necessary"
mkdir -p $TRANSFORMEDDIR
log "Creating directory $STALLEDDIR if necessary"
mkdir -p $STALLEDDIR


check_parameters
cleanup_log
do_export
do_transform
popd > /dev/null
