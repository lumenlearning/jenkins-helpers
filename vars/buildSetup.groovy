def call(appName) {
  env.BUILD_LABEL = "${JOB_NAME}-${BUILD_NUMBER}".replace("%2F", "-").replace("/", "-")
}
