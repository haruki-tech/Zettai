package com.example

import org.http4k.client.JettyClient
import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.fail
import strikt.api.DescribeableBuilder

class SeeATodoListAT {
    @Test
    fun `List owners can see their lists`() {
        val user = "frank"
        val listName = "shopping"
        val foodToBuy = listOf("carrots", "apples", "milk")

        startTheApplication(user, listName, foodToBuy)

        val list = getToDoList(user, listName)

        expectThat(list.name).isEqualTo(listName)
        expectThat(list.items).isEqualTo(foodToBuy)
    }
}

private fun <T> DescribeableBuilder<T>.isEqualTo(expected: List<String>) {
    TODO("Not yet implemented")
}

fun getToDoList(user: String, listName: String): ToDoList {
    val client = JettyClient()
    val request = Request(Method.GET, "http://localhost:8081/todo/$user/$listName")
    val response = client(request)
    return if (response.status == Status.OK)
        parseResponse(response.toString())
        else
            fail(response.toMessage())
}

fun parseResponse(html: String): ToDoList = TODO("parse the response")

fun startTheApplication(user: String, listName: String, items: List<String>) {
    val server = Zettai().asServer(Jetty(8081))
}
