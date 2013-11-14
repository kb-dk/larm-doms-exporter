#!/bin/bash

script_path=$(dirname $(readlink -f $0))
configfile=$(readlink -f $(dirname $(readlink -f $0))/../config/lde.infrastructure.properties)
source $configfile

logdir=$HOME/logs
# Save the pid of this process for later use
this_pid=$$
# This process logs here (see redirection before calling main())
logfile=$logdir/run_Upload.$this_pid.log

main()
{
if stat -t ${fileOutputDirectory}/*.xml >/dev/null 2>&1 
then  
  filename=$(date +%s)_$(date +%FT%H-%M-%S)_envelopes.tar.gz
  cd ${fileOutputDirectory}
  tar -czf ${filename} --remove-files  *.xml
  echo "Uploading tar-archive containing $(tar tf ${filename}|wc -l) files."
  md5_original=$(openssl dgst -md5 ${filename} | cut -f2 -d' ')
  tempCopyFile=$(mktemp -u)
  lftp <<FTP
    open ${ftpServer} 
    user ${ftpUsername} ${ftpPassword}
    put ${filename}
    get ${filename} -o ${tempCopyFile}
    quit
FTP
  md5_copy=$(openssl dgst -md5 ${tempCopyFile} | cut -f2 -d' ')
  if [ "${md5_original}" == "${md5_copy}" ]
  then
     rm -f ${tempCopyFile}
     echo "Upload of ${filename} completed."
     exit 0
  else
     echo "Upload of ${filename} failed: see ${tempCopyFile} for failed file."
     exit 6
  fi  
else
  echo "No files found"
  exit 0
fi

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
