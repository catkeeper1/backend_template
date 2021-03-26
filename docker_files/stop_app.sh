#!/bin/bash


if [ -f sgs_pid.txt ] ;then
        pid="$(cat sgs_pid.txt)"

        kill $pid

        cmsThread=`ps -ef | grep $pid | grep -v "grep" | awk '{print $2}'`

        while [[ $cmsThread -eq $pid ]]
                do
                        sleep 1
                        cmsThread=`ps -ef | grep $pid | grep -v "grep" | awk '{print $2}'`
                done


fi
