#!/bin/bash

# Recursively finds in the current directory all "*log" files which contain
# the string "error" (case insensitive) and will save them to the file
# "list_of_log_files_with_error.txt" and will print them to the screen with their size.
grep --include \*.log -rli "error" | tee list_of_log_files_with_error.txt | xargs wc -c
