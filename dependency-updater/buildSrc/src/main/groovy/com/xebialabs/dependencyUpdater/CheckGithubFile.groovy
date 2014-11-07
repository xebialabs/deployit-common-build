import org.gradle.api.tasks.TaskAction

class CheckGithubFile extends GitHubTask {
  def repositories = []
  Closure check

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
  }
}