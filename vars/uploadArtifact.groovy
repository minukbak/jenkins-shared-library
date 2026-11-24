def call() {
    echo '[Shared Library] Artifact Upload Start'

    withCredentials([
        usernamePassword(
            credentialsId: 'minio-credentials',
            usernameVariable: 'AWS_ACCESS_KEY_ID',
            passwordVariable: 'AWS_SECRET_ACCESS_KEY'
        )
    ]) {
        sh '''
            set +x
            set -e

            export AWS_DEFAULT_REGION=us-east-1
            export AWS_EC2_METADATA_DISABLED=true
            export AWS_REQUEST_CHECKSUM_CALCULATION=when_required
            export AWS_RESPONSE_CHECKSUM_VALIDATION=when_required

            aws --endpoint-url http://minio:9000 \
                s3 cp app/build/libs/ \
                s3://cicd-artifacts/build-${BUILD_NUMBER}/ \
                --recursive \
                --exclude "*" \
                --include "*.jar"

            aws --endpoint-url http://minio:9000 \
                s3 ls \
                s3://cicd-artifacts/build-${BUILD_NUMBER}/
        '''
    }

    echo '[Shared Library] Artifact Upload Complete'
}