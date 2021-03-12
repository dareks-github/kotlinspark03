import com.google.gson.Gson
import spark.Request
import spark.Response
import spark.kotlin.Http
import spark.kotlin.ignite

private var list = mutableListOf<Car>()

// trzeba pokazać na postmanie i na przegladarce

fun main() {

    val http: Http = ignite()

    with(http) {
        port(getHerokuAssignedPort())
        staticFiles.location("/public")
        get("/hello") { "Hello Spark Kotlin!" } // ok
        get("/info") { info(request, response) } //ok
        get("/add") { add(request, response) } //ok
        get("/text") { text(request, response) } //ok
        get("/json") { json(request, response) } //ok
        get("/html") { html(request, response) } //ok
        get("/deleteall") { deleteAll(request, response) } //ok
        get("/delete/:id") { delete(request, response) }
        get("/update/:id") { update(request, response) }

    }

}

fun getHerokuAssignedPort(): Int {
    val processBuilder = ProcessBuilder()
    return if (processBuilder.environment()["PORT"] != null) {
        processBuilder.environment()["PORT"]!!.toInt()
    } else 5000
    //return default port if heroku-port isn't set (i.e. on localhost)
}

fun info(request: Request, res: Response): String {
    println(res.status());
    println(request.requestMethod());
    println(request.attributes());
    println(request.cookies());
    println(request.params());
    println(request.uri());
    println(request.url());
    println(request.queryParams());
    println(request.pathInfo());
    println(request.contentLength());
    println(request.contentType());
    println(request.protocol());
    println(request.headers());
    //
    return "request info";
}

//dodanie elementu do listy - ok

fun add(req: Request, res: Response): String {

    // dane z query - lista presłanych

    println(req.queryParams())

    //pobranie wartości jednego
    //println(req.queryParams("country"))

    val id = if (list.size == 0) list.size + 1 else list.last().id + 1

    val model = req.queryParams("model") ?: "default model" // elvis operator!
    val damaged = req.queryParams("damaged") != null

    val doors = Integer.parseInt(req.queryParams("doors") ?: "0")
    val country = req.queryParams("country") ?: "default country"

    println("$model $country $doors $damaged")

    list.add(Car(id, model, damaged, doors, country))

    return "car added to list, list size = ${list.size}"
}

//pobranie wszystkich elementów listy i zwrot textu

fun text(req: Request, res: Response): String {
    res.type("text/plain")
    return Gson().toJson(list)
}

//pobranie wszystkich elementów listy i zwrot json-a - ok

fun json(req: Request, res: Response): String {
    res.type("application/json")
    return Gson().toJson(list)
}

//pobranie wszystkich elementów listy i zwrot html-a - ok

fun html(req: Request, res: Response): String {
    res.type("text/html") // typ zwracany strony
    val table = StringBuilder()
    table.append("<html><head></head><body><table border=1>")
    list.forEach {
        table.append("<tr>")
        table.append("<td>")
        table.append(it.id)
        table.append("</td><td>")
        table.append("<td>")
        table.append(it.model)
        table.append("</td><td>")
        table.append(it.damaged)
        table.append("</td><td>")
        table.append(it.doors)
        table.append("</td><td>")
        table.append(it.country)
        table.append("</td>")
        table.append("</tr>")
    }
    table.append("</table><body></html>")

    // String json = new Gson().toJson(list);
    return table.toString()
}

// aktualizacja jednego elementu listy

fun update(req: Request, res: Response): String {
    var id:Int?=null
    try {
        id = Integer.parseInt(req.params("id") ?: "0")
    }catch (e:Exception){
        println(e.message)
    }

    println(id)
    val toupdate = list.filter { it.id == id }
    println(toupdate.size == 0)
    if (toupdate.size != 0) {
        toupdate[0].damaged = !toupdate[0].damaged
        return "zaktualizowane:  ${req.params(":id")} na $toupdate.damaged"
    } else {
        return "update - brak samochodu z id = $id"
    }


}

//usunięcie jednego elementu listy

fun delete(req: Request, res: Response): String {
    var id:Int?=null
    try {
        id = Integer.parseInt(req.params("id") ?: "0")
    }catch (e:Exception){
        println(e.message)
    }
    val todelete = list.filter { it.id == id }

    if (todelete.size != 0) {
        list.remove(todelete[0])
        return "usuniete: " + req.params(":id")
    } else {
        return "delete - brak samochodu z id = $id"
    }





}

//usunięcie wszystkich elementów listy

fun deleteAll(req: Request, res: Response): String {
    list.clear()
    return "lista jest pusta"
}
