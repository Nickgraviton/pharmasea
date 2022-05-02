#!/usr/bin/env bash
user=elli
password=Orpheus2019
database=medicine
sourcefile=$1

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <name_of_database_file>"
    exit 1
fi

mysql --user="$user" --password="$password" --database="$database" --execute="USE $database; SOURCE $sourcefile;"
