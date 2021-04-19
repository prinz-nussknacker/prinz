#!/bin/bash

set -e

# Add lines "-o <condition> -prune" to add more exceptions
files="$(find . \
  -type d \( -path ./.git \) -prune \
  -type d \( -path ./docs \) -prune \
  -o -type f \( -name "*.jpg" -o -name "*.png" \) -prune \
  -o -type f -exec egrep -l " +$" {} \;)"

exit_code=0
if [ ! -z "$files" ]; then
  printf "Files containing trailing whitespace\n"
  printf '%s\n' $files
  exit_code=1
fi

exit $exit_code
