@Library('my-shared-library') _

pipeline {
    agent any
    stages {
        stage('Git Checkout'){
            steps {
                script {
                    gitCheckout(
                        branch: "main",
                        url: "https://github.com/jokermario/drone.git"
                        credentialsId: "63b9f1a8-ffe7-44fd-a97e-e4178f20d68e"
                    )
                }
            }
        }
    }
}