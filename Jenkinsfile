@Library('my-shared-library') _

pipeline {
    agent any
    parameters {
        choice(name: 'action', choices: 'create\ndelete', description: 'Choose create/Destroy')
        string(name: 'ImageName', description: "name of the docker build", defaultValue: "javaapp")
        string(name: 'ImageTag', description: "tag of the docker build", defaultValue: "v1")
        string(name: 'DockerHubUser', description: "name of the Application", defaultValue: "jokermariox")
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

        stage('Docker Image Build'){
            when { expression { params.action == 'create' } }
            steps {
                script {
                    dockerBuild("${params.ImageName}", "${params.ImageTag}", "${params.DockerHubUser}")
                }
            }
        }
    }
}