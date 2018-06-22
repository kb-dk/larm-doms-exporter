larm-kuana-exporter
==================

Utility to export newly-updated or created Kuana radio/tv objects to the LARM/CHAOS platform.

This package replaces the much more complex larm-doms-exporter. The simplification arises because this package
extracts data directly from the summarise index without querying Kuana directly. 

In normal use, one executes once per day the two scripts:

1. `daily_extract.sh`
1. `runUpload.sh`

The `daily_extract.sh` script reads its configuration from `config/summarise.conf`. 

The `runUpload.sh` script is unchanged except that it now also reads its configuration from the `config/summarise.conf` file.

The configuration parameters are documented and described in `config/summarise.conf`.

The "persistence layer" consists of a flat file `bin/timestamp.txt` containing the timestamp of the last program exported on the previous run (in the format 2018-01-08T16:39:00Z).
On first run, the timestamp can be passed as an argument to the daily_extract.sh script and the timestamp file will then be created.