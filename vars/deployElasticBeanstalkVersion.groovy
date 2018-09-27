def call(profile, appName, envName, buildLabel) {
  sh """/usr/bin/aws elasticbeanstalk update-environment
    --region us-west-2
    --profile=${profile}
    --application-name ${appName}
    --environment-name ${envName}
    --version-label ${buildLabel}
  """
}
