#!/bin/bash
#
# Script for bulk ftp upload of xml envelope files as a timestamped tar-file. The file
# is uploaded then downloaded again to check for bitwise integrity.
#

script_path=$(dirname $(readlink -f $0))
configfile=$(readlink -f $(dirname $(readlink -f $0))/../config/summarise.conf)
source $configfile

logdir=$HOME/logs
# This process logs here
logfile=$logdir/runUpload.log

# report(): write $1 with a date prefixed to $logfile
# params: $1 = message  $2 = stderr
# $2 is optional, when set to 'stderr' it will additionally echo the message
# to stderr
report()
{
    local d=$(date "+%Y-%m-%d %H:%M:%S")
    echo "$d: $1" >> $logfile
    # Also to stderr?
    [ "$2" = "stderr" ] && echo "$d: $1" >&2
}
report_err() { report "$1" stderr; }

# rotate_log(): rotate a logfile
# params: $1 = logfile to rotate, $2 = max logsize which determines rotation
# $2 is optional and defaults to 10M
# The number of log generations defaults to 8 but can be overrided by setting
# rotate_log_generations before calling the function
rotate_log()
{
    # How many log generations to keep, 8 is the default
    local numlogs=${rotate_log_generations:-8}

    local logfile=$1
    # Default is 10485760 bytes, 10M
    local maxlogsize=${2:-10485760}

    # Multiple instances could be running in parallel so we need to grab a lock
    # to avoid race conditions
    (
        flock 200
        # Should we rotate?
        if [ -r "$logfile" ] && [ "$(stat -c %s $logfile)" -ge $maxlogsize ]; then
            # Rotate log
            for x in $(seq $numlogs -1 1)
            do
                [ -r ${logfile}.$x ] && mv ${logfile}.$x ${logfile}.$((x+1))
            done
            [ -r ${logfile} ] && mv ${logfile} ${logfile}.1
        fi
    ) 200> ${logfile}.lck
}

main()
{
    if [ "$(find ${fileOutputDirectory} -maxdepth 1 -type f -name '*.xml' | wc -l)" -gt 0 ]
    then
        filename=$(date +%s_%FT%H-%M-%S)_envelopes.tar.gz
        cd ${fileOutputDirectory}
        find . -maxdepth 1 -name '*.xml' -printf '%f\n' | tar -czf ${filename} --remove-files -T -
        report "Uploading tar-archive containing $(tar tf ${filename}|wc -l) files."
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
            report "Upload of ${filename} completed."
            exit 0
        else
            report_err "Upload of ${filename} failed: see ${tempCopyFile} for failed file."
            exit 6
        fi
    else
        report "No files found"
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

# Check if logs need to be rotated
rotate_log_generations=3
rotate_log $logfile 1048576

# off we go!
main
