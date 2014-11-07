import org.gradle.api.tasks.TaskAction

class UpdateGithubFile extends GitHubTask {
  String repository
  Closure doUpdate

  @TaskAction
  def update() {
    checkBranch()
    doWithRepo(repository, { url, content, sha ->
      logger.debug("Loaded from '{}': '{}'", url, content)

      def updateMap = doUpdate content
      if (updateMap.size() > 0) {
        logger.debug("Updating with {}", updateMap)

        def body = ['content': updateMap.content.bytes.encodeBase64().toString(),
                    'message': updateMap.message.toString(),
                    'sha': sha,
                    'branch': "${branch}".toString()]
        logger.debug("Update request body: {}", body)

        def putResponse = github().put('path': url, 'body': body)
        logger.debug("Update response: {}", putResponse.data)
      } else {
        logger.debug("No update to do.")
      }
    })
  }
}