pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/local/jdk17'  // 指定 JDK 17 的路径
        MAVEN_HOME = '/usr/local/mvn/bin/mvn'  // 指定 Maven 的路径
        PATH = "${env.JAVA_HOME}/bin:${env.MAVEN_HOME}/bin:${env.PATH}"
        SAAS_PATH = '/dows/hep'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: '*', url: 'http://192.168.1.21/dows/dows-ops.git'
            }
        }
        stage('Build and Push Image') {
            steps {
                script {
                    // 获取分支名称 并用分割出版本号和名称
                    def branch = env.BRANCH_NAME.split('/')[1]
                    def rte = branch.split('-')[0]
                    def ver = branch.split('-')[1]
                    echo "=============build $rte-$ver=============="
                    // 根据分支名称的前缀判断不同的环境
                    if (branch.startsWith('dev-')) {
                        echo 'Building for development environment'
                        sh '''
                            /usr/local/mvn/bin/mvn -v
                            /usr/local/mvn/bin/mvn -Dmaven.test.skip=true clean package -U
                            docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com
                        '''
                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/dows-hep-dev:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/dows-hep-dev:$ver"
                        // 远程copy 文件
                        //sh "sshpass -p 'findsoft' scp saas/hep-admin/dev root@192.168.1.60:$SAAS_PATH"
                        // 在远程服务器上执行启动脚本
                        //sh 'sshpass -p "findsoft" user@192.168.1.60 "cd /dows/hep/saas/hep-admin/dev && docker-compose stop && docker compose up -d"'
                        // 本地copy并执行
                        sh "cp -r saas/hep-admin/dev $SAAS_PATH"
                        sh "cd /dows/hep/saas/hep-admin/dev && docker compose stop && docker compose up -d"
                    } else if (branch.startsWith('sit-')) {
                        echo 'Building for sit environment'
                        sh '''
                            /usr/local/mvn/bin/mvn -v
                            /usr/local/mvn/bin/mvn -Dmaven.test.skip=true clean package -U
                            docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com
                        '''
                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/dows-hep-sit:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/dows-hep-sit:$ver"
                        // 远程copy 文件
                        //sh "sshpass -p 'findsoft' scp saas/hep-admin/dev root@192.168.1.60:$SAAS_PATH"
                        // 在远程服务器上执行启动脚本
                        //sh 'sshpass -p "findsoft" user@192.168.1.60 "cd /dows/hep/saas/hep-admin/dev && docker-compose stop && docker compose up -d"'
                        // 本地copy并执行
                        sh "cp -r saas/hep-admin/dev $SAAS_PATH"
                        sh "cd /dows/hep/saas/hep-admin/dev && docker compose stop && docker compose up -d"
                    } else if (branch.startsWith('uat-')) {
                        echo 'Building for uat environment'
                        sh '''
                            /usr/local/mvn/bin/mvn -v
                            /usr/local/mvn/bin/mvn -Dmaven.test.skip=true clean package -U
                            docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com
                        '''
                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/dows-hep-uat:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/dows-hep-uat:$ver"
                        // 远程copy 文件
                        //sh "sshpass -p 'findsoft' scp saas/hep-admin/dev root@192.168.1.60:$SAAS_PATH"
                        // 在远程服务器上执行启动脚本
                        //sh 'sshpass -p "findsoft" user@192.168.1.60 "cd /dows/hep/saas/hep-admin/dev && docker-compose stop && docker compose up -d"'
                        // 本地copy并执行
                        sh "cp -r saas/hep-admin/dev $SAAS_PATH"
                        sh "cd /dows/hep/saas/hep-admin/dev && docker compose stop && docker compose up -d"
                    } else if (branch.startsWith('prd-')){
                        echo 'Building for production environment'
                        sh '''
                            /usr/local/mvn/bin/mvn -v
                            /usr/local/mvn/bin/mvn -Dmaven.test.skip=true clean package -U
                            docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com
                        '''
                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/dows-hep-prd:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/dows-hep-prd:$ver"
                        // 远程copy 文件
                        //sh "sshpass -p 'findsoft' scp saas/hep-admin/dev root@192.168.1.60:$SAAS_PATH"
                        // 在远程服务器上执行启动脚本
                        //sh 'sshpass -p "findsoft" user@192.168.1.60 "cd /dows/hep/saas/hep-admin/dev && docker-compose stop && docker compose up -d"'
                        // 本地copy并执行
                        sh "cp -r saas/hep-admin/dev $SAAS_PATH"
                        sh "cd /dows/hep/saas/hep-admin/dev && docker compose stop && docker compose up -d"
                    }
                }
            }
        }
    }
}