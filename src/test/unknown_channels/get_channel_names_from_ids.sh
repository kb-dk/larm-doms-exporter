#!/usr/bin/env bash
ids_file=larm_identifiers.txt
baseurl="http://naiad.statsbiblioteket.dk:7880/fedora/objects/uuid%3A[UUID]/datastreams/PBCORE/content"

username=
password=

saveChannelName(){
    local uuid=$1

    url=${baseurl/"[UUID]"/$uuid}
#    echo $url

    local response=`curl -s --user $username:$password "$url"`

    local channel_name=`echo $response | xmllint \
            --xpath "/*[local-name()='PBCoreDescriptionDocument']/*[local-name()='pbcorePublisher']/*[local-name()='publisherRole' and text() = 'channel_name']/../*[local-name()='publisher']/text()" \
            - 2>/dev/null`

    local channel_long_name=`echo $response | xmllint \
            --xpath "/*[local-name()='PBCoreDescriptionDocument']/*[local-name()='pbcorePublisher']/*[local-name()='publisherRole' and text() = 'kanalnavn']/../*[local-name()='publisher']/text()" \
            - 2>/dev/null`

    local channel_id_field=`echo $response | xmllint \
            --xpath "/*[local-name()='PBCoreDescriptionDocument']/*[local-name()='pbcoreExtension']/*[local-name()='extension' and starts-with(., 'kanalid')]/text()" \
            - 2>/dev/null`

    local channel_id=`echo "$channel_id_field" | cut -d ':' -f 2`

    if [ -z "$channel_name" ] || [ "$channel_name" = "" ] ; then
        echo $uuid >> unidentified_ids.txt
#        echo $response
    else
        if grep -q "\"$channel_name\"" "unknown_channel_names.txt" ; then
            :
        else
            echo "$channel_id" >> unknown_channel_ids.txt
            echo "\"$channel_name\"" >> unknown_channel_names.txt
            echo "$channel_long_name" >> unknown_channel_long_names.txt
            echo "$uuid" >> unknown_identifiers.txt
        fi
    fi

}

saveAllChannelNames(){
    i=1
    while IFS= read uuid
    do
        echo $i > "count.txt"
        saveChannelName "$uuid"
        i=$((i+1))
    done <"$ids_file"
}

saveAllChannelNames
#saveChannelName dc4bec36-b5f3-4238-8540-db29c3993c2b