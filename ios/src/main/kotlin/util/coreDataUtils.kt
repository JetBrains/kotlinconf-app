import libs.*
import platform.Foundation.*
import platform.CoreData.*

private val bConst = BConst()

fun NSManagedObjectContext.deleteAll(entityName: String) {
    val fetchRequest = NSFetchRequest.fetchRequestWithEntityName(entityName)
    fetchRequest.includesPropertyValues = false

    for (item in executeFetchRequest(fetchRequest).toList<NSManagedObject>()) {
        deleteObject(item)
    }
}

fun NSManagedObjectContext.saveRecursively() {
    this.save()

    var current: NSManagedObjectContext? = this.parentContext
    while (current != null) {
        val context = current
        var error: NSError? = null

        context.performAndWait {
            try {
                context.save()
            } catch (e: NSErrorException) {
                error = e.error
            }
        }

        error?.let { throw NSErrorException(it) }
        current = current.parentContext
    }
}

fun NSURLSession.dataTask(request: NSURLRequest, handler: (NSData?, NSURLResponse?, NSError?) -> Unit): NSURLSessionDataTask {
    return bConst.dataTaskWithSession(this, request = request, handler = object : BResponse() {
        override fun completedWithData(data: NSData?, response: NSURLResponse?, error: NSError?) {
            handler(data, response, error)
        }
    })!!
}

fun NSManagedObjectContext.executeFetchRequest(request: NSFetchRequest): NSArray {
    return nsTry { errorPtr -> this.executeFetchRequest(request, error = errorPtr)!! }
}

fun NSManagedObjectContext.save() {
    nsTry { errorPtr -> save(error = errorPtr) }
}
