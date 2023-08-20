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

        stage('Unit Tests: Maven'){
            when { expression { params.action == 'create' } }
            steps {
                script {
                    mvnTest()
                }
            }
        }

        stage('Static Code Analysis: Sonarqube'){
            when { expression { params.action == 'create' } }
            steps {
                script {
                    def sonarqubeCredentialId = 'sonar-api'
                    staticCodeAnalysis(sonarqubeCredentialId)
                }
            }
        }

        stage('Quality Gate Status: Sonarqube'){
            when { expression { params.action == 'create' } }
            steps {
                script {
                    def sonarqubeCredentialId = 'sonar-api'
                    qualityGateStatus(sonarqubeCredentialId)
                }
            }
        }

        stage('Maven Build: Maven'){
            when { expression { params.action == 'create' } }
            steps {
                script {
                    mvnBuild()
                }
            }
        }
    }
}