def call(options) {
  assert options.result != null

  if (options.result == 'SUCCESS') {
    slackSendSuccess(
      message: """:unicorn_face: Build "${env.BUILD_LABEL}" *Complete*"""
    )
  } else {
    slackSendFailure(
      message: """☠️ Build "${env.BUILD_LABEL}" *Failed*"""
    )
  }
}
