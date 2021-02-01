/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

pipeline {
    agent {
        label 'ubuntu'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timeout(time: 2, unit: 'HOURS')
    }

    triggers {
        cron('@daily')
        pollSCM('@hourly')
    }

    stage ('Build and Test - Java 8') {
        options {
        timeout(time: 4, unit: 'HOURS')
            retry(2)
        }
        steps {
            sh 'mvn -V clean verify checkstyle:check apache-rat:check'
        }
        post {
            always {
                junit '**/target/surefire-reports/*.xml'
                deleteDir()
            }
        }
    }

    stage ('Deploy') {
        options {
            timeout(time: 2, unit: 'HOURS')
            retry(2)
        }
        tools {
            maven "maven_latest"
            jdk "jdk_8_latest"
        }
        steps {
            sh "mvn clean deploy -Pgenerate-assembly"
        }
        post {
            always {
                junit(testResults: '**/surefire-reports/*.xml', allowEmptyResults: true)
                archiveArtifacts '**/target/*.jar'
            }
        }
    }

    post {
        // Build Failed
        failure {
        mail to: "notifications@myfaces.apache.org",
        subject: "Jenkins pipeline failed: ${currentBuild.fullDisplayName}",
        body: 
        """
        Jenkins build URL: ${env.BUILD_URL}
        The build for ${env.JOB_NAME} completed successfully and is back to normal.
        Build: ${env.BUILD_URL}
        Logs: ${env.BUILD_URL}console
        Changes: ${env.BUILD_URL}changes
        """
        }

        // Build succeeded, but some tests failed
        unstable {
        mail to: "notifications@myfaces.apache.org",
        subject: "Jenkins pipeline failed: ${currentBuild.fullDisplayName}",
        body: 
        """
        Jenkins build URL: ${env.BUILD_URL}
        The build for ${env.JOB_NAME} completed successfully and is back to normal.
        Build: ${env.BUILD_URL}
        Logs: ${env.BUILD_URL}console
        Changes: ${env.BUILD_URL}changes
        """
        }

        // Last build failed, but current one was successful
        fixed {
        mail to: "notifications@myfaces.apache.org",
        subject: "Jenkins pipeline is back to normal: ${currentBuild.fullDisplayName}",
        body: 
        """
        Jenkins build URL: ${env.BUILD_URL}
        The build for ${env.JOB_NAME} completed successfully and is back to normal.
        Build: ${env.BUILD_URL}
        Logs: ${env.BUILD_URL}console
        Changes: ${env.BUILD_URL}changes
        """
        }
    }
}

