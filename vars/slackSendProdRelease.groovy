def call(options) {
  assert options.appName != null

  def desc = ""

  if (appDesc != '') {
    desc = " (${appDesc})"
  }

  slackSendSuccess(
    channel: '#releases',
    message: """
    :unicorn_face: ${appName}${desc} was released to production! (Build "${env.BUILD_LABEL}")
    ${env.SLACK_RELEASE_LINK}
    """.stripIndent()
  )
}
