@Library('my-shared-library') _

pipeline {
    agent any
    parameters {
        choice(name: 'action', choices: 'create\ndelete', description: 'Choose create/Destroy')
    }
    stages {
        stage('Git Checkout'){
            when { expression { params.action == 'create' } }
            steps {
                gitCheckout(
                    branch: "main",
                    credentialsId: "63b9f1a8-ffe7-44fd-a97e-e4178f20d68e",
                    url: "https://github.com/jokermario/drone.git"
                )
            }
        }

        stage('Running tests with Maven'){
            when { expression { params.action == 'create' } }
            steps {
                script {
                    mvnTest()
                }
            }
        }

        stage('Static code analysis: Sonarqube'){
            when { expression { params.action == 'create' } }
            steps {
                script {
                    def sonarqubeCredentialId = 'sonar-api'
                    staticCodeAnalysis(sonarqubeCredentialId)
                }
            }
        }

        stage('Quality gate status: Sonarqube'){
            when { expression { params.action == 'create' } }
            steps {
                script {
                    def sonarqubeCredentialId = 'sonar-api'
                    qualityGateStatus(sonarqubeCredentialId)
                }
            }
        }
    }
}