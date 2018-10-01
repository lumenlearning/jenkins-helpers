def call(Map config) {
  assert config.deployName != null
  assert config.profile != null
  assert config.appName != null
  assert config.envName != null
  assert config.buildLabel != null

  if (config.sleepTime == null) {
    config.sleepTime = 75
  }

  // Deploy the previously created EB version
  deployElasticBeanstalkVersion(
    config.profile,
    config.appName,
    config.envName,
    config.buildLabel
  )

  // Send a Slack message indicating the EB deploy has started
  slackSendInfo(
    """:rocket: Build "${BUILD_LABEL}" deploy to *${config.deployName}* started..."""
  )

  // Sleep because EB deploys are never very quick, and we don't need to poll
  // right away
  sleep config.sleepTime

  def success = false
  def tries = 0

  waitUntil {
    tries += 1
    echo "EB Status try: ${tries}"

    // Check the current status of the EB Application
    def health = evaluateElasticBeanstalkStatus(config.profile, config.envName)

    success = health == 'Green'

    return health != ''
  }

  if (success == false) {
    return false
  }

  // Send a Slack message indicating the EB deploy has completed
  slackSendSuccess(""":rocket: Build "${BUILD_LABEL}" deploy to *${config.deployName}* succeeded.""")
}
