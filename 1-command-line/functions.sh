#!/bin/bash

# Common functions

# Call it as grep_sorted_values search_regexp log_file
grep_sorted_values () {
    local values=( $(
        for line in $(egrep -x "$1" "$2" | egrep -o '([0-9]+\.[0-9]{2}ms)$' | egrep -o '^([0-9]+\.[0-9]{2})');
        do
            echo "$line"
        done | sort -g ) )
    echo "${values[@]}"
}

# Call it as grep_sorted_values "${values[@]}"
calculate_sum_of_array () {
	local sum=0
    for num in "$@"
    do
        sum=$(echo $sum + $num | bc)
    done
    echo "$sum"
}

# Call it as calculate_avarage "${values[@]}"
calculate_avarage () {
	local avarage=$(echo scale=2\; $1 / $2 | bc)
	echo "$avarage"
}

# Call it as calculate_quantile_idx quantile_value length_of_array
calculate_quantile_idx () {
	local quantile=$(echo $2 \* $1 / 100 | bc)
	echo "$quantile"
}
