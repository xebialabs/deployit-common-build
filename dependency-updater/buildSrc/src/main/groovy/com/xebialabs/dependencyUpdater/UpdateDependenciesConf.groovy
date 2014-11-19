import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions

class UpdateDependenciesConf extends UpdateGithubFile {

  UpdateDependenciesConf() {
    doUpdate = { content ->
      def cfg = parseConfig(content)
      def updates = [:]
      def updatedToList = []

      project.versionProperties.grep { project.hasProperty(it) && cfg.hasPath(versionsPrefix + it) }.each {
        def key = versionsPrefix + it
        def requestedProject = project.artifactBaseNames[it]
        def requestedVersion = project.getProperty(it)
        def currentVersion = cfg.getString(key)

        if (!requestedVersion.equals(currentVersion)) {
          logger.info("$repository: Updating $it $currentVersion -> $requestedVersion")
          updatedToList += "$requestedProject-$requestedVersion"
          updates[key] = requestedVersion
        } else {
          logger.warn("$repository: Not updating $it: $requestedProject already at version $requestedVersion")
        }
      }

      cfg = ConfigFactory.parseMap(updates, "updated values").withFallback(cfg)

      def rendered = cfg.root().render(ConfigRenderOptions.defaults().setOriginComments(false).setJson(false))

      (updatedToList.size() > 0) ? [content: rendered, message: updatedToList.join(', ')] : [:]
    }
  }
}