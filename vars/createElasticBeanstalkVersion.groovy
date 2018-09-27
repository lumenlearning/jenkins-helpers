def call(profileName, appName, buildLabel, bucketName, artifactName) {
  def cmd = "/usr/bin/aws elasticbeanstalk create-application-version"
  def region = "--region us-west-2"
  def profile = "--profile=${profileName}"
  def app = "--application-name ${appName}"
  def version = "--version-label ${buildLabel}"
  def bundle = "--source-bundle S3Bucket=${bucketName},S3Key=${artifactName}"
  def desc = "--description 'Jenkins build ${buildLabel}'"

  sh "${cmd} ${region} ${profile} ${app} ${version} ${bundle} ${desc}"
}
