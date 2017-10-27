import libs.*
import kotlinx.cinterop.*
import platform.Foundation.*
import platform.CoreGraphics.*
import platform.CoreData.*
import platform.darwin.*

private val bConst = BConst()

class NSErrorException(val error: NSError): Exception()

fun <T : Any> attempt(errorHandler: ((NSError) -> Unit)?, block: () -> T?): T? {
    try {
        return block()
    } catch (e: NSErrorException) {
        if (errorHandler != null) {
            errorHandler(e.error)
        } else {
        	log("${e.error}")
        }
        return null
    }
}

interface DispatchQueue {
    companion object {
        val main: DispatchQueue = object : DispatchQueue {
            override fun async(block: () -> Unit) {
                dispatch_async(dispatch_get_main_queue()) { block() }
            }

            override fun asyncAfter(ms: Long, block: () -> Unit) {
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, ms * NSEC_PER_MSEC), dispatch_get_main_queue()) {
                    block()
                }
            }
        }
    }

    fun async(block: () -> Unit)
    fun asyncAfter(ms: Long, block: () -> Unit)
}

fun NSManagedObjectContext.perform(block: () -> Unit) {
    bConst.performInContext(this, task = BTaskWithLambda(block))
}

fun NSManagedObjectContext.performAndWait(block: () -> Unit) {
    bConst.performAndWaitInContext(this, task = BTaskWithLambda(block))
}

private class BTaskWithLambda(private val block: () -> Unit) : BWork() {
    override fun work() { block() }
}

fun log(text: String) {
    //NSLog(text)
    bConst.logText(text)
}

inline fun <R> nsTry(block: (errorPtr: CPointer<ObjCObjectVar<NSError?>>) -> R): R = memScoped {
    val errorVar = alloc<ObjCObjectVar<NSError?>>()
    errorVar.value = null
    val result = block(errorVar.ptr)

    val error = errorVar.value
    if (error != null) {
        throw NSErrorException(error)
    }

    result
}

fun nsArrayOf(): NSArray {
    return NSArray.array()
}

fun nsArrayOf(obj: ObjCObject?): NSArray {
    return NSArray.arrayWithObject(obj)
}

operator fun NSArray.get(index: Int): Any? {
    return this.objectAtIndex(index.toLong())
}

operator fun NSArray.get(index: Long): Any? {
    return this.objectAtIndex(index)
}

fun nsArrayOf(vararg objects: ObjCObject?): NSArray {
    val arr = NSMutableArray.arrayWithCapacity(objects.size.toLong())
    for (obj in objects) {
        arr.addObject(obj)
    }
    return arr
}

fun <T> NSArray?.toList(): kotlin.collections.List<T> {
    if (this == null) return listOf()
    return objectEnumerator().toList()
}

fun <T> NSSet?.toList(): kotlin.collections.List<T> {
    if (this == null) return listOf()
    return objectEnumerator().toList()
}

fun <T> NSEnumerator.toList(): List<T> {
    val items = mutableListOf<T>()
    var obj = nextObject()
    while (obj != null) {
        items += obj.uncheckedCast<T>()
        obj = nextObject()
    }
    return items
}

fun String.toNSString(): NSString {
    return interpretObjCPointer<NSString>(CreateNSStringFromKString(this))
}
