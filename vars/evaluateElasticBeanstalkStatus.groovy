def call(profile, envName) {
  String statusScript = """/usr/bin/aws elasticbeanstalk describe-environments
    --region us-west-2
    --profile=${profile}
    --environment-name ${envName}
  """.stripIdent()

  def description = sh(returnStdout: true, script: statusScript).trim()
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
