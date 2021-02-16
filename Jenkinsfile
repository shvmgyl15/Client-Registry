pipeline {

	agent any
	stages {
		   
		stage('Build and Publish Docker Image to Azure Container Registry') {
                        steps {
                                withCredentials([string(credentialsId: 'EKS-Region', variable: 'REGION'), string(credentialsId: 'Registry-Name', variable: 'REGISTRY_NAME')]) {
					sh 'ls'
					sh './setJava.sh'
					
					sh 'java -version'
					sh 'ls'
					sh 'docker login -u AWS -p $(aws ecr get-login-password --region $REGION) $REGISTRY_NAME'
					sh 'echo $REGISTRY_NAME"/consent-manager/client-registry:${BUILD_NUMBER}"'
					sh 'docker build . -t  $REGISTRY_NAME"/consent-manager/client-registry:${BUILD_NUMBER}"'
					sh 'docker push $REGISTRY_NAME"/consent-manager/client-registry:${BUILD_NUMBER}"'
				}

                        }
                        post {
                                success {
                                        echo "Published Docker Image to Azure Container Registry"
                                }
                        }
                }
 
                stage('Deploy Application in AKS') {
                        steps {
				sh 'kubectl apply -f kubernetes/deployment.yml'
                                sh 'kubectl apply -f kubernetes/service.yml'
							
				withCredentials([string(credentialsId: 'Registry-Name', variable: 'REGISTRY_NAME')]) {
                                        sh 'kubectl set image deployment/client-registry-test  client-registry-test=$REGISTRY_NAME"/consent-manager/client-registry:${BUILD_NUMBER}" -n consent-manager'
                                }
                        }
                        post {
                                success {
                                        echo "Deployed to AKS"
                                }
                        }
                }

		
	}
}
