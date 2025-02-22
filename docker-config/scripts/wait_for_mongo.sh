#!/usr/bin/env bash
host="$1"
port="$2"
shift 2
until nc -z "$host" "$port"; do
  >&2 echo "MongoDB is NOT available. Sleeping for 1 second."
  sleep 1
done
>&2 echo "MongoDB is available. Executing Java command."
exec "$@"