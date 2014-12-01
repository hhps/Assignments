#!/bin/bash

# Construct a graph of 95% quantile of response time during current day for three URLs: /resume /vacancy /user.
# Each URL should has it's own color.
# 
# Log file format:
# date time log_level request_type user_id url  response_code response_time
source functions.sh

#current_date=$(date +%Y-%m-%d)
current_date='2013-01-18'
echo "Current date: $current_date"

# Call it as calculate_quantile URL log_file
calculate_quantile () {
    local values=($(grep_sorted_values "$current_date .+ /"$1".+" "$2"))
    local length="${#values[@]}"
    local quantile=0
    if [ $length -gt 0 ]
    then
        local quantile_idx=$(calculate_quantile_idx 95 $length)
        quantile="${values[$quantile_idx]}"
    fi
    echo "$quantile"
}

resume_quantile=$(calculate_quantile "resume" $1)
echo "Resume quantile: $resume_quantile ms"

vacancy_quantile=$(calculate_quantile "vacancy" $1)
echo "Vacancy quantile: $vacancy_quantile ms"

user_quantile=$(calculate_quantile "user" $1)
echo "User quantile: $user_quantile ms"

# Draw diagramm
echo -e "0 /resume $resume_quantile\n1 /vacancy $vacancy_quantile\n2 /user $user_quantile" > timings.dat

plot="set term png"
plot="$plot; set output 'diagramm.png'"
plot="$plot; set title 'Quantile 95%'"
plot="$plot; set xlabel 'URL'"
plot="$plot; set style line 1 lc rgb 'red'"
plot="$plot; set style line 2 lc rgb 'green'"
plot="$plot; set style line 3 lc rgb 'blue'"
plot="$plot; set ylabel 'Time (ms)'"
plot="$plot; set style fill solid"
plot="$plot; set boxwidth 0.5"

plot="$plot; plot 'timings.dat' every ::0::0 using 1:3:xtic(2) notitle with boxes ls 1,"
plot="$plot 'timings.dat' every ::1::1 using 1:3:xtic(2) notitle with boxes ls 2,"
plot="$plot 'timings.dat' every ::2::2 using 1:3:xtic(2) notitle with boxes ls 3"

gnuplot -p -e "$plot"
echo "See results in files 'timings.dat' and 'diagramm.png'"
