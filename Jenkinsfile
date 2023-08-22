
def detect_branch() {
    def RESULT = sh(returnStdout: true, script: '''
        for branch in `git branch -r | grep -v HEAD`; do echo -e `git show --format="%ci %cr" $branch | head -n 1` "\\t" $branch; done | sort -r |head -n 1 |awk \'{print $NF}\'
    ''')
    def content = "RESULT=$RESULT\n"
    RESULT=sh(returnStdout: true, script: content+'echo $RESULT|sed "s#origin/##g"') // 删除 origin
    return RESULT
}

pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/local/jdk17'
        MAVEN_HOME = '/usr/local/mvn/bin/mvn'
        PATH = "${env.JAVA_HOME}/bin:${env.MAVEN_HOME}/bin:${env.PATH}"
        SAAS_PATH = '/dows/saas/hep-admin'
        AS_HOST='192.168.1.60'
        AS_USERNAME='root'
        AS_PWD='findsoft2022!@#'
    }

    stages {
        stage('CI&&CD') {
            steps {
                script {
                    def branch = detect_branch()
                    def rte = branch.split('-')[0]
                    def ver = branch.split('-')[1]

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

                    List<String> changes = getChangedFilesList()
                    println ("文件变更列表: " + changes)

                    String gitCommitId = getGitcommitID()
                    println("CommitID: " + gitCommitID)

                    String gitCommitAuthorName = getAuthorName()
                    println("提交人: " + gitCommitAuthorName)

                    String gitCommitMessage = getCommitMessage()
                    println("提交信息: " + gitCommitMessage)

                    sh '''
                        /usr/local/mvn/bin/mvn -v
                        /usr/local/mvn/bin/mvn -Dmaven.test.skip=true clean package -U
                        docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com
                    '''

                    if (branch.startsWith('dev-')) {
                        echo "Building for development environment for ${branch}"

                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-dev:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-dev:$ver"

                        sh 'sshpass -p "$AS_PWD" ssh -o StrictHostKeyChecking=no "$AS_USERNAME"@"$AS_HOST" "mkdir -p $SAAS_PATH/dev"'
                        sh 'sshpass -p "$AS_PWD" scp -r saas/hep-admin/dev "$AS_USERNAME"@"$AS_HOST":"$SAAS_PATH"'
                        sh 'sshpass -p "$AS_PWD" ssh "$AS_USERNAME"@"$AS_HOST" "cd $SAAS_PATH/dev;sudo docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com;docker compose stop && docker compose up -d"'
                        // 通知
                        sh '''
                            sshpass -p $AS_PWD ssh $AS_USERNAME@$AS_HOST "sh $SAAS_PATH/dev/robot.sh $branch $gitCommitAuthorName hep-admin-dev 'dev环境构建、打包、传输成功'" 'green'
                        '''

                    } else if (branch.startsWith('sit-')) {
                        echo 'Building for sit environment for ${branch}'

                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-sit:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-sit:$ver"

                        sh 'sshpass -p "findsoft2022!@#" ssh -o StrictHostKeyChecking=no root@192.168.1.60 "mkdir -p $SAAS_PATH/sit"'
                        sh "sshpass -p 'findsoft2022!@#' scp -r saas/hep-admin/sit root@192.168.1.60:$SAAS_PATH"
                        sh 'sshpass -p "findsoft2022!@#" ssh root@192.168.1.60 "cd $SAAS_PATH/sit && docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com && docker compose stop && docker compose up -d"'

                    } else if (branch.startsWith('uat-')) {
                        echo 'Building for uat environment for ${branch}'

                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-uat:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-uat:$ver"

                        sh 'sshpass -p "findsoft2022!@#" ssh -o StrictHostKeyChecking=no root@192.168.1.60 "mkdir -p $SAAS_PATH/uat"'
                        sh "sshpass -p 'findsoft2022!@#' scp -r saas/hep-admin/uat root@192.168.1.60:$SAAS_PATH"
                        sh 'sshpass -p "findsoft2022!@#" ssh root@192.168.1.60 "cd $SAAS_PATH/uat && docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com && docker compose stop && docker compose up -d"'
                    } else if (branch.startsWith('prd-')){
                        echo 'Building for production environment for ${branch}'

                        sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-prd:$ver"
                        sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/hep-admin-prd:$ver"

                        sh 'sshpass -p "findsoft2022!@#" ssh -o StrictHostKeyChecking=no root@192.168.1.60 "mkdir -p $SAAS_PATH/prd"'
                        sh "sshpass -p 'findsoft2022!@#' scp -r saas/hep-admin/prd root@192.168.1.60:$SAAS_PATH"
                        sh 'sshpass -p "findsoft2022!@#" ssh root@192.168.1.60 "cd $SAAS_PATH/prd && docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com && docker compose stop && docker compose up -d"'
                    }
                }
            }
        }
    }
}


//获取变更文件列表，返回HashSet，注意添加的影响文件路径不含仓库目录名
@NonCPS
List<String> getChangedFilesList(){
    def changedFiles = []
    for ( changeLogSet in currentBuild.changeSets){
        for (entry in changeLogSet.getItems()){
            changedFiles.addAll(entry.affectedPaths)
        }
    }
    return changedFiles
}

// 获取提交ID
@NonCPS
String getGitcommitID(){
    gitCommitID = " "
    for ( changeLogSet in currentBuild.changeSets){
        for (entry in changeLogSet.getItems()){
            gitCommitID = entry.commitId
        }
    }
    return gitCommitID
}

// 获取提交人
@NonCPS
String getAuthorName(){
    gitAuthorName = " "
    for ( changeLogSet in currentBuild.changeSets){
        for (entry in changeLogSet.getItems()){
            gitAuthorName = entry.author.fullName
        }
    }
    return gitAuthorName
}

// 获取提交信息
@NonCPS
String getCommitMessage(){
    commitMessage = " "
    for ( changeLogSet in currentBuild.changeSets){
        for (entry in changeLogSet.getItems()){
            commitMessage = entry.msg
        }
    }
    return commitMessage
}