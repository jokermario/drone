@Library('my-shared-library') _

pipeline {
    agent any
//     parameters {
//         choice(name: 'action', choices: 'create\ndelete', description: 'Choose create/Destroy')
//         choice(name: 'Select Registry', choices: 'Docker Hub\nAWS ECR', description: 'Choose Docker Hub/AWS ECR')
//         string(name: 'ImageName', description: "name of the docker build", defaultValue: "javaapp")
//         string(name: 'ImageTag', description: "tag of the docker build", defaultValue: "v1")
//         string(name: 'DockerHubUser', description: "name of the Application", defaultValue: "jokermariox")
//     }
    stages {

    stage('User Input') {
                steps {
                    script {
                        def userInput = input(
                            id: 'userInput',
                            message: 'Select an action and registry:',
                            parameters: [
                                choice(name: 'action', choices: 'create\ndelete', description: 'Choose create/Destroy'),
                                choice(name: 'Select Registry', choices: 'Docker Hub\nAWS ECR', description: 'Choose Docker Hub/AWS ECR')
                            ]
                        )

                        if (userInput['Select Registry'] == 'AWS ECR') {
                            def ecrUserInput = input(
                                id: 'ecrUserInput',
                                message: 'Provide ECR User:',
                                parameters: [
                                    string(name: 'ECRUser', description: 'Name of the Application', defaultValue: 'jokermariox')
                                ]
                            )

                            echo "Selected Registry: AWS ECR"
                            echo "ECR User: ${ecrUserInput['ECRUser']}"
                        } else if (userInput['Select Registry'] == 'Docker Hub') {
                            def dockerHubUserInput = input(
                                id: 'dockerHubUserInput',
                                message: 'Provide Docker Hub User:',
                                parameters: [
                                    string(name: 'DockerHubUser', description: 'Name of the Application', defaultValue: 'jokermariox')
                                ]
                            )

                            echo "Selected Registry: Docker Hub"
                            echo "Docker Hub User: ${dockerHubUserInput['DockerHubUser']}"
                        }

                        echo "Action: ${userInput['action']}"
                    }
                }
            }

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

        stage('Docker Image Scan: Trivy'){
            when { expression { params.action == 'create' } }
            steps {
                script {
                    dockerImageScan("${params.ImageName}", "${params.DockerHubUser}")
                }
            }
        }

        stage('Docker Image Push: DockerHub'){
            when { expression { params.action == 'create' } }
            steps {
                script {
                    dockerImagePush("${params.ImageName}", "${params.ImageTag}", "${params.DockerHubUser}")
                }
            }
        }

        stage('Docker Image Cleanup: DockerHub'){
            when { expression { params.action == 'create' } }
            steps {
                script {
                    dockerImageClean("${params.ImageName}", "${params.ImageTag}", "${params.DockerHubUser}")
                }
            }
        }
    }
}