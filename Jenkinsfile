#!groovy

def kubernetes_build_config = """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:3-openjdk-11
    command:
    - cat
    env:
    - name: "HOME"
      value: "/home/jenkins"
    - name: "MAVEN_OPTS"
      value: "-Xms1024m -Xmx2560m"
    tty: true
    resources:
      limits:
        memory: "3Gi"
        cpu: "1"
      requests:
        memory: "3Gi"
        cpu: "1"
    volumeMounts:
    - name: m2-repo
      mountPath: /home/jenkins/.m2/repository
    - name: sonar
      mountPath: /home/jenkins/.sonar
  - name: node
    image: sonarsource/sonarcloud-scan:1.2.1
    command:
    - cat
    env:
    - name: "HOME"
      value: "/home/jenkins"
    tty: true
    resources:
      limits:
        memory: "1Gi"
        cpu: "1"
      requests:
        memory: "1Gi"
        cpu: "800m"
    volumeMounts:
    - name: sonar
      mountPath: /home/jenkins/.sonar
  - name: jnlp
    volumeMounts:
    - name: volume-known-hosts
      mountPath: /home/jenkins/.ssh
  volumes:
  - name: volume-known-hosts
    configMap:
      name: known-hosts
  - name: m2-repo
    emptyDir: {}
  - name: sonar
    emptyDir: {}
"""

def projectName = 'lsp-for-cobol'
def kubeLabelPrefix = "${projectName}_pod_${env.BUILD_NUMBER}_${env.BRANCH_NAME}"
        // cleaning the branch name according K8s restrictions
        .replaceAll(/[^a-zA-Z0-9._-]+/,"").take(52)
def kubeBuildLabel = "${kubeLabelPrefix}_build"

pipeline {
    agent none
    options {
        disableConcurrentBuilds()
        timestamps()
        timeout(time: 3, unit: 'HOURS')
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '3'))
    }
    environment {
        branchName = "$env.BRANCH_NAME"
    }
    stages {
        stage('Build a package') {
            agent {
                kubernetes {
                    label kubeBuildLabel
                    yaml kubernetes_build_config
                }
            }
            stages {
                stage('Checkout'){
                    steps {
                        deleteDir()
                        checkout scm
                    }
                }
                stage('Build LSP server part') {
                    steps {
                        container('maven') {
                            dir('server') {
                                withMaven {
                                    sh 'mvn -version'
                                    sh 'mvn clean verify --no-transfer-progress'
                                    sh 'cp target/server.jar $WORKSPACE/clients/cobol-lsp-vscode-extension/server/'
                                }
                            }
                        }
                    }
                }

                stage('SonarCloud analysis-Server') {
                    steps {
                        container('maven') {
                            dir('server') {
                                withCredentials([string(credentialsId: 'sonarcloud-token', variable: 'SONARCLOUD_TOKEN')]) {
                                    sh 'mvn sonar:sonar -Dsonar.projectKey=eclipse_che-che4z-lsp-for-cobol -Dsonar.organization=eclipse -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=${SONARCLOUD_TOKEN} -Dsonar.branch.name=${BRANCH_NAME} --no-transfer-progress'
                                }
                            }
                        }
                    }
                }
                stage('Client - Install dependencies') {
                    environment {
                        npm_config_cache = "$env.WORKSPACE"
                    }
                    steps {
                        container('node') {
                            dir('clients/cobol-lsp-vscode-extension') {
                                sh 'npm ci'
                            }
                        }
                    }
                }

                stage('Client - Unit tests'){
                  environment {
                      npm_config_cache = "$env.WORKSPACE"
                  }
                  steps {
                      container('node') {
                          dir('clients/cobol-lsp-vscode-extension') {
                            sh 'npm run coverage'
                          }
                      }
                  }
                }

                stage('SonarCloud analysis-Client') {
                    environment {
                        npm_config_cache = "$env.WORKSPACE"
                        SONAR_BINARY_CACHE="$env.WORKSPACE"
                    }
                    steps {
                        container('node') {
                            dir('clients/cobol-lsp-vscode-extension') {
                                sh 'npm i sonarqube-scanner'
                                withCredentials([string(credentialsId: 'sonarcloud-token', variable: 'SONARCLOUD_TOKEN')]) {
                                    sh 'node_modules/sonarqube-scanner/dist/bin/sonar-scanner -Dsonar.projectKey=com.ca.lsp:com.ca.lsp.cobol -Dsonar.organization=eclipse -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=${SONARCLOUD_TOKEN} -Dsonar.branch.name=${BRANCH_NAME}'
                                }
                            }
                        }
                    }
                }

                stage('Client - Change version') {
                    environment {
                        // Cleaning the branch name according to https://semver.org/ rules
                        buildIdentifier = "${env.BRANCH_NAME}.${env.BUILD_NUMBER}".replaceAll(/[^a-zA-Z0-9.-]+/,"")
                    }
                    when {
                        expression { branchName != 'master' }
                    }
                    steps {
                        container('node') {
                            dir('clients/cobol-lsp-vscode-extension') {
                                sh 'sed -i "s/\\"version\\": \\"\\(.*\\)\\"/\\"version\\": \\"\\1+$buildIdentifier\\"/g" package.json'
                            }
                        }
                    }
                }

                stage('Update telemetry key') {
                    when {
                        expression { branchName == 'master' }
                    }

                    steps{
                        container('node') {
                            dir('clients/cobol-lsp-vscode-extension') {
                                //test telemetry key generation
                                withCredentials([string(credentialsId: 'TELEMETRY_INSTRUMENTATION_KEY', variable: 'TELKEY')]) {
                                    sh 'echo ${TELKEY}|base64 > resources/TELEMETRY_KEY'
                                }
                            }
                        }
                    }
                }

                stage('Client - Package') {
                    environment {
                        npm_config_cache = "$env.WORKSPACE"
                    }
                    steps {
                        container('node') {
                            dir('clients/cobol-lsp-vscode-extension') {
                                sh 'npx vsce package'
                                archiveArtifacts "*.vsix"
                            }
                        }
                    }
                }

                stage('Deploy') {
                    environment {
                        sshChe4z = "genie.che4z@projects-storage.eclipse.org"
                        project = "download.eclipse.org/che4z/snapshots/$projectName"
                        url = "$project/$branchName"
                        deployPath = "/home/data/httpd/$url"
                    }
                    when {
                        expression { branchName == 'master' || branchName == 'development' }
                    }
                    steps {
                        container('jnlp') {
                            sshagent(['projects-storage.eclipse.org-bot-ssh']) {
                                sh '''
                                ssh $sshChe4z rm -rf $deployPath
                                ssh $sshChe4z mkdir -p $deployPath
                                scp -r $WORKSPACE/clients/cobol-lsp-vscode-extension/*.vsix $sshChe4z:$deployPath
                                '''
                                echo "Deployed to https://$url"
                            }
                        }
                    }
                }
            }
        }
    }
}
