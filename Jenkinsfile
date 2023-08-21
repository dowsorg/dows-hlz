
def detect_branch() {
    def RESULT = sh(returnStdout: true, script: '''
        cd src
        for branch in `git branch -r | grep -v HEAD`; do echo -e `git show --format="%ci %cr" $branch | head -n 1` "\\t" $branch; done | sort -r |head -n 1 |awk \'{print $NF}\'
    ''') // 获取分支名如：origin/develop
    def content = "RESULT=$RESULT\n"
    RESULT=sh(returnStdout: true, script: content+'echo $RESULT|sed "s#origin/##g"') // 删除 origin
    return RESULT
}

pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/local/jdk17'  // 指定 JDK 17 的路径
        MAVEN_HOME = '/usr/local/mvn/bin/mvn'  // 指定 Maven 的路径
        PATH = "${env.JAVA_HOME}/bin:${env.MAVEN_HOME}/bin:${env.PATH}"
        SAAS_PATH = '/dows/saas/hep-admin'
        BRANCH="${env.BRANCH_NAME.split('/')[1]}"
        RTE="${BRANCH.split('-')[0]}"
        VER="${BRANCH.split('-')[1]}"
    }

    stages {

        stage('CI-CD') {
            steps {


                script {
                    // 获取分支名称 并用分割出版本号和名称
                    def branch = detect_branch()
                    def rte = branch.split('-')[0]
                    def ver = branch.split('-')[1]

                    // 根据分支名称的前缀判断不同的环境
                    if (branch.startsWith('dev-')) {
                        echo "Building for development environment for ${branch}"
                        //清理空间
                        step([$class: 'WsCleanup'])
                        checkout([$class: 'GitSCM',
                            branches: [[name: "$branch"]],
                            //extensions: [],
                            extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: '']],// 下载代码放到 ${WORKSPACE}/ 中
                            userRemoteConfigs: [[
                                credentialsId: 'dows-gitlab', // credentialsId 在jenkins 凭据管理处获得
                                url: 'http://192.168.1.21/dows/dows-hep.git' // gitlab链接
                            ]]
                        ])
                        sh '''
                            /usr/local/mvn/bin/mvn -v
                            /usr/local/mvn/bin/mvn -Dmaven.test.skip=true clean package -U
                            docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com
                        '''
                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-dev:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-dev:$ver"

                        sh 'sshpass -p "findsoft2022!@#" ssh -o StrictHostKeyChecking=no root@192.168.1.60 "mkdir -p $SAAS_PATH/dev"'
                        sh "sshpass -p 'findsoft2022!@#' scp -r saas/ops-admin/dev root@192.168.1.60:$SAAS_PATH"
                        // 在远程服务器上执行启动脚本
                        sh 'sshpass -p "findsoft2022!@#" ssh root@192.168.1.60 "cd $SAAS_PATH/dev;sudo docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com;docker compose stop && docker compose up -d"'
                        // 本地copy并执行
                        //sh "cp -r saas/ops-admin/dev $SAAS_PATH/ops-admin/dev"
                        //sh "cd /dows/hep/saas/hep-admin/dev && docker compose stop && docker compose up -d"
                    } else if (branch.startsWith('sit-')) {
                        echo 'Building for sit environment'
                        sh '''
                            /usr/local/mvn/bin/mvn -v
                            /usr/local/mvn/bin/mvn -Dmaven.test.skip=true clean package -U
                            docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com
                        '''
                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-sit:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-sit:$ver"
                        // 远程copy 文件
                        sh 'sshpass -p "findsoft2022!@#" ssh -o StrictHostKeyChecking=no root@192.168.1.60 "mkdir -p $SAAS_PATH/sit"'
                        sh "sshpass -p 'findsoft2022!@#' scp -r saas/hep-admin/sit root@192.168.1.60:$SAAS_PATH"
                        sh 'sshpass -p "findsoft2022!@#" ssh root@192.168.1.60 "cd $SAAS_PATH/sit && docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com && docker compose stop && docker compose up -d"'
                        // 本地copy并执行
                        //sh "cp -r saas/hep-admin/dev $SAAS_PATH"
                        //sh "cd /dows/hep/saas/hep-admin/dev && docker compose stop && docker compose up -d"
                    } else if (branch.startsWith('uat-')) {
                        echo 'Building for uat environment'
                        sh '''
                            /usr/local/mvn/bin/mvn -v
                            /usr/local/mvn/bin/mvn -Dmaven.test.skip=true clean package -U
                            docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com
                        '''
                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-uat:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-uat:$ver"
                        // 远程copy 文件
                        sh 'sshpass -p "findsoft2022!@#" ssh -o StrictHostKeyChecking=no root@192.168.1.60 "mkdir -p $SAAS_PATH/uat"'
                        sh "sshpass -p 'findsoft2022!@#' scp -r saas/hep-admin/uat root@192.168.1.60:$SAAS_PATH"
                        sh 'sshpass -p "findsoft2022!@#" ssh root@192.168.1.60 "cd $SAAS_PATH/uat && docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com && docker compose stop && docker compose up -d"'
                    } else if (branch.startsWith('prd-')){
                        echo 'Building for production environment'
                        sh '''
                            /usr/local/mvn/bin/mvn -v
                            /usr/local/mvn/bin/mvn -Dmaven.test.skip=true clean package -U
                            docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com
                        '''
                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-prd:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-prd:$ver"
                        // 远程copy 文件
                        sh 'sshpass -p "findsoft2022!@#" ssh -o StrictHostKeyChecking=no root@192.168.1.60 "mkdir -p $SAAS_PATH/prd"'
                        sh "sshpass -p 'findsoft2022!@#' scp -r saas/hep-admin/prd root@192.168.1.60:$SAAS_PATH"
                        sh 'sshpass -p "findsoft2022!@#" ssh root@192.168.1.60 "cd $SAAS_PATH/prd && docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com && docker compose stop && docker compose up -d"'
                    }
                }
            }
        }
    }
}