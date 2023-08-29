#!/bin/bash

#分支名
BRANCH_NAME=$1
#触发者
AUTHOR_NAME=$2
#项目名
PROJECT_NAME=$3
#描述
DESC=$4
#颜色
COLOR=$5

title='应用发布监控'
time="$(date "+%Y-%m-%d")"
times="$(date "+%H:%M:%S")"
xingqi="$(date "+%A")"
ip=$(ifconfig | grep inet | awk 'NR==3{print $2}')
lsblk=$(df -h / | awk '{print $5}' | tail -n 1 )
mem=$(free | grep Mem | awk '{print $3/$2 * 100.0}')
cpu=$(top -b -n1 | grep "Cpu(s)" | awk '{print $2}')
url="https://oapi.dingtalk.com/robot/send?access_token=078c2fbb8c787e37d392e10470e18e7db0a10053145a4b90770fdfc7ae646257"


curl $url -H 'Content-Type: application/json' -d "{
    'msgtype': 'markdown',
    'markdown':{
      'title':'应用发布监控',
      'text':'
        ******${title}******\n
        **发布时间:** ${time} ${times} ${xingqi}\n
        **项目名:** ${PROJECT_NAME}\n
        **分支名:** ${BRANCH_NAME}\n
        **发布者:** ${AUTHOR_NAME}\n
        **IP**: ${ip}\n
        **磁盘空间使用率:** ${lsblk}\n
        **内存使用率**: ${mem}%\n
        **CPU使用率**: ${cpu}%\n
      '
    }
}"