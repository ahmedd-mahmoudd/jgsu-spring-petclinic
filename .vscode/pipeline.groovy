pipeline {
    agent any
    triggers { pollSCM('* * * * *') }

  /* tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "M3"
    } */

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/ahmedd-mahmoudd/jgsu-spring-petclinic.git'
            }
        }    

        stage('Build') {
            steps {
              
                sh "./mvnw  package"
                

                // To run Maven on a Windows agent, use
                // bat "mvn -Dmaven.test.failure.ignore=true clean package"
            }

           post {
             // If Maven was able to run the tests, even if some of the test
             //    failed, record the test results and archive the jar file.
              always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }
               changed {
                    emailext attachLog: true, 
                    body: "Please go to ${BUILD_URL} and verify the build",
                    compressLog: true, 
                    recipientProviders: [upstreamDevelopers(), requestor()], 
                    subject: " Job \'${JOB_NAME}\' (${BUILD_NUMBER}) is waiting for input ", 
                    to: 'test@jenkins'
                }
            } 
        }
    }
}
