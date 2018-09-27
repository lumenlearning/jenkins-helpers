def call(msg, channel = "#dev-noisy") {
  slackSend(
    channel: channel,
    color: 'good',
    message: msg
  )
}
