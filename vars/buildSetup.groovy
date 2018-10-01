def setupEnvironments() {
  assert params.SKIP_TESTS_REQUESTED instanceof Boolean
  assert params.DEV_DEPLOYMENT_REQUESTED instanceof Boolean
  assert params.STAGING_DEPLOYMENT_REQUESTED instanceof Boolean

  env.PROD_DEPLOYMENT_REQUESTED = (env.TAG_NAME != null && env.TAG_NAME != "")
  // Dev and Staging always happen for Prod deploys
  env.DEV_DEPLOYMENT_REQUESTED = params.DEV_DEPLOYMENT_REQUESTED == true || env.PROD_DEPLOYMENT_REQUESTED == true
  env.STAGING_DEPLOYMENT_REQUESTED = params.STAGING_DEPLOYMENT_REQUESTED == true || env.PROD_DEPLOYMENT_REQUESTED == true

  // If any server deployment was requested
  env.DEPLOY_REQUESTED = (
    env.DEV_DEPLOYMENT_REQUESTED == 'true' || 
    env.STAGING_DEPLOYMENT_REQUESTED == 'true' || 
    env.PROD_DEPLOYMENT_REQUESTED == 'true'
  )
}

def setupS3(identifier) {
  env.S3_ARTIFACT_PATH = "${identifier}/build-${BUILD_LABEL}.zip"
  
  env.S3_ARTIFACTS_BUCKET_URL_STG = "s3://${S3_ARTIFACTS_BUCKET_STG}"
  env.S3_ARTIFACTS_BUCKET_URL_PROD = "s3://${S3_ARTIFACTS_BUCKET_PROD}"

  env.S3_ARTIFACT_URL_STG = "${S3_ARTIFACTS_BUCKET_URL_STG}/${S3_ARTIFACT_PATH}"
  env.S3_ARTIFACT_URL_PROD = "${S3_ARTIFACTS_BUCKET_URL_PROD}/${S3_ARTIFACT_PATH}"
}

def setupRequestor() {
  /**
   * Who should be identified as responsible for triggering the build. This gets
   * included in slack messages, and is derived from many sources based on the
   * whatever can be extracted from the build cause and run context.
   */
  env.WHO_STARTED_BUILD = ''

  // When triggered by a Pull Request event:
  if (env.CHANGE_AUTHOR) {
    env.WHO_STARTED_BUILD = "*${env.CHANGE_AUTHOR}*'s Pull Request"
  }

  // It's unknown so just credit Jenkins with author of the most recent commit:
  if (env.WHO_STARTED_BUILD == '') {
    // The shell script grabs Author metadata from the latest commit:
    env.WHO_STARTED_BUILD = "(Last commit by: *${sh(returnStdout: true, script: 'git log --format="%aN" -1').trim()}*)"
  }
}

def slackSendSetup(appName) {
  def optionsMsg = ""

  if (env.DEPLOY_REQUESTED == 'true') {
    optionsMsg += params.SKIP_TESTS_REQUESTED == true ? "~Tests~" : "Tests ✓"

    optionsMsg += " | "
    optionsMsg += env.DEV_DEPLOYMENT_REQUESTED == 'true' ? "Dev ✓" : "~Dev~"

    optionsMsg += " | "
    optionsMsg += env.STAGING_DEPLOYMENT_REQUESTED == 'true' ? "Staging ✓" : "~Staging~"

    optionsMsg += " | "
    optionsMsg += env.PROD_DEPLOYMENT_REQUESTED == 'true' ? "Prod ✓" : "~Prod~"
  }

  def linksMsg = "<${BUILD_URL}|Jenkins Build>"
  linksMsg += env.PROD_DEPLOYMENT_REQUESTED == 'true' ? " | ${SLACK_RELEASE_LINK}" : ""

  slackSendInfo(
    message: """
    ${appName} build "${env.BUILD_LABEL}" started.
    By ${env.WHO_STARTED_BUILD}
    ${linksMsg}
    ${optionsMsg}
    """.stripIndent()
  )
}

def call(Map config) {
  assert config.appName != null

  if (config.repoName == null) {
    config.repoName = config.appName.toLowerCase()
  }

  // For troubleshotting
  echo "TAG_NAME: ${env.TAG_NAME}"

  // Unique slug name to identify this build and to name artifact files
  env.BUILD_LABEL = "${JOB_NAME}-${BUILD_NUMBER}".replace("%2F", "-").replace("/", "-")

  echo "Build label: ${env.BUILD_LABEL}"

  // Link to the GitHub Release. Will be useless on non-tag builds.
  env.GITHUB_RELEASE_URL = "https://github.com/lumenlearning/${config.repoName}/releases/tag/${env.TAG_NAME}"

  // Slack-formatted Link to Release Notes:
  env.SLACK_RELEASE_LINK = "<${env.GITHUB_RELEASE_URL}|Release Notes>"

  setupEnvironments()
  setupS3(config.repoName)
  setupRequestor()
  slackSendSetup(config.appName)
}
