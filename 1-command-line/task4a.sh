#!/bin/bash
 
# Calculate the total time of successful requests to URL /resume from 12pm to 1pm.
# Calculate average response time, 95% quantile, 99% quantile to this URL.
# 
# Log file format:
# date time log_level request_type user_id url  response_code response_time
source functions.sh

values=($(grep_sorted_values "^(.{10}) [12|13].+ /resume.* 200 .+" "$1"))
length="${#values[@]}"
 
if [ $length -gt 0 ]
then
    sum=$(calculate_sum_of_array "${values[@]}")
    echo "The total time of successful requests: $sum ms"
 
    avarage=$(calculate_avarage $sum $length)
    echo "Average response time: $avarage ms"
 
    quantile95=$(calculate_quantile_idx 95 $length)
    echo "95% quantile of response time: ${values[$quantile95]} ms"
 
    quantile99=$(calculate_quantile_idx 99 $length)
    echo "99% quantile of response time: ${values[$quantile99]} ms"
else
	echo "No records match the search criteria"
fi
