import kotlinx.cinterop.*
import platform.UIKit.*
import platform.MapKit.*
import platform.Foundation.*
import platform.CoreGraphics.*
import platform.CoreLocation.*

@ExportObjCClass
class AboutViewController : UIViewController, MKMapViewDelegateProtocol {
    companion object {
        val SHOW_BADGE_KEY = "showBadge"
    }

    @ObjCOutlet lateinit var map: MKMapView
    @ObjCOutlet lateinit var badge: UIView

    constructor(aDecoder: NSCoder) : super(aDecoder)
    override fun initWithCoder(aDecoder: NSCoder) = initBy(AboutViewController(aDecoder))

    override fun viewDidLoad() {
        map.applyCornerRadius(6.0)
        
        map.delegate = this

        val location = CLLocation(37.805423, longitude = -122.401123)
        map.setRegion(MKCoordinateRegionMakeWithDistance(location.coordinate(), 800.0, 800.0), animated = false)

        val confPlace = MKPointAnnotation().apply {
            setCoordinate(location.coordinate())
            setTitle("Pier 27")
        }

        badge.hidden = !NSUserDefaults.standardUserDefaults.boolForKey(SHOW_BADGE_KEY)

        map.addAnnotation(confPlace)
    }
    
    @ObjCAction 
    fun onMapClick(sender: ObjCObject?) {
        val place = map.annotations.firstObject.uncheckedCast<MKPointAnnotation>()
        val mapItem = MKMapItem(MKPlacemark(place.coordinate(), addressDictionary = null))
        mapItem.name = place.title()?.toString()
        mapItem.openInMapsWithLaunchOptions(null)
    }
    
    @ObjCAction 
    fun onTwitterClick(sender: ObjCObject?) {
        openUrl("https://twitter.com/kotlinconf")
    }
    
    @ObjCAction 
    fun onWebsiteClick(sender: ObjCObject?) {
        openUrl("https://www.kotlinconf.com/")
    }
    
    override fun mapView(mapView: MKMapView, viewForAnnotation: MKAnnotationProtocol): MKAnnotationView? {
        val identifier = "pin"
        
        val dequeuedView = map.dequeueReusableAnnotationViewWithIdentifier(identifier)
        if (dequeuedView is MKPinAnnotationView) {
            dequeuedView.annotation = viewForAnnotation
            return dequeuedView
        }

        return MKPinAnnotationView(viewForAnnotation, reuseIdentifier = identifier).apply {
            canShowCallout = true
            calloutOffset = CGPointMake(-5.0, 5.0)
            rightCalloutAccessoryView = UIButton.buttonWithType(UIButtonTypeDetailDisclosure)
        }
    }
}
