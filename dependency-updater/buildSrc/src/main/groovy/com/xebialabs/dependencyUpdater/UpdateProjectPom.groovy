class UpdateProjectPom extends UpdateGithubFile {
  UpdateProjectPom() {
    doUpdate = { content ->
      def newContent = new StringWriter()

      boolean insidePropertiesTag = false
      def updatedToList = []
      def matcher

      content.eachLine { contentLine ->
        def outputLine = contentLine
        if (!insidePropertiesTag && contentLine ==~ /^\s+<properties>\s*$/) {
          insidePropertiesTag = true;
        } else if (insidePropertiesTag && contentLine ==~ "^\\s+</properties>\\s*\$") {
          insidePropertiesTag = false;
        } else if (insidePropertiesTag && (matcher = (contentLine =~ /^(\s+)<(\w+)>(.+)<\/\w+>(\s*)$/))) {
          def leadingWhitespace = matcher[0][1]
          def propertyName = matcher[0][2]
          def currentVersion = matcher[0][3]
          def trailingWhitespace = matcher[0][4]

          if (project.hasProperty(propertyName) && project.versionProperties.contains(propertyName)) {
            def requestedVersion = project.getProperty(propertyName)
            def requestedProject = project.artifactBaseNames[propertyName]
            if (!requestedVersion.equals(currentVersion)) {
              logger.info("$repository: Updating $propertyName $currentVersion -> $requestedVersion")
              updatedToList += "$requestedProject-$requestedVersion"
            } else {
              logger.debug("$repository: Not updating $contentLine: $requestedProject already at version $requestedVersion")
            }
            outputLine = "${leadingWhitespace}<${propertyName}>${requestedVersion}</${propertyName}>${trailingWhitespace}"
          }
        }
        newContent.println outputLine
      }

      (updatedToList.size() > 0) ? [content: newContent.toString(), message: updatedToList.join(', ')] : [:]
    }
  }
}