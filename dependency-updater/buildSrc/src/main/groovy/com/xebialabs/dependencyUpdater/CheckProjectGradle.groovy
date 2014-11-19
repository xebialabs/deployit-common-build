class CheckProjectGradle extends CheckConsistencyAcrossRepos {

  def touchstoneUrl = null

  CheckProjectGradle() {
    check = { repository, content ->
      def currentUrl = readDistributionUrl(content)
      logger.debug("${repository}:\n\t Current    URL=${currentUrl}\n\t Touchstone URL=${touchstoneUrl}")

      if (touchstoneUrl == null) {
        logger.warn("Touchstone gradle wrapper url taken from $repository: $currentUrl")
        touchstoneUrl = currentUrl;
      } else {
        if (!currentUrl.equals(touchstoneUrl)) {
          if (currentUrl == null) {
            logger.warn "No distributionUrl defined in $repository $contentPath; not registering it as non-matching!"
          } else {
            registerMismatch(repository, 'gradle wrapper', getGradleVersionFromUrl(touchstoneUrl), getGradleVersionFromUrl(currentUrl))
          }
        }
      }
    }
  }

  def getGradleVersionFromUrl(def url) {
    def start = url.lastIndexOf('/') + 1
    def end = url.lastIndexOf('-bin.zip')
    if (end == -1) {
      end = url.length()
    }
    url.substring(start, end)
  }

  def readDistributionUrl(content) {
    Properties props = new Properties();
    props.load(new StringReader(content))
    props['distributionUrl']
  }

}