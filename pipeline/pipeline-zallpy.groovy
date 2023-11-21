node {
    jdk = tool name: 'JDK11'
    env.JAVA_HOME = "${jdk}"

    echo "jdk installation path is: ${jdk}"


    def project_name = "${project_name}"
    def path_to_pom = "${path_to_pom}"
    def java_home_path = "${java_home_path}"
    def module_name = path_to_pom.tokenize('/').last()
    def git_repository = "${git_repository}"
    def git_branch = "${git_branch}"
    def user = "${user}"
    def git_credentials = "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" 

    echo "############################################################################"
    echo 'BUILD DO ARTEFATO: ' + project_name
    echo "############################################################################"

    echo "############################################################################"
    echo "### Variáveis de Ambiente ###"
    echo "Nome do projeto " + project_name
    echo "Repositório GIT: " + git_repository
    echo "Branch do GIT: " + git_branch
    echo "Usuário: " + user
    echo "Path do pom.xml: " + path_to_pom
    echo "JAVA_HOME utilizado: " + java_home_path
    echo "############################################################################"

    stage("Obtendo código fonte do GIT"){
        echo "############################################################################"
        echo "### Obtendo código fonte do Git ###"

        git([url: git_repository, branch: git_branch, credentialsId: git_credentials])

        echo "############################################################################"
    }

    stage('Build artifact'){
     sh(script: "mv source/* .; rm -rf source", returnStdout: true)
        version = sh(script: '''cat ${project_name}/pom.xml | grep -A1 -E "(<artifactId>)($project_name)(<\\/artifactId>)" | sed -n 's/<version>\\(.*\\)<\\/version>/\\1/p' ''', returnStdout: true)
    
        if(version.contains("SNAPSHOT")){
            goals = "clean install -U -Dmaven.test.skip=true"
        } else {
            goals = "clean install deploy -U -Dmaven.test.skip=true"
        }
    
        echo 'Projeto avaliado: ' + project_name
        
        def path_to_pom = "$JENKINS_HOME/workspace/Generic/$current_build_folder/" + project_name
        def java_version = sh(script: "grep -E \"<java.version>(.*)</java.version>\" ${path_to_pom}/pom.xml", returnStdout: true)
        def java_home_path = "/var/lib/jenkins/tools/hudson.model.JDK/JDK_1.8"

        if (java_version.contains("11")) {
            java_home_path = "/var/lib/jenkins/tools/hudson.model.JDK/jdk11/jdk-11"
        } else if (java_version.contains("1.8")) {
            java_home_path = "/var/lib/jenkins/tools/hudson.model.JDK/JDK_1.8"
        } else if (java_version.contains("14")) {
            java_home_path = "/var/lib/jenkins/tools/hudson.model.JDK/JDK-14"
        }
        
        def command = 'export JAVA_HOME=' + java_home_path + ' ; export PATH=$PATH:/var/lib/jenkins/apache-maven/apache-maven-3.3.9/bin ; export PATH=$PATH:/usr/bin ; mvn -s /var/lib/jenkins/apache-maven/apache-maven-3.3.9/conf/settings.xml -f ' + path_to_pom + '/pom.xml ' 
        sh command
    }

    stage('SonarQube Scanner') {
            dir(project_name){
                def scannerHome = tool 'SonarQube';
                withSonarQubeEnv('SonarQube') {
                 sh "${scannerHome}/bin/sonar-scanner"
                }
            }
            
        }
        
    stage('Sonarqube Quality Gate') {
        timeout(time: 1, unit: 'HOURS'){
            def qg = waitForQualityGate()
            if (qg.status != 'OK') {
                error "Pipeline abortada devido falha de Quality Gate do Sonar: ${qg.status}"
            } else {
                echo "Quality Gate passou com sucesso!"
            }
        }
    }

    stage('Anchore Scan'){
        steps {
            script {
                def anchoreScanCommand = "anchore-cli --url ${ANCHORE_ENGINE_URL} --u ${ANCHORE_ENGINE_USER} --p ${ANCHORE_ENGINE_PASS} image add ${env.BUILD_TAG}"
                sh(anchoreScanCommand)
            }
        }
    }
}