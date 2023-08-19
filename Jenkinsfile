pipeline {
    agent any
    stages {
        stage('Git Checkout'){
            steps {
                script {
                    git branch: 'main', credentialsId: '63b9f1a8-ffe7-44fd-a97e-e4178f20d68e', url: 'https://github.com/jokermario/drone'
                }
            }
        }
    }
}