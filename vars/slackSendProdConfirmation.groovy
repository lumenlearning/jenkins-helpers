def call(Map options) {
  assert options.buildLabel != null
  assert options.userLabel != null

  options.message = """
  :warning: Build "${options.buildLabel}" awaiting production deploy confirmation from ${options.userLabel}.
  <${env.BUILD_URL}|Jenkins Build>
  """.stripIndent()

  slackSendWarning(options)
}
