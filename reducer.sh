#!/bin/bash

echo "Starting"

# run the jar file, collect output into a temp file
analysis_output=$(mktemp)
compiler_output=$(mktemp)
analysis_error=$(mktemp)
difference=$(mktemp)

java -jar /Users/szh/Developer/ICL/COMP70081_Project/my_work/kotlin/debug-analysis-api.jar --script test.kt >"$analysis_output" 2>"$analysis_error"
kotlinc test.kt 2>&1 >/dev/null | rg "error:" | sed 's/.*error: //' | tr '[:upper:]' '[:lower:]' >"$compiler_output"
python3 /Users/szh/Developer/ICL/COMP70081_Project/my_work/kotlin/verify_output.py "$analysis_output" "$compiler_output" | tee "$difference"

echo "Done"

trap 'rm -f $analysis_output $compiler_output $analysis_error $difference' EXIT INT TERM

echo "Trapped"

# determine if there is an uncaught exception
if rg -q "exception" "$analysis_error"; then
    echo "error branch"
    exit_code=0 # there is an exception
elif [ "$(wc -l <"$difference" | tr -d ' ' | tee lines.txt)" != 0 ]; then
    cat "$difference"
    echo "problematic branch"
    exit_code=0 # there is a problem
else
    echo "fine branch"
    exit_code=1 # there is not an exception
fi

echo "Finishing"

exit $exit_code
