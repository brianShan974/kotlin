#!/bin/bash

result_dir=$1

mkdir -p "$result_dir"

echo "Saving to $1"

echo "Starting"

# initialise counters
problematic_counter=0
error_counter=0

# from here
for ((i = 1; i <= 10000; i++)); do
    src_code=$(mktemp)
    analysis_output=$(mktemp)
    compiler_output=$(mktemp)
    analysis_error=$(mktemp)
    difference=$(mktemp)

    echo "Fuzzing Test $i"

    rprs >"$src_code.kt"
    java -jar /Users/szh/Developer/ICL/COMP70081_Project/my_work/kotlin/debug-analysis-api.jar --script "$src_code.kt" | tr '[:upper:]' '[:lower:]' | tee "$analysis_output" | tee temp_analysis.txt 2>"$analysis_error"
    kotlinc "$src_code.kt" 2>&1 >/dev/null | rg "error:" | sed 's/.*error: //' | tr '[:upper:]' '[:lower:]' | tee "$compiler_output" | tee temp_compiler.txt
    python3 /Users/szh/Developer/ICL/COMP70081_Project/my_work/kotlin/verify_output.py "$analysis_output" "$compiler_output" | tee "$difference" | tee temp_difference.txt

    echo "Done running analysis and compiling"

    trap 'rm -f $analysis_output $compiler_output $analysis_error $difference' EXIT INT TERM

    echo "Trapped"

    if rg -q "Exception" "$analysis_error"; then
        echo "error branch"
        cp "$src_code" "$result_dir/error_$error_counter.kt"
        ((error_counter++))
    elif [ "$(wc -l <"$difference" | tr -d ' ' | tee lines.txt)" != 0 ]; then
        # cat "$difference"
        cp "$src_code" "$result_dir/problematic_$problematic_counter.kt"
        ((problematic_counter++))
        echo "problematic branch"
    else
        echo "fine branch"
    fi

    rm -f "$analysis_output" "$compiler_output" "$analysis_error" "$difference"
done
# until here

echo "Finished"
