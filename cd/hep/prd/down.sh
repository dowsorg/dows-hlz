#!/bin/bash

# 批量,目前手动
#var=` find ./ -maxdepth 2 -name "*.yml"   -printf "docker-compose -f %p up -d; " `
#echo $var | sh


docker_username="findsoft@dows"
docker_password="findsoft123456"
docker_registry="registry.cn-hangzhou.aliyuncs.com"
docker_images=("$docker_registry/findsoft/api-hep-admin-prd" "$docker_registry/findsoft/h5-hep-admin-prd" "$docker_registry/findsoft/h5-hep-user-prd")
docker_network="prd_net"


# Function to check if a container is running
check_container_running() {
    local container_name="$1"
    local running=$(docker inspect -f '{{.State.Running}}' "$container_name" 2>/dev/null)

    if [ "$running" = "true" ]; then
        return 0 # Container is running
    else
        return 1 # Container is not running
    fi
}

# Function to start or restart a container
start_or_restart_container() {
    local container_name="$1"
    local image_name="$2"

    if check_container_running "$container_name"-prd; then
        echo "Container '$container_name' is already running."
    else
        echo "Starting or restarting container '$container_name'..."
        docker compose -f ./paas/"$container_name".yml up  -d
        echo "Container '$container_name' started or restarted."
    fi
}

sudo echo -e "\033[32m --start-- \033[0m"

#手动输入
#sudo echo -e "\033[33m please input username: \033[0m"
#read -r docker_username
#sudo echo -e "\033[33m please input password: \033[0m"
#read -r docker_password
#sudo echo -e "\033[33m please input version: \033[0m"
#read -r docker_version
#sudo echo -e "\033[33m docker_username:${docker_username}  docker_password:${docker_password} docker_version:${docker_version} \033[0m"

#登录私仓(自动)
#sudo docker login --username=$docker_username --password=$docker_password ${docker_registry}

#拉取
#sudo echo -e "\033[32m 1.pull image from route \033[0m"
#sudo docker pull "$docker_registry/openjdk:11-jre-slim"
#sudo docker pull "$docker_registry/odc-order:$docker_version"
#
#sudo echo -e "\033[32m 2.tag images \033[0m"
#sudo docker tag "$docker_registry/openjdk:17-jre-slim" openjdk:11-jre-slim
#sudo docker tag "$docker_registry/odc-order:$docker_version" odc-order:$docker_version

sudo echo -e "\033[32m 1.remove old tag images \033[0m"
## todo脚本动态判断
## api
#sudo docker rmi -f "$docker_registry/hep-admin-uat:1.0.230821"
## h5教师端
#sudo docker rmi -f "$docker_registry/h5-hep-admin-uat:1.0.230826"
## h5学生端
#sudo docker rmi -f "$docker_registry/h5-hep-user-uat:1.0.230826"

sudo docker images

# 停止并移除
docker compose -f ./saas/api/admin/docker-compose.yml down
docker compose -f ./saas/h5/admin/docker-compose.yml down
docker compose -f ./saas/h5/user/docker-compose.yml down

### api 教师端
#docker rmi -f "$docker_registry/findsoft/api-hep-admin-uat:1.0.230821"
### h5教师端
#docker rmi -f "$docker_registry/findsoft/h5-hep-admin-uat:1.0.230826"
### h5学生端
#docker rmi -f "$docker_registry/findsoft/h5-hep-user-uat:1.0.230826"

#for di in "${docker_images[@]}"; do
#    if docker image inspect $di >/dev/null 2>&1; then
#        docker image rm $di
#        echo "......image '$di' deleted......"
#    else
#        echo "......image '$di' does not exist......"
#    fi
#done
#containerId=`docker images | grep hep-* | awk '{print $3}'`
#echo $containerId
#for str in $containerId
#do
#  docker rmi $str
#  echo "......deleted image '$str'......"
#done

#创建uat网络
#if docker network inspect $docker_network >/dev/null 2>&1; then
#    echo "Network '$docker_network' already exists."
#else
#    docker network create --driver bridge --subnet 172.18.0.0/16 --gateway 172.18.0.1 $docker_network
#    echo "Network '$docker_network' created."
#fi
#docker network rm uat_net
#docker network create --driver bridge --subnet 172.18.0.0/16 --gateway 172.18.0.1 uat_net

#启动paas
echo -e "\033[32m 2.running paas docker-compose  \033[0m"
#start_or_restart_container "mysql"
#start_or_restart_container "redis"
#start_or_restart_container "minio"
#start_or_restart_container "pdf"
#sudo docker compose -f ./paas/mysql.yml up  -d
#sudo docker compose -f ./paas/redis.yml up  -d
#sudo docker compose -f ./paas/minio.yml up  -d
#sudo docker compose -f ./paas/pdf.yml up -d

echo -e "\033[32m 3.running saas docker-compose  \033[0m"
#启动saas
#docker compose -f ./saas/api/admin/docker-compose.yml up -d
#docker compose -f ./saas/h5/user/docker-compose.yml up -d
#docker compose -f ./saas/h5/admin/docker-compose.yml up -d


#查看
sudo docker ps -a

sudo echo -e "\033[32m --end-- \033[0m"
