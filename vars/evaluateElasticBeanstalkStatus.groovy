def call(profileName, envName) {
  def cmd = "/usr/bin/aws elasticbeanstalk describe-environments"
  def region = "--region us-west-2"
  def profile = "--profile=${profileName}"
  def env = "--environment-name ${envName}"

  def description = sh(returnStdout: true, script: "${cmd} ${region} ${profile} ${env}").trim()
  echo "${description}"

  def status = parseElasticBeanstalkStatus(description)
  echo "Status, Health is ${status.Status}, ${status.Health}"

  def completedHealth = ''

  if (status.Status == 'Launching' || status.Status == 'Updating') {
    echo "Status is still pending"
  }
  else {
    echo "Status is complete (${status.Health})"
    completedHealth = status.Health
  }

  return completedHealth
}
