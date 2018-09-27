def call(dirPath, fileName) {
  sh """
  rm -f ${fileName}
  zip --exclude="*.git*" --exclude="*tmp*" ${fileName} -r ${dirPath}
  """
}
