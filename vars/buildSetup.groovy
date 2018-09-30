Boolean DEV_DEPLOYMENT_REQUESTED = false
Boolean STAGING_DEPLOYMENT_REQUESTED = false
Boolean PROD_DEPLOYMENT_REQUESTED = false
Boolean DEPLOY_REQUESTED = false

def setupEnvironments() {
  assert params.SKIP_TESTS_REQUESTED instanceof Boolean
  assert params.DEV_DEPLOYMENT_REQUESTED instanceof Boolean
  assert params.STAGING_DEPLOYMENT_REQUESTED instanceof Boolean

  PROD_DEPLOYMENT_REQUESTED = (env.TAG_NAME != null && env.TAG_NAME != "")
  // Dev and Staging always happen for Prod deploys
  DEV_DEPLOYMENT_REQUESTED = params.DEV_DEPLOYMENT_REQUESTED == true || PROD_DEPLOYMENT_REQUESTED == true
  STAGING_DEPLOYMENT_REQUESTED = params.STAGING_DEPLOYMENT_REQUESTED == true || PROD_DEPLOYMENT_REQUESTED == true

  // If any server deployment was requested
  DEPLOY_REQUESTED = (DEV_DEPLOYMENT_REQUESTED || STAGING_DEPLOYMENT_REQUESTED || PROD_DEPLOYMENT_REQUESTED)
}

def setupS3(appName) {
  def app = appName.toLowerCase()

  env.S3_ARTIFACT_PATH = "${app}/build-${BUILD_LABEL}.zip"
  
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

  if (DEPLOY_REQUESTED == true) {
    optionsMsg += params.SKIP_TESTS_REQUESTED == true ? "~Tests~" : "Tests ✓"

    optionsMsg += " | "
    optionsMsg += DEV_DEPLOYMENT_REQUESTED == true ? "Dev ✓" : "~Dev~"

    optionsMsg += " | "
    optionsMsg += STAGING_DEPLOYMENT_REQUESTED == true ? "Staging ✓" : "~Staging~"

    optionsMsg += " | "
    optionsMsg += PROD_DEPLOYMENT_REQUESTED == true ? "Prod ✓" : "~Prod~"
  }

  def linksMsg = "<${BUILD_URL}|Jenkins Build>"
  linksMsg += PROD_DEPLOYMENT_REQUESTED == true ? " | ${SLACK_RELEASE_LINK}" : ""

  slackSendInfo(
    """
    ${appName} build "${BUILD_LABEL}" started.
    By ${WHO_STARTED_BUILD}
    ${linksMsg}
    ${optionsMsg}
    """.stripIndent()
  )
}

def call(appName) {
  // For troubleshotting
  echo "TAG_NAME: ${env.TAG_NAME}"

  env.BUILD_LABEL = "${JOB_NAME}-${BUILD_NUMBER}".replace("%2F", "-").replace("/", "-")

  setupEnvironments()
  setupS3(appName)
  setupRequestor()
  slackSendSetup(appName)
}
