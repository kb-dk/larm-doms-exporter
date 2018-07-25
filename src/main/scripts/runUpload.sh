#!/bin/bash
#
# Script for bulk ftp upload of xml envelope files as a timestamped tar-file. The file
# is uploaded then downloaded again to check for bitwise integrity.
#

script_path=$(dirname $(readlink -f $0))
configfile=$(readlink -f $(dirname $(readlink -f $0))/../config/summarise.conf)
source $configfile
source "common.sh"

main()
{
    if [ "$(find ${fileOutputDirectory} -maxdepth 1 -type f -name '*.xml' | wc -l)" -gt 0 ]
    then
        filename=$(date +%s_%FT%H-%M-%S)_envelopes.tar.gz
        cd ${fileOutputDirectory}
        find . -maxdepth 1 -name '*.xml' -printf '%f\n' | tar -czf ${filename} --remove-files -T -
        log "Uploading tar-archive containing $(tar tf ${filename}|wc -l) files."
        md5_original=$(openssl dgst -md5 ${filename} | cut -f2 -d' ')
        tempCopyFile=$(mktemp -u)
        lftp <<FTP >> $logfile 2>&1
open ${ftpServer}
user ${ftpUsername} ${ftpPassword}
put ${filename}
get ${filename} -o ${tempCopyFile}
quit
FTP
        md5_copy=$(openssl dgst -md5 ${tempCopyFile} | cut -f2 -d' ')
        if [ "${md5_original}" = "${md5_copy}" ]
        then
            rm -f ${tempCopyFile}
            log "Upload of ${filename} completed."
            exit 0
        else
            report_error "Upload of ${filename} failed: see ${tempCopyFile} for failed file."
            exit 6
        fi
    else
        log "No files found"
        exit 0
    fi
}

print_usage()
{
    echo "Usage: $(basename $0) "
    echo
    echo "Settings will be sourced from this file:"
    echo "$configfile"
    echo "and must include:"
    echo "fileOutputDirectory (the input directory to this script)"
    echo "ftpServer"
    echo "ftpUsername"
    echo "ftpPassword"
    echo
}

[ -z "$fileOutputDirectory" ] && print_usage && exit 2
[ -z "$ftpServer" ] && print_usage && exit 3
[ -z "$ftpUsername" ] && print_usage && exit 4
[ -z "$ftpPassword" ] && print_usage && exit 5


# off we go!
main
