import libs.*
import kotlinx.cinterop.*
import platform.UIKit.*
import platform.MapKit.*
import platform.objc.*
import platform.darwin.*
import platform.Foundation.*
import platform.CoreGraphics.*
import platform.CoreLocation.*

fun main(args: Array<String>) {
    memScoped {
        val argc = args.size + 1
        val argv = (arrayOf("konan") + args).map { it.cstr.getPointer(memScope) }.toCValues()

        autoreleasepool {
            UIApplicationMain(argc, argv, null, NSStringFromClass(AppDelegate))
        }
    }
}
