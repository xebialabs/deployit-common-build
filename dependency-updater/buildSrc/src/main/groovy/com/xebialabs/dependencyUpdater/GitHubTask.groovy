import org.gradle.api.DefaultTask

import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.JSON
import org.apache.http.*
import org.apache.http.protocol.*
import org.apache.http.auth.*

class GitHubTask extends DefaultTask {
  String username = "${ -> project.githubOauthToken }"
  String password = 'x-oauth-basic'
  String organization = 'xebialabs'
  String contentPath
  def ignoreMissing = true
  def branch = "${ -> project.branch}"

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

  def checkBranch() {
    try {
      if (!"${branch}".trim()) {
        throw new RuntimeException("Task ${name} requires you to specify a branch, ex. -Pbranch=master")
      }
    } catch (MissingPropertyException e) {
      throw new RuntimeException("Task ${name} requires you to specify a branch, ex. -Pbranch=master", e)
    }
  }

  def doWithRepo(def repository, Closure action) {
    try {
      def contentUrl = "/repos/$organization/$repository/contents/$contentPath"
      def response = github().get(path: contentUrl, query: ['ref': "${branch}".toString()])
      def content = new String(response.data.content.decodeBase64(), 'UTF-8');
      def sha = response.data.sha
      action(contentUrl, content, sha)
    } catch (RuntimeException e) {
      if(ignoreMissing) {
        logger.warn("Could not access repository $repository branch $branch => skipping")
      } else {
        throw new RuntimeException("Failed to read from repository $repository ($branch)", e)
      }
    }
  }
}