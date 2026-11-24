def call() {
    echo '[Shared Library] Application Deploy Start'

    sh '''
        set -eu

        DEPLOY_DIR=/var/jenkins_home/deployments/cicd-demo

        mkdir -p "$DEPLOY_DIR"

        cp app/build/libs/app.jar "$DEPLOY_DIR/app.jar"

        java -cp "$DEPLOY_DIR/app.jar" com.example.App

        echo "[SUCCESS] Application execution verified"
    '''

    echo '[Shared Library] Application Deploy Complete'
}