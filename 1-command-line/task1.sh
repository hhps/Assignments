#!/bin/bash

# Recursively finds in the current directory all "*java" files which do not contain
# the string "import ru.hh.deathstar" and will save them to file "almost_harmless.txt".
grep --include \*.java -rL "import ru.hh.deathstar" > almost_harmless.txt
