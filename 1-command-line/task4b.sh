#!/bin/bash

# Calculate average response time and the median to resume with id=43 during current day.
# 
# Log file format:
# date time log_level request_type user_id url  response_code response_time
source functions.sh

#current_date=$(date +%Y-%m-%d)
current_date='2013-01-18'
echo "Current date: $current_date"

values=($(grep_sorted_values "$current_date .+ /resume?.*id=43.+" "$1"))
length="${#values[@]}"

if [ $length -gt 0 ]
then
    sum=$(calculate_sum_of_array "${values[@]}")

    avarage=$(calculate_avarage $sum $length)
    echo "Average response time: $avarage ms"

    quantile50=$(calculate_quantile_idx 50 $length)
    echo "The median response time: ${values[$quantile50]} ms"
else
    echo "No records match the search criteria"
fi
