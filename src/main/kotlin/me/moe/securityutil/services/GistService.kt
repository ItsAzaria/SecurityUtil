package me.moe.securityutil.services

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.jakejmattson.discordkt.annotations.Service
import me.moe.securityutil.utilities.Constants
import org.kohsuke.github.GitHubBuilder
import java.util.*

val github = GitHubBuilder().withOAuthToken(Constants.GITHUB_TOKEN).build() ?: throw(Exception("Incorrect github auth details."))

@DelicateCoroutinesApi
@Service
class GistService() {
    fun postGist(content: String) {
        val gist = github.createGist().file("tokens.txt", content).public_(true).create()

        GlobalScope.launch {
            delay(30000) // 30 seconds

            gist.delete()
        }

    }


    fun removeGists() {
        val oneMinuteAgo = Date().time - 60000
        val gists = github.myself.listGists().toList()

        gists.forEach {
            if (it.createdAt.time < oneMinuteAgo) {
                it.delete()
            }
        }

        if (gists.size > 0)
            println("Removed ${gists.size} gists")

        GlobalScope.launch {
            delay(300000) // 5 minutes
            removeGists()
        }
    }
}