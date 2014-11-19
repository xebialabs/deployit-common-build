import org.gradle.api.tasks.TaskAction

class UpdateGithubFile extends GitHubTask {
  String repository
  Closure doUpdate

  @TaskAction
  def update() {
    checkBranch()
    readContentFromRepo(repository, { content, token ->

      def updateMap = doUpdate content

      if (updateMap.size() == 0) {
        logger.debug("No update to do.")
      } else {
        logger.debug("Updating with {}", updateMap)

        def body = ['content': updateMap.content.bytes.encodeBase64().toString(),
                    'message': updateMap.message.toString(),
                    'sha': token,
                    'branch': "${branch}".toString()]
        logger.debug("Update request body: {}", body)

        def putResponse = github().put('path': contentUrl(repository, contentPath), 'body': body)
        logger.debug("Update response: {}", putResponse.data)
      }
    })
  }
}