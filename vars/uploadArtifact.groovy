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
            set -eu

            export AWS_DEFAULT_REGION=us-east-1
            export AWS_EC2_METADATA_DISABLED=true
            export AWS_REQUEST_CHECKSUM_CALCULATION=when_required
            export AWS_RESPONSE_CHECKSUM_VALIDATION=when_required

            set -- app/build/libs/*.jar

            if [ ! -f "$1" ]; then
                echo "[ERROR] JAR file not found"
                exit 1
            fi

            VERIFY_DIR=$(mktemp -d)
            trap 'rm -rf "$VERIFY_DIR"' EXIT

            for JAR_FILE in "$@"; do
                FILE_NAME=$(basename "$JAR_FILE")
                S3_PATH="s3://cicd-artifacts/build-${BUILD_NUMBER}/${FILE_NAME}"

                aws --endpoint-url http://minio:9000 \
                    s3 cp "$JAR_FILE" "$S3_PATH"

                aws --endpoint-url http://minio:9000 \
                    s3 cp "$S3_PATH" "$VERIFY_DIR/$FILE_NAME"

                ORIGINAL_HASH=$(sha256sum "$JAR_FILE" | cut -d ' ' -f 1)
                DOWNLOAD_HASH=$(sha256sum "$VERIFY_DIR/$FILE_NAME" | cut -d ' ' -f 1)

                if [ "$ORIGINAL_HASH" != "$DOWNLOAD_HASH" ]; then
                    echo "[ERROR] Artifact checksum mismatch"
                    exit 1
                fi

                echo "[SUCCESS] SHA-256 verification passed"
                echo "SHA-256: $ORIGINAL_HASH"
            done
        '''
    }

    echo '[Shared Library] Artifact Upload Complete'
}