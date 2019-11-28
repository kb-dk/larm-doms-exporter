#!/usr/bin/env bash

# Script only works if .pgpass is in use

ID=$1

if [[ ! $ID =~ "uuid:" ]]
then
   ID="uuid:"$ID
fi

psql -d lde-prod -U lde -h 127.0.0.1 -c "UPDATE domsexportrecord SET lastexporttimestamp = null, state='PENDING' WHERE id='$ID'"