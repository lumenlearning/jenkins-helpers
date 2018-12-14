/**
 * Wait for a Beanstalk environment to be "Green".
 *
 * @param options Map as defined in Jenkinsfile:deployToElasticBeanstalk
 * @return True on "Green" status. False on timeout while waiting for "Green".
 */
def call(Map options) {
    int maxWaitMins = options.beanstalkMaxWaitMins ?: 20
    long statusCheckIntervalMs = 5000

    long instantStarted = Instant.now()
    int tries = 0

    while (true) {
        tries += 1
        int timeElapsedMins = Duration.between(instantStarted, Instant.now()).getMinutes()

        echo "EB Status try: ${tries}, waited ${timeElapsedMins} minutes so far..."

        // Check the current status of the EB Application
        def health = evaluateElasticBeanstalkStatus(options.profile, options.envName)

        if ('Green' == health) {
            return true
        }
        if (timeElapsedMins > maxWaitMins) {
            return false
        }

        sleep(statusCheckIntervalMs)
    }
}
