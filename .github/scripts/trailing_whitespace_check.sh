#!/bin/bash

set -e

# copy "-o -path ./docs/imgs \" to add another directory to exclude in checking
files="$(find . -type d \( -path ./.git \
  -o -path ./docs/imgs \
  \) -prune -o -type f -exec egrep -l " +$" {} \;)"

exit_code=0
if [ ! -z "$files" ]; then
  printf "Files containing trailing whitespace\n"
  printf '%s\n' $files
  exit_code=1
fi

exit $exit_code
