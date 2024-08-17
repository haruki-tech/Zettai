package com.example

import org.http4k.core.*
import org.http4k.routing.path
import org.http4k.server.asServer
import org.http4k.server.Jetty

data class DataZettai(val lists: Map<User, List<ToDoList>>): HttpHandler{
    //routes functions are not changed...
    private fun showList(request: Request): Response =
        request.let(::extractListData)
            .let(::fetchListContent)
            .let(::renderHtml)
            .let(::createResponse)
    fun extractListData(request: Request): Pair<User, ListName> {
        val user = request.path("user").orEmpty()
        val list = request.path("list").orEmpty()
        return User(user) to ListName(list)
    }
    fun fetchListContent(listId: Pair<User, ListName>): ToDoList =
        lists[listId.first]
            ?.firstOrNull { it.listName == listId.second }
            ?: error("List unknown")
    fun renderHtml(todoList: ToDoList): HtmlPage =
        HtmlPage("""
<html>
<body>
<h1>Zettai</h1>
<h2>${todoList.listName.name}</h2>
<table>
<tbody>${renderItems(todoList.items)}</tbody>
</table>
</body>
</html>
""".trimIndent()
        )
    fun renderItems(items: List<ToDoItem>) =
        items.map {
            """<tr><td>${it.description}</td></tr>""".trimIndent()
        }.joinToString("")
    fun createResponse(html: HtmlPage): Response =
        Response(Status.OK).body(html.raw)
}

private fun startTheApplication(user: String, listName: String, items: List<String>) {
    val toDoList = ToDoList(ListName(listName), items.map(::ToDoItem))
    val lists = mapOf(User(user) to listOf(toDoList) )
    val server = Zettai(lists).asServer(Jetty(8081)) //different from main
    server.start()
}

private fun parseResponse(html: String): ToDoList {
    val nameRegex = "<h2>.*<".toRegex()
    val listName = ListName(extractListName(nameRegex, html))
    val itemsRegex = "<td>.*?<".toRegex()
    val items = itemsRegex.findAll(html)
        .map { ToDoItem(extractItemDesc(it)) }.toList()
    return ToDoList(listName,items)
}
private fun extractListName(nameRegex: Regex, html: String): String =
    nameRegex.find(html)?.value
        ?.substringAfter("<h2>")
        ?.dropLast(1)
        .orEmpty()
private fun extractItemDesc(matchResult: MatchResult): String =
    matchResult.value.substringAfter("<td>").dropLast(1)

