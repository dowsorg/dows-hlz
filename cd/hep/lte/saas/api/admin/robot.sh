#!/bin/bash


#!/bin/bash

#分支名
BRANCH_NAME="$1"
#触发者
AUTHOR_NAME="$2"
#项目名
PROJECT_NAME="$3"
#描述
TITLE="$4"
#提交信息
COMMIT="$5"
CHANGES="$6"
#颜色
COLOR="$7"

echo "p5:"$COMMIT
echo "p6:"$CHANGES

IFS=',' read -ra arr <<< "$CHANGES"
result=""
for item in "${arr[@]}"; do
    result="$result$item"$'\n'
done


title='应用发布'
time="$(date "+%Y-%m-%d")"
times="$(date "+%H:%M:%S")"
xingqi="$(date "+%A")"
ip=$(ifconfig | grep inet | awk 'NR==3{print $2}')
lsblk=$(df -h / | awk '{print $5}' | tail -n 1 )
mem=$(free | grep Mem | awk '{print $3/$2 * 100.0}')
cpu=$(top -b -n1 | grep "Cpu(s)" | awk '{print $2}')
url="https://oapi.dingtalk.com/robot/send?access_token=936103586e804f6f8dd6eb648990851153826ab5d55037ae8fe3bd9469ee5631"


curl $url \
-H 'Content-Type: application/json' \
-d "{
    'msgtype': 'text',
    'text': {
        'content': '
          项目: $TITLE
          发布时间: $time $times $xingqi
          项目名: $PROJECT_NAME
          分支名: $BRANCH_NAME
          发布者: $AUTHOR_NAME
          COMMIT: $COMMIT
          CHANGES:
          $result
          HOST: $ip
          DISK: $lsblk
          MEM: $mem%
          CPU: $cpu%
        '
    }
}"


##分支名
#BRANCH_NAME=$1
##触发者
#AUTHOR_NAME=$2
##项目名
#PROJECT_NAME=$3
##描述
#DESC=$4
##颜色
#COLOR=$5
#
#title='应用发布监控'
#time="$(date "+%Y-%m-%d")"
#times="$(date "+%H:%M:%S")"
#xingqi="$(date "+%A")"
#ip=$(ifconfig | grep inet | awk 'NR==3{print $2}')
#lsblk=$(df -h / | awk '{print $5}' | tail -n 1 )
#mem=$(free | grep Mem | awk '{print $3/$2 * 100.0}')
#cpu=$(top -b -n1 | grep "Cpu(s)" | awk '{print $2}')
#url="https://oapi.dingtalk.com/robot/send?access_token=a108a939447601fcd4a884751203f35b187301a93c5dc880794ba1370f063f74"
#
#
#curl $url -H 'Content-Type: application/json' -d "{
#    'msgtype': 'markdown',
#    'markdown':{
#      'title':'应用发布监控',
#      'text':'
#        ******<font color=\"#0000FF\">${title}</font>******\n
#        **发布时间:** <font color=\"#0000FF\">${time} ${times} ${xingqi}</font>\n
#        **项目名:** <font  color=\"#FF0000\">${PROJECT_NAME}</font>\n
#        **分支名:** <font  color=\"#FF0000\">${BRANCH_NAME}</font>\n
#        **发布者:** <font  color=\"#FF0000\">${AUTHOR_NAME}</font>\n
#        **IP**: <font color=\"#0000FF\">${ip}</font>\n
#        **磁盘空间使用率:** <font color=\"#FF0000\">${lsblk}</font>\n
#        **内存使用率**: <font color=\"#FF0000\">${mem}%</font>\n
#        **CPU使用率**: <font color=\"#FF0000\">${cpu}%</font>\n
#      '
#    }
#}"