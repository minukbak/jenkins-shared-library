import groovy.json.JsonOutput

def call(String status) {

    def success = status == 'SUCCESS'
    def icon = success ? ':white_check_mark:' : ':x:'

    def message = """
${icon} *Jenkins Pipeline ${status}*

*Project:* ${env.JOB_NAME}
*Build Number:* #${env.BUILD_NUMBER}
*Result:* ${status}
*Build URL:* ${env.BUILD_URL}
*Console Log:* ${env.BUILD_URL}console
""".stripIndent()

    if (success) {
        message += """
*Artifact:* ${env.BUILD_URL}artifact/
*Storage:* http://localhost:9001
*Bucket:* cicd-artifacts
*Path:* build-${env.BUILD_NUMBER}/
""".stripIndent()
    }

    def payload = JsonOutput.toJson([
        text: message
    ])

    writeFile(
        file: 'slack-payload.json',
        text: payload
    )

    try {
        withCredentials([
            string(
                credentialsId: 'slack-webhook-url',
                variable: 'SLACK_WEBHOOK_URL'
            )
        ]) {
            sh '''
                set +x

                curl --fail --silent --show-error \
                    --connect-timeout 10 \
                    --max-time 30 \
                    -X POST \
                    -H "Content-Type: application/json" \
                    --data-binary @slack-payload.json \
                    "$SLACK_WEBHOOK_URL"
            '''
        }

        echo '[Shared Library] Slack Notification Complete'

    } finally {
        sh 'rm -f slack-payload.json'
    }
}