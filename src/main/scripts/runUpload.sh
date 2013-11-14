#!/bin/bash

script_path=$(dirname $(readlink -f $0))
configfile=$(readlink -f $(dirname $(readlink -f $0))/../config/lde.infrastructure.properties)
. $configfile

logdir=$HOME/logs
# Save the pid of this process for later use
this_pid=$$
# This process logs here (see redirection before calling main())
logfile=$logdir/run_Upload.$this_pid.log

main()
{
tar -czf ${fileOutputDirectory}/foobar.tar ${fileOutputDirectory}/*.xml
exit 0;
}

print_usage()
{
    echo "Usage: $(basename $0) "
    echo
    echo
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

# Save all output to the logfile as well
exec > >(tee $logfile) 2>&1

# off we go!
main
