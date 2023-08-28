#!/bin/bash

# 批量,目前手动
#var=` find ./ -maxdepth 2 -name "*.yml"   -printf "docker-compose -f %p up -d; " `
#echo $var | sh


docker_username="findsoft@dows"
docker_password="findsoft123456"
docker_version=""
docker_registry="registry.cn-hangzhou.aliyuncs.com"

sudo echo -e "\033[32m --start-- \033[0m"

#sudo echo -e "\033[33m please input username: \033[0m"
#read -r docker_username
#sudo echo -e "\033[33m please input password: \033[0m"
#read -r docker_password
#sudo echo -e "\033[33m please input version: \033[0m"
#read -r docker_version
#sudo echo -e "\033[33m docker_username:${docker_username}  docker_password:${docker_password} docker_version:${docker_version} \033[0m"


sudo docker login --username=$docker_username --password=$docker_password ${docker_registry}

#sudo echo -e "\033[32m 1.pull image from route \033[0m"
#sudo docker pull "$docker_registry/findsoft/openjdk:11-jre-slim"
#sudo docker pull "$docker_registry/findsoft/odc-order:$docker_version"

#sudo echo -e "\033[32m 2.tag images \033[0m"
#sudo docker tag "$docker_registry/findsoft/openjdk:17-jre-slim" openjdk:11-jre-slim
#sudo docker tag "$docker_registry/findsoft/odc-order:$docker_version" odc-order:$docker_version

sudo echo -e "\033[32m 1.remove old tag images \033[0m"
## api
#sudo docker rmi -f "$docker_registry/findsoft/hep-admin-uat:1.0.230821"
## h5教师端
#sudo docker rmi -f "$docker_registry/findsoft/h5-hep-admin-uat:1.0.230826"
## h5学生端
#sudo docker rmi -f "$docker_registry/findsoft/h5-hep-user-uat:1.0.230826"

sudo docker images

#启动paas
sudo echo -e "\033[32m 2.running paas docker-compose  \033[0m"
sudo docker compose -f /findsoft/hep/paas/uat/mysql.yml up -d
sudo docker compose -f /findsoft/hep/paas/uat/redis.yml up -d
sudo docker compose -f /findsoft/hep/paas/uat/pdf.yml up -d

#启动saas
sudo echo -e "\033[32m 3.running saas docker-compose  \033[0m"
docker compose -f /findsoft/hep/saas/uat/api/admin/docker-compose.yml down
docker compose -f /findsoft/hep/saas/uat/h5/admin/docker-compose.yml down
docker compose -f /findsoft/hep/saas/uat/h5/user/docker-compose.yml down

docker compose -f /findsoft/hep/saas/uat/api/admin/docker-compose.yml up -d
docker compose -f /findsoft/hep/saas/uat/h5/admin/docker-compose.yml up -d
docker compose -f /findsoft/hep/saas/uat/h5/user/docker-compose.yml up -d

#查看
sudo docker ps -a

sudo echo -e "\033[32m --end-- \033[0m"
