def call() {
    echo '[Shared Library] Artifact Archive Start'

    archiveArtifacts(
        artifacts: 'app/build/libs/*.jar',
        fingerprint: true
    )

    echo '[Shared Library] Artifact Archive Complete'
}