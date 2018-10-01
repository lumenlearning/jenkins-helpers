def call(Map options) {
  assert options.deployName != null
  assert options.profile != null
  assert options.appName != null
  assert options.envName != null
  assert options.buildLabel != null

  if (options.sleepTime == null) {
    options.sleepTime = 75
  }

  // Deploy the previously created EB version
  deployElasticBeanstalkVersion(
    options.profile,
    options.appName,
    options.envName,
    options.buildLabel
  )

  // Send a Slack message indicating the EB deploy has started
  slackSendInfo(
    message: """:rocket: Build "${env.BUILD_LABEL}" deploy to *${options.deployName}* started..."""
  )

  // Sleep because EB deploys are never very quick, and we don't need to poll
  // right away
  sleep options.sleepTime

  def success = false
  def tries = 0

  waitUntil {
    tries += 1
    echo "EB Status try: ${tries}"

    // Check the current status of the EB Application
    def health = evaluateElasticBeanstalkStatus(options.profile, options.envName)

    success = health == 'Green'

    return health != ''
  }

  if (success == false) {
    return false
  }

  // Send a Slack message indicating the EB deploy has completed
  slackSendSuccess(
    message: """:rocket: Build "${env.BUILD_LABEL}" deploy to *${options.deployName}* succeeded."""
  )
}
