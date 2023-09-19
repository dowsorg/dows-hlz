
def detect_branch() {
    def RESULT = sh(returnStdout: true, script: '''
        git for-each-ref --sort=-committerdate --format="%(refname:short)" refs/remotes/ | head -n 1
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

        FORM_LTE_CD_PATH = 'cd/hep/lte/saas/api/admin'
        TO_LTE_CD_PATH = '/findsoft/hep/lte/saas/api'

        FORM_DEV_CD_PATH = 'cd/hep/dev/saas/api/admin'
        TO_DEV_CD_PATH = '/findsoft/hep/dev/saas/api'

        FORM_SIT_CD_PATH = 'cd/hep/sit/saas/api/admin'
        TO_SIT_CD_PATH = '/findsoft/hep/sit/saas/api'

        FORM_UAT_CD_PATH = 'cd/hep/uat/saas/api/admin'
        TO_UAT_CD_PATH = '/findsoft/hep/uat/saas/api'

        FORM_PRD_CD_PATH = 'cd/hep/prd/saas/api/admin'
        TO_PRD_CD_PATH = '/findsoft/hep/prd/saas/api'

        DOCKER_OFFLINE_LOGIN = 'docker login --username=admin --password=findsoft_harbor http://192.168.1.60:7080'
        DOCKER_OFFLINE_BUILD = 'docker build . --file Dockerfile -t 192.168.1.60:7080/hep/api-hep-admin'
        DOCKER_OFFLINE_PUSH = 'docker push 192.168.1.60:7080/hep/api-hep-admin'

        DOCKER_ONLINE_LOGIN = 'docker login --username=findsoft@dows --password=findsoft123456 registry.cn-hangzhou.aliyuncs.com'
        DOCKER_ONLINE_BUILD  = 'docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/api-hep-admin'
        DOCKER_ONLINE_PUSH  = 'docker push registry.cn-hangzhou.aliyuncs.com/findsoft/api-hep-admin'

        DOCKER_CONTAINER_START = "docker compose down && docker compose up -d"

        OFFLINE_AS_HOST='192.168.1.60'
        OFFLINE_AS_USERNAME='root'
        OFFLINE_AS_PWD='findsoft2022!@#'

        ONLINE_AS_HOST='139.186.208.204'
        ONLINE_AS_USERNAME='root'
        ONLINE_AS_PWD='Findsoft20232023'

        BRANCH="${env.BRANCH_NAME.split('/')[1]}"
        RTE="${BRANCH.split('-')[0]}"
        VER="${BRANCH.split('-')[1]}"
    }

    stages {
        stage('CI&&CD') {
            steps {
                script {
                    def branch = detect_branch()
                    echo  '===================================='
                    echo  "         当前分支为:$branch           "
                    echo  '===================================='
                    def rte = branch.split('-')[0]
                    def ver = branch.split('-')[1]

                    step([$class: 'WsCleanup'])

                    checkout([$class: 'GitSCM',
                        branches: [[name: "$branch"]],
                        //extensions: [],
                        extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: '']],
                        userRemoteConfigs: [[
                            credentialsId: 'dows-gitlab',
                            url: 'http://192.168.1.21/dows/dows-hep.git'
                        ]]
                    ])

                    def changes = getChangedFilesList()
                    println ("文件变更列表: " + changes)

                    def gitCommitId = getGitcommitID()
                    println("CommitID: " + gitCommitID)

                    def gitCommitAuthorName = getAuthorName()
                    println("提交人: " + gitCommitAuthorName)

                    def gitCommitMessage = getCommitMessage()
                    println("提交信息: " + gitCommitMessage)

                    sh '''
                        /usr/local/mvn/bin/mvn -v
                        /usr/local/mvn/bin/mvn -Dmaven.test.skip=true clean package -U
                    '''
                    if (branch.startsWith('lte-')) {
                        echo "Building for sit environment for $branch"
                        sh "$DOCKER_OFFLINE_LOGIN"
                        sh "docker build . --file Dockerfile -t 192.168.1.60:7080/hep/api-hep-admin-lte:$ver"
                        sh "docker push 192.168.1.60:7080/hep/api-hep-admin-lte:$ver"

                        sh "sshpass -p $OFFLINE_AS_PWD ssh -o StrictHostKeyChecking=no $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST mkdir -p $TO_LTE_CD_PATH"
                        sh "sshpass -p $OFFLINE_AS_PWD scp -r $FORM_LTE_CD_PATH $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST:$TO_LTE_CD_PATH"
                        sh "sshpass -p $OFFLINE_AS_PWD ssh $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST 'cd $TO_LTE_CD_PATH/admin;$DOCKER_OFFLINE_LOGIN;$DOCKER_CONTAINER_START'"

                        sh "sshpass -p $OFFLINE_AS_PWD ssh $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST sh $TO_LTE_CD_PATH/admin/robot.sh '"$branch" "$gitCommitAuthorName" "api-hep-admin" "LTE环境发布" "$gitCommitMessage" "$changes" "green"'"

                    } else if (branch.startsWith('dev-')) {
                        echo "Building for development environment for ${branch}"
                        sh "$DOCKER_OFFLINE_LOGIN"
                        sh "$DOCKER_OFFLINE_BUILD'-dev':$ver"
                        sh "$DOCKER_OFFLINE_PUSH'-dev':$ver"

                        sh "sshpass -p $OFFLINE_AS_PWD ssh -o StrictHostKeyChecking=no $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST mkdir -p $TO_DEV_CD_PATH"
                        sh "sshpass -p $OFFLINE_AS_PWD scp -r $FORM_DEV_CD_PATH $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST:$TO_DEV_CD_PATH"
                        sh "sshpass -p $OFFLINE_AS_PWD ssh $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST 'cd $TO_DEV_CD_PATH/admin;$DOCKER_OFFLINE_LOGIN;$DOCKER_CONTAINER_START'"

                        sh "sshpass -p $OFFLINE_AS_PWD ssh $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST sh $TO_DEV_CD_PATH/admin/robot.sh '\"$branch\"' \"$gitCommitAuthorName\" 'api-hep-admin' 'DEV环境发布' '\"$gitCommitMessage\"' '\"$changes\"' 'green'"

                    } else if (branch.startsWith('sit-')) {
                        echo "Building for sit environment for $branch"

                        sh "$DOCKER_OFFLINE_LOGIN"
                        sh "$DOCKER_OFFLINE_BUILD'-sit':$ver"
                        sh "$DOCKER_OFFLINE_PUSH'-sit':$ver"

                        sh "sshpass -p $OFFLINE_AS_PWD ssh -o StrictHostKeyChecking=no $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST mkdir -p $TO_SIT_CD_PATH"
                        sh "sshpass -p $OFFLINE_AS_PWD scp -r $FORM_SIT_CD_PATH $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST:$TO_SIT_CD_PATH"
                        sh "sshpass -p $OFFLINE_AS_PWD ssh $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST 'cd $TO_SIT_CD_PATH/admin;$DOCKER_OFFLINE_LOGIN;$DOCKER_CONTAINER_START'"

                        sh "sshpass -p $OFFLINE_AS_USERNAME ssh $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST sh $TO_SIT_CD_PATH/admin/robot.sh '\"$branch\"' \"$gitCommitAuthorName\" 'api-hep-admin' 'SIT环境发布' '\"$gitCommitMessage\"' '\"$changes\"' 'green'"
                        //sh "sshpass -p $AS_PWD ssh $AS_USERNAME@$AS_HOST sh $TO_SIT_CD_PATH/admin/robot.sh '"$branch"' '"$gitCommitAuthorName"' 'api-hep-admin' 'SIT环境发布' '"$gitCommitMessage"' '"$changes"' 'green'"

                    } else if (branch.startsWith('uat-')) {
                        echo "Building for uat environment for ${branch}"

                        sh "$DOCKER_OFFLINE_LOGIN"
                        sh "$DOCKER_OFFLINE_BUILD'-uat':$ver"
                        sh "$DOCKER_OFFLINE_PUSH'-uat':$ver"

                        sh "sshpass -p $OFFLINE_AS_PWD ssh -o StrictHostKeyChecking=no $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST mkdir -p $TO_UAT_CD_PATH"
                        sh "sshpass -p $OFFLINE_AS_PWD scp -r $FORM_UAT_CD_PATH $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST:$TO_UAT_CD_PATH"
                        sh "sshpass -p $OFFLINE_AS_PWD ssh $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST 'cd $TO_UAT_CD_PATH/admin;$DOCKER_OFFLINE_LOGIN;$DOCKER_CONTAINER_START'"

                        sh "sshpass -p $OFFLINE_AS_USERNAME ssh $OFFLINE_AS_USERNAME@$OFFLINE_AS_HOST sh $TO_UAT_CD_PATH/admin/robot.sh '\"$branch\"' \"$gitCommitAuthorName\" 'api-hep-admin' 'UAT环境发布' '\"$gitCommitMessage\"' '\"$changes\"' 'green'"
                        //sh "sshpass -p $AS_PWD ssh $AS_USERNAME@$AS_HOST sh $TO_UAT_CD_PATH/admin/robot.sh '$branch' '$gitCommitAuthorName' 'api-hep-admin' 'UAT环境发布' '$gitCommitMessage' '$changes' 'green'"
                        //sh "sshpass -p $AS_PWD ssh $AS_USERNAME@$AS_HOST sh $TO_UAT_CD_PATH/admin/robot.sh '\"${branch}\"' '\"${gitCommitAuthorName}\"' 'api-hep-admin' 'UAT环境构建、打包、传输成功' 'green' '\"${gitCommitMessage}\"'"
                    } else if (branch.startsWith('prd-')){
                        echo "Building for production environment for ${branch}"
                        // 只做推送处理
                        sh "$DOCKER_ONLINE_LOGIN"
                        sh "$DOCKER_ONLINE_BUILD'-prd':$ver"
                        sh "$DOCKER_ONLINE_PUSH'-prd':$ver"

                        //sh "docker build . --file Dockerfile -t registry.cn-hangzhou.aliyuncs.com/findsoft/api-hep-admin-prd:$ver"
                        //sh "docker push registry.cn-hangzhou.aliyuncs.com/findsoft/api-hep-admin-prd:$ver"

                        //sh "sshpass -p $PRD_AS_PWD ssh -o StrictHostKeyChecking=no $PRD_AS_USERNAME@$PRD_AS_HOST mkdir -p $TO_PRD_CD_PATH"
                        //sh "sshpass -p $PRD_AS_PWD scp -r $FORM_PRD_CD_PATH $AS_USERNAME@$AS_HOST:$TO_PRD_CD_PATH"
                        //sh "sshpass -p $PRD_AS_PWD ssh $PRD_AS_USERNAME@$PRD_AS_HOST 'cd $TO_PRD_CD_PATH/admin;$LOGIN_ONLINE_DOCKER;$DOCKER_CONTAINER_START'"

                        //sh "sshpass -p $AS_PWD ssh $AS_USERNAME@$AS_HOST sh $TO_PRD_CD_PATH/admin/robot.sh '\"$branch\"' \"$gitCommitAuthorName\" 'api-hep-admin' 'PRD环境发布' '\"$gitCommitMessage\"' '\"$changes\"' 'green'"
                        //sh "sshpass -p $AS_PWD ssh $AS_USERNAME@$AS_HOST sh $TO_PRD_CD_PATH/admin/robot.sh '$branch' '$gitCommitAuthorName' 'api-hep-admin' 'PRD环境发布' '$gitCommitMessage' '$changes' 'green'"
                        //sh "sshpass -p $AS_PWD ssh $AS_USERNAME@$AS_HOST sh $TO_PRD_CD_PATH/admin/robot.sh '\"${branch}\"' '\"${gitCommitAuthorName}\"' 'api-hep-admin' 'PRD环境构建、打包、传输成功' 'green' '\"${gitCommitMessage}\"'"
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