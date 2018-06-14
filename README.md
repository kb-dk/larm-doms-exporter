larm-kuana-exporter
==================

Utility to export newly-updated or created Kuana radio/tv objects to the LARM/CHAOS platform.

This package replaces the much more complex larm-doms-exporter. The simplification arises because this package
extracts data directly from the summarise index without querying Kuana directly. 

In normal use, one executes once per day the two scripts:

1. `daily_extract.sh`
1. `runUpload.sh`

The `runUpload.sh` script is unchanged from the previous implementation and reads its configuration from the `config/lde.infrastructure.properties` file.

The `daily_extract.sh` script reads its configuration from `config/summarise.conf`. There are only three configuration parameters:

1. The solr endpoint
2. The xalan command to use to make the final Xml transformation, and
3. The path to the output directory.

The "persistence layer" consists of a flat file `bin/timestamp.txt` containing the timestamp of the last program exported on the previous run (in the format 2018-01-08T16:39:00Z).
On first run, the timestamp can be passed as an argument to the daily_extract.sh script and the timestamp file will then be created.