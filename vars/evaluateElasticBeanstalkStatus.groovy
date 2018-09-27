def call(profileName, envName) {
  def cmd = "/usr/bin/aws elasticbeanstalk describe-environments"
  def region = "--region us-west-2"
  def profile = "--profile=${profileName}"
  def env = "--environment-name ${envName}"

  def description = sh(returnStdout: true, script: "${cmd} ${region} ${profile} ${env}").trim()
  echo "${description}"

  def env = parseElasticBeanstalkStatus(description)
  echo "Status, Health is ${env.Status}, ${env.Health}"

  def completedHealth = ''

  if (env.Status == 'Launching' || env.Status == 'Updating') {
    echo "Status is still pending"
  }
  else {
    echo "Status is complete (${env.Health})"
    completedHealth = env.Health
  }

  return completedHealth
}
