import org.gradle.api.tasks.TaskAction

class CheckConsistencyAcrossRepos extends GitHubTask {
  def repositoriesToCheck = []
  Closure check

  def registerMismatch(repository, key, touchstoneValue, current) {
    project.inconsistencies += [[repository, key, touchstoneValue, current]]
  }

  @TaskAction
  def check() {
    checkBranch()

    repositoriesToCheck.each { repository ->
      def content = ""
      readContentFromRepo(repository, { urlContent, token ->
        content = urlContent
      })
      logger.info("Checking $repository...")
      check repository, content
    }
  }
}