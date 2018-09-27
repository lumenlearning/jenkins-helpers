def call(profile, appName, envName, buildLabel) {
  def cmd = "/usr/bin/aws elasticbeanstalk update-environment"
  def region = "--region us-west-2"
  def profile = "--profile=${profile}"
  def app = "--application-name ${appName}"
  def env = "--environment-name ${envName}"
  def version = "--version-label ${buildLabel}"

  sh "${cmd} ${region} ${profile} ${app} ${env} ${version}"
}
