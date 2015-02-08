#!/bin/bash

# Prints all processes which contain string "127.0.0.1" in their command
# and which has a 5 digits PID number in descending order by PID.
ps axo pid,cmd k-pid | egrep "^([ ]*[0-9]{5}) .*[1]27.0.0.1.*$"
