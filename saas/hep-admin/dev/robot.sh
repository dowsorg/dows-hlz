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

curl --location 'https://oapi.dingtalk.com/robot/send?access_token=a108a939447601fcd4a884751203f35b187301a93c5dc880794ba1370f063f74' \
--header 'Content-Type: application/json' \
--data '
   {
       "msgtype": "markdown",
       "markdown": {
           "content": "测试环境\n
            >项目名: <font color=\"comment\">'"$PROJECT_NAME"'</font>
            >分支名: <font color=\"comment\">'"$BRANCH_NAME"'</font>
            >触发者: <font color=\"comment\">'"$AUTHOR_NAME"'</font>
            >描述: <font color='"$COLOR"'>'"$DESC"'</font>"
       }
   }'

