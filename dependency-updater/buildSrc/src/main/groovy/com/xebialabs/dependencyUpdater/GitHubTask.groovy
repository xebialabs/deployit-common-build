import org.gradle.api.DefaultTask

import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.JSON
import org.apache.http.*
import org.apache.http.protocol.*
import org.apache.http.auth.*
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions


class GitHubTask extends DefaultTask {
  String username = "${ -> project.githubOauthToken }"
  String password = 'x-oauth-basic'
  String organization = 'xebialabs'
  String contentPath
  def ignoreMissing = project.hasProperty("ignoreMissing") ? Boolean.parseBoolean(project.ignoreMissing) : true
  def branch = "${ -> project.branch}"

  def versionsPrefix = 'xebialabs.dependencies.versions.'

  def github() {
    // set auth header manually else it doesn't work
    def userPassBase64 = "${username}:${password}".toString().bytes.encodeBase64()

    return new RESTClient("https://api.github.com/").with {
      contentType = JSON
      defaultRequestHeaders.'Authorization' = "Basic $userPassBase64"
      defaultRequestHeaders.'Accept' = 'application/vnd.github.v3+json'
      // will not work without User-Agent!
      defaultRequestHeaders.'User-Agent' = 'GradleGithubTask'
      handler.failure = { resp ->
        throw new RuntimeException("GitHub API Failure: ${resp.statusLine}")
      }
      delegate
    }
  }

  def parseConfig(cfgString) {
    ConfigFactory.parseString(cfgString, ConfigParseOptions.defaults())
  }


  def checkBranch() {
    try {
      if (!"${branch}".trim()) {
        throw new RuntimeException("Task ${name} requires you to specify a branch, e.g. -Pbranch=master")
      }
    } catch (MissingPropertyException e) {
      throw new RuntimeException("Task ${name} requires you to specify a branch, e.g. -Pbranch=master", e)
    }
  }

  def contentUrl(repoName, path) {
    "/repos/$organization/$repoName/contents/$path"
  }

  def readContentFromRepo(def repository, Closure action) {
    try {
      def contentUrl = contentUrl(repository, contentPath)
      def response = github().get(path: contentUrl, query: ['ref': "${branch}".toString()])
      def content = new String(response.data.content.decodeBase64(), 'UTF-8');
      def optimisticLockingToken = response.data.sha
      logger.debug("Loaded from '{}': '{}'", contentUrl, content)

      action(content, optimisticLockingToken)

    } catch (RuntimeException e) {
      if(ignoreMissing) {
        logger.warn("Could not access repository $repository branch $branch => skipping")
      } else {
        throw new RuntimeException("Failed to read from repository $repository ($branch)", e)
      }
    }
  }
}