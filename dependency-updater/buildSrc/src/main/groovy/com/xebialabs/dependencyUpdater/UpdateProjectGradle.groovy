class UpdateProjectGradle extends UpdateGithubFile {

  UpdateProjectGradle() {
    doUpdate = { content ->
      if (!project.hasProperty('gradleDistributionUrl')) {
        throw new RuntimeException("Property gradleDistributionUrl not set, don't know which gradle to update to.")
      }
      def requestedGradleUrl = project.getProperty('gradleDistributionUrl')

      Properties props = new Properties()
      props.load(new StringReader(content))
      def currentUrl = props['distributionUrl']

      if (requestedGradleUrl.equals(currentUrl)) {
        logger.warn("$repository: Not updating gradle distributionUrl: already at $currentUrl")
        return [:]
      }

      logger.info("$repository: Updating gradle distribution $currentUrl -> $requestedGradleUrl")
      props['distributionUrl'] = requestedGradleUrl
      def newContent = new StringWriter()
      props.store(newContent, null)

      String gradleVersion = requestedGradleUrl.substring(requestedGradleUrl.lastIndexOf('/') + 1).replace('-bin.zip', '')
      [content: newContent.toString(), message: gradleVersion]
    }
  }

}