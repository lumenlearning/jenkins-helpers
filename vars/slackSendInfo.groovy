def call(msg, channel = "#dev-noisy") {
  slackSend(
    channel: channel,
    color: '#CCCCCC',
    message: msg
  )
}
