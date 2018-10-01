def call(options) {
  assert options.message != null

  if (options.channel == null) {
    options.channel = '#dev-noisy'
  }

  if (options.color == null) {
    options.color = '#CCCCCC'
  }

  slackSend(
    channel: options.channel,
    color: options.color,
    message: options.message
  )
}
