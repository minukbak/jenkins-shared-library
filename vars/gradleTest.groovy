def call() {
    echo '[Shared Library] Gradle Test Start'

    try {
        sh './gradlew test'
    } finally {
        junit 'app/build/test-results/test/*.xml'
    }

    echo '[Shared Library] Gradle Test Complete'
}