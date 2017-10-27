import libs.*
import kotlinx.cinterop.*
import platform.UIKit.*
import platform.MapKit.*
import platform.objc.*
import platform.Foundation.*
import platform.CoreGraphics.*
import platform.CoreLocation.*
import platform.CoreData.*

fun UIView.applyCornerRadius(radius: CGFloat) {
    layer.setMasksToBounds(true)
    layer.setCornerRadius(radius)
}

fun UIImage.circularImage(): UIImage {
    val minEdge = this.size.useContents { minOf(height, width) }
    val size = CGSizeMake(minEdge, minEdge)

    UIGraphicsBeginImageContextWithOptions(size, false, 0.0)
    val context = UIGraphicsGetCurrentContext()

    fun makeRect(size: CValue<CGSize>) = size.useContents { CGRectMake(0.0, 0.0, width, height) }

    this.drawInRect(
            makeRect(size),
            blendMode = CGBlendMode.kCGBlendModeCopy, alpha = 1.0)

    CGContextSetBlendMode(context, CGBlendMode.kCGBlendModeCopy)
    CGContextSetFillColorWithColor(context, UIColor.clearColor.CGColor)

    val rectPath = UIBezierPath.bezierPathWithRect(makeRect(size))
    val circlePath = UIBezierPath.bezierPathWithOvalInRect(makeRect(size))
    rectPath.appendPath(circlePath)
    rectPath.usesEvenOddFillRule = true
    rectPath.fill()

    val result = UIGraphicsGetImageFromCurrentImageContext()!!
    UIGraphicsEndImageContext()

    return result
}

fun UIView.showIndeterminateProgress(title: String): MBProgressHUD {
    val hud = MBProgressHUD(this)
    hud.label.text = title
    hud.removeFromSuperViewOnHide = true
    this.addSubview(hud)
    hud.showAnimated(true)
    return hud
}

fun UIView.showPopupText(title: String, text: String = "", delay: Long = 1000) {
    val hud = MBProgressHUD(this)
    hud.label.text = title
    hud.detailsLabel.text = text
    hud.mode = MBProgressHUDMode.MBProgressHUDModeText
    hud.removeFromSuperViewOnHide = true
    this.addSubview(hud)
    hud.showAnimated(true)
    
    DispatchQueue.main.asyncAfter(ms = delay) {
        hud.hideAnimated(true)
    }
}

fun UIViewController.showPopupText(title: String, text: String = "", delay: Long = 1000) {
    view.showPopupText(title, text, delay)
}

fun UIViewController.showIndeterminateProgress(title: String): MBProgressHUD {
    return view.showIndeterminateProgress(title)
}

fun UIViewController.createErrorHandler(message: String? = null, additionalWork: (() -> Unit)? = null): (NSError) -> Unit {
    return { error ->
        additionalWork?.invoke()
        showPopupText(message ?: "An error occured", error.localizedDescription, 1500)
    }
}

fun NSManagedObjectContext.entityDescription(entityName: String): NSEntityDescription {
    return NSEntityDescription.entityForName(entityName, this)!!
}

fun openUrl(urlString: String) {
    UIApplication.sharedApplication.openURL(NSURL.URLWithString(urlString)!!)
}