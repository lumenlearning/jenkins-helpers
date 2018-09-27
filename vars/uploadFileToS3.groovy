def call(profileName, name, url) {
  sh """
  /usr/bin/aws s3 cp --region us-west-2 --profile=${profileName} ${name} "${url}"
  """
}
