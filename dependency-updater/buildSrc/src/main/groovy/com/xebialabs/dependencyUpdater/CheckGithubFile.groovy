import org.gradle.api.tasks.TaskAction

class CheckGithubFile extends GitHubTask {
  def repositories = []
  Closure check
  def nonMatching = []

  def mismatch(repository, key, touchstoneValue, current) {
    nonMatching += [[repository, key, touchstoneValue, current]]
  }

  @TaskAction
  def check() {
    checkBranch()

    repositories.each { repository ->
      def content = ""
      doWithRepo(repository, { url, urlContent, sha ->
        content = urlContent
        logger.debug("Loaded from '{}': '{}'", url, content)
      })
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