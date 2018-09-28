def call(msg, channel = "#dev-noisy") {
  slackSend(
    channel: channel,
    color: 'danger',
    message: msg
  )
}
