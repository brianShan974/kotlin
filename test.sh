#!/bin/bash

echo "Starting"

# run the jar file, collect output into a temp file
temp=$(mktemp)
java -jar /Users/szh/Developer/ICL/COMP70081_Project/my_work/kotlin/debug-analysis-api.jar --script --show-serializable test2.kt >"$temp"
exit_code=$?

echo "Done"

trap 'rm -f "$temp"' EXIT INT TERM

echo "Trapped"

# determine if there is an uncaught exception
if rg -q "Serializable" "$temp"; then
    echo "problematic branch"
    exit_code=0 # there is an exception
else
    echo "fine branch"
    exit_code=1 # there is not an exception
fi

echo "Finishing"

exit $exit_code
