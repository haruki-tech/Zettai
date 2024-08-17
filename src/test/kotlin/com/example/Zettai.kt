package com.example

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

class Zettai():HttpHandler{
        val routes = routes(
            "/todo/{user}/{list}" bind Method.GET to ::showList
        )

        override fun invoke(req: Request): Response = routes(req)

        private fun showList(req: Request): Response {
            val user = req.path("user").orEmpty()
            val list = req.path("list").orEmpty()
            val htmlPage = """
            <html>
            <body>
            <h1>Zettai</h1>
            <p>Here is the list <b>$list</b> of user<b>$user</b></p>
            </body>
            </html>"""
            return Response(Status.OK).body(htmlPage)
        }
    }

    data class ToDoList(val listName: ListName, val items: List<ToDoItem>, val name: Any)
    data class ListName(val name: String)

    data class User(val name: String)

    data class ToDoItem(val description: String)
    enum class ToDOStatus { Todo, InProgress, Done, Blocked}

    data class HtmlPage(val raw: String)

    fun extractListData(request: Request): Pair<User, ListName> = TODO()
    fun fetchListContent(listId: Pair<User, ListName>): ToDoList = TODO()
    fun renderHtml(list: ToDoList): HtmlPage = TODO()
    fun createResponse(html: HtmlPage): Response = TODO()

    fun getToDoList(request: Request): Response =
        request
            .let(::extractListData)
            .let(::fetchListContent)
            .let(::renderHtml)
            .let(::createResponse)

fun main() {
    val items = listOf("write chapter", "insert code", "draw diagrams")
    val toDoList = ToDoList(ListName("book"), items.map(::ToDoItem))
    val lists = mapOf(User("uberto") to listOf(toDoList) )
    val app: HttpHandler = Zettai(lists)
    app.asServer(Jetty(8080)).start() //starting the server
    println("Server started at http://localhost:8080/todo/uberto/book")
}

