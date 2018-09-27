def call(profile, name, url) {
  sh """
  /usr/bin/aws s3 cp --region us-west-2 --profile=${profile} ${name} "${url}"
  """
}
