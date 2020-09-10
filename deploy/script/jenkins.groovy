def mvn(args) {
    def mvnHome = tool 'Maven_3.3.9'
    sh "${mvnHome}/bin/mvn ${args} -DBUILD_NUMBER=${env.BUILD_NUMBER} -DBUILD_URL=${env.BUILD_URL} -DBRANCH_NAME=${env.BRANCH_NAME}"
}

def buildDockerImage() {
    stage name: 'Build Docker Image', concurrency: 1
    echo '开始构建Docker镜像'
    echo "tag: ${env.BUILD_TAG}"
    docker.withRegistry("https://" + dockerRegistryDomain, dockerRegistryCredentialId) {
        def dockerImage = docker.build "${dockerRegistryDomain}/xingshulin/${appName}:${env.BRANCH_NAME}.build_${env.BUILD_NUMBER}"
        echo '开始推送Docker镜像'
        dockerImage.push()
    }
    sh "docker rmi ${dockerRegistryDomain}/xingshulin/${appName}:${env.BRANCH_NAME}.build_${env.BUILD_NUMBER}"
}

def deploy(environment, hostIp, ports = ["8080:8080"], hostUser = 'suoper') {
    if (!hostIp) {
        error "hostIp not set"
    }

    if ("${env.BRANCH_NAME}" != "master" && environment in ["prod", "production", "online"]) {
        throw new IllegalStateException("非主干分支不允许部署至线上环境");
    }

    groovy.lang.GString dockerComposeFileContent = """
    version: "3"
    services:
      form:
        image: ${dockerRegistryDomain}/xingshulin/${appName}:${env.BRANCH_NAME}.build_${env.BUILD_NUMBER}
        ports:
          - ${ports.join('\n      - ')}
        labels:
          - group:task
        network_mode: "bridge"
        volumes:
          - "/logs/${appName}:/logs/${appName}"
        restart: always
        container_name: ${appName}
        environment:
          SPRING_PROFILES_ACTIVE: ${environment}
          HOST_IP: ${hostIp}
          TZ: "Asia/Shanghai"
          LANG: zh_CN.UTF-8
          APP_NAME: ${appName}
        """

    def deployShell = "docker login --username=docker-image@xingshulin --password=adBxKaEst2 https://registry-vpc.cn-beijing.aliyuncs.com;" +
            "docker-compose -f /apps/${appName}/docker-compose.yml up -d;"

    sh """
       alias ssh="ssh -i ~/.ssh/id_rsa_aliyun -o StrictHostKeyChecking=no"
       ssh ${hostUser}@${hostIp} 'mkdir -p /apps/${appName} || true'
       echo '${dockerComposeFileContent}' | ssh ${hostUser}@${hostIp}  "cat > /apps/${appName}/docker-compose.yml"
       ssh ${hostUser}@${hostIp} '${deployShell}'
      """
    echo "${appName} 部署至${environment}-${hostIp}完成 "
}

def testCase(environment) {
    sh "curl -X GET 'http://jenkins.xingshulin.com/job/AutomationInterfaceTest_Medclips/job/MedChart_TaskCenter/buildWithParameters?token=123456&ServerAddr=" + environment.toUpperCase() + "'"
    echo "${environment}测试用例执行完成"
}


this