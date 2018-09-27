import groovy.json.JsonSlurper

def call(description) {
  def json

  try {
    json = new JsonSlurper().parseText(description)
  }
  catch(e) {
    return 'Could not parse environment description value'
  }

  try {
    return json.Environments[0]
  }
  catch(e) {
    return 'Could not find environment status in description info'
  }
}
