import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.ConfigValueType
import com.typesafe.config.ConfigFactory

class CheckDependencyVersions extends CheckConsistencyAcrossRepos {

  def touchstoneCfg = null

  CheckDependencyVersions() {
    check { repository, content ->
      def cfg = parseConfig(content)

      if (touchstoneCfg == null) {
        logger.warn("Repository $repository provides touchstone configuration for versions: ${renderCfg(cfg)}")
        touchstoneCfg = cfg
      } else {

        cfg.entrySet().grep {
          it.getKey().startsWith(versionsPrefix)}.each { entry ->
          def value = getEntryValue(cfg, entry)
          String key = entry.getKey()

          registerIfUnexpectedVersion(repository, key, value)
        }
      }
    }
  }

  def registerIfUnexpectedVersion(repository, fullKey, value) {
    String shortkey = fullKey - versionsPrefix

    if (!touchstoneCfg.hasPath(fullKey)) {
      logger.warn("\t Repository $repository adds $shortkey=$value to touchstone configuration")
      touchstoneCfg = ConfigFactory.parseMap([(fullKey): value], "updated values").withFallback(touchstoneCfg)
    } else {
      def touchstoneValue = touchstoneCfg.getString(fullKey)
      logger.debug("\t Current    $shortkey: $value\n\t Touchstone $shortkey: $touchstoneValue")

      if (!touchstoneValue.equals(value)) {
        registerMismatch(repository, shortkey, touchstoneValue, value)
      }
    }
  }

  def renderCfg(def cfg) {
    cfg.root().render(ConfigRenderOptions.defaults().setOriginComments(false).setJson(false))
  }

  def getEntryValue(def cfg, def entry) {
    if (entry.getValue().valueType() == ConfigValueType.STRING) {
      cfg.getString(entry.getKey())
    } else if (entry.getValue().valueType() == ConfigValueType.NUMBER) {
      cfg.getNumber(entry.getKey()).toString()
    } else {
      throw new RuntimeException("$repository: gradle/dependencies.conf has key ${entry.getKey()} with unsupported type ${entry.getValue().valueType()}");
    }
  }
}