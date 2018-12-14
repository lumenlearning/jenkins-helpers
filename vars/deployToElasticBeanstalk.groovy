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

  success = waitForBeanstalkStatus(20, 5, options)

  if (success == false) {
    return false
  }

  // Send a Slack message indicating the EB deploy has completed
  slackSendSuccess(
    message: """:rocket: Build "${env.BUILD_LABEL}" deploy to *${options.deployName}* succeeded."""
  )
}
