def call(options) {
  if (currentBuild.currentResult == 'SUCCESS') {
    slackSendSuccess(
      message: """:unicorn_face: Build "${env.BUILD_LABEL}" *Complete*"""
    )
  } else {
    slackSendFailure(
      message: """☠️ Build "${env.BUILD_LABEL}" *Failed*"""
    )
  }
}
