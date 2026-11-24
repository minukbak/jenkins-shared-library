def call() {
    echo '[Shared Library] Gradle Build Start'

    sh 'chmod +x gradlew'
    sh './gradlew clean assemble'

    echo '[Shared Library] Gradle Build Complete'
}