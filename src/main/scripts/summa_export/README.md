# Scripts for retrieving changed records from SummaRise/doms

## Suggested workflow

Fetch timestamps and recordIDs for all changes records:
```
FORMAT=csv FIELDS=stime,recordID ./get_solr_docs.sh "stime:[2018-01-08T16:39:00Z TO *]"
```
This will produce a file with the data. Note that the content is not sorted.

To exclude programs not covered by Copydan
```
FORMAT=csv FIELDS=stime,recordID ./get_solr_docs.sh "stime:[2018-01-08T16:39:00Z TO *] NOT (lhovedgenre:film NOT (lsubject:miniserie OR no:"miniserie" OR no:"thrillerserie" OR no:"tvfilm")) NOT (channel_name:(canal8sport OR tv2sport1hd OR eurosportdk OR idinvestigation OR tv3max)) AND lma_long:"tv"  "
```

Extract the last timestamp to use on the next document request:
```
sort < documents_20180130-134914.dat | tail -n 1 | cut -d, -f1
```

Extract the record IDs:
```
cut -d, -f2 < documents_20180130-134914.dat > recordIDs.dat
```

Extract the records:
```
./get_records.sh recordIDs.dat
```

## Notes

The argument to `./get_solr_docs.sh` is a Solr query. `stime:[2018-01-08T16:39:00Z TO *]` could e.g. be `hest AND stime:[2018-01-08T16:39:00Z TO *]` or `ged AND stime:[* TO *]`.

The scripts currently use the developer server `mars`. This must be changed to production at some point, by specifying `SOLR` and `SUMMA_STORAGE`.
