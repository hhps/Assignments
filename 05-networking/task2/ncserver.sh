#!/bin/bash

load_file () {
    local file=""
    while read line
    do
        file=$file$line"\r\n"
    done <$1
    echo "$file"
}

fifo="/tmp/ncserver.fifo"
rm -f $fifo
mkfifo $fifo
echo "Server started with fifo file: $fifo"
trap "echo -e '\nServer stoped'; rm -f $fifo" EXIT

HELLO=$(load_file hello.html)
BAD_REQUEST=$(load_file bad-request.html)
END="`echo -ne \"\r\n\"`"
IFS=""

while true
do
    cat $fifo | nc -l 8080 | (
        i=0;
        while read request[$i] && [ "${request[$i]}" != "$END" ];
        do  
            i=$[i + 1]
        done
        
        url=$(echo ${request[0]} | cut -d " " -f2)
        protocol=$(echo ${request[0]} | tr -d "[\r\n]" | cut -d " " -f3)
        if [ "$url" == "/hello" ]
        then
            echo -ne "$protocol 200 OK\r\nDate: `date -R`\r\n$HELLO" > $fifo
        elif [ "$url" == "/echo" ]
        then
            echo -ne "${request[*]}" > $fifo
        else
            echo -ne "$protocol 400 Bad Request\r\nDate: `date -R`\r\n$BAD_REQUEST" > $fifo
        fi
    )
done

