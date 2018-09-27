def call(profile, appName, buildLabel, bucketName, artifactName) {
  sh """/usr/bin/aws elasticbeanstalk create-application-version
    --region us-west-2
    --profile=${profile}
    --application-name ${appName}
    --version-label ${buildLabel}
    --source-bundle S3Bucket=${bucketName},S3Key=${artifactName}
    --description 'Jenkins build ${buildLabel}'
  """.stripIndent()
}
