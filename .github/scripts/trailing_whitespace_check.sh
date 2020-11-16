#!/bin/bash

set -e
files="$(find . -type d \( -path ./.git \
  -o -path ./docs/imgs \                             # copy this line to add another directory to exclude in checking
  \) -prune -o -type f -exec egrep -l " +$" {} \;)"

exit_code=0
if [ ! -z "$files" ]; then
  printf "Files containing trailing whitespace\n"
  printf '%s\n' $files
  exit_code=1
fi

exit $exit_code
