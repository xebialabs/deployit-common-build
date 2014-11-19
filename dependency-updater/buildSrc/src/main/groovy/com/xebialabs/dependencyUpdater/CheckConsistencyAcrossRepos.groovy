import org.gradle.api.tasks.TaskAction

class CheckConsistencyAcrossRepos extends GitHubTask {
  def repositoriesToCheck = []
  Closure check
  def nonMatching = []

  def registerMismatch(repository, key, touchstoneValue, current) {
    nonMatching += [[repository, key, touchstoneValue, current]]
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

    if (nonMatching.size() > 0) {
      println ""
      project.ext.numInconsistencies += nonMatching.size()
      nonMatching.each { repo, key, touchstoneValue, value ->
        println "Repository $repo: $key was $value while $touchstoneValue was expected"
      }
    }
  }
}