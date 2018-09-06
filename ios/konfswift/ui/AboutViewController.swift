import UIKit
import MapKit
import AddressBook
import Contacts

class AboutViewController : UIViewController, MKMapViewDelegate {
    @IBOutlet private weak var map: MKMapView!

    override func viewDidLoad() {
        map.delegate = self

        let location = CLLocation(latitude: 37.805423, longitude: -122.401123)
        map.setRegion(MKCoordinateRegionMakeWithDistance(location.coordinate, 400, 400), animated: false)

        let confPlace = Place(title: "Pier 27", subtitle: "The Embarcadero, San Francisco", coordinate: location.coordinate)
        map.addAnnotation(confPlace)
    }

    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        guard annotation is Place else { return nil }

        let identifier = "pin"
        let view: MKPinAnnotationView
        if let dequeuedView = map.dequeueReusableAnnotationView(withIdentifier: identifier) as? MKPinAnnotationView {
            dequeuedView.annotation = annotation
            view = dequeuedView
        } else {
            view = MKPinAnnotationView(annotation: annotation, reuseIdentifier: identifier)
            view.canShowCallout = true
            view.calloutOffset = CGPoint(x: -5, y: 5)
            view.rightCalloutAccessoryView = UIButton(type: .detailDisclosure)
        }

        return view
    }

    func mapView(_ mapView: MKMapView, annotationView view: MKAnnotationView, calloutAccessoryControlTapped control: UIControl) {
        guard let place = view.annotation as? Place else { return }
        let mapItem = MKMapItem(placemark: MKPlacemark(coordinate: place.coordinate, addressDictionary: nil))
        mapItem.name = place.title
        _ = mapItem.openInMaps(launchOptions: nil)
    }
}

fileprivate class Place: NSObject, MKAnnotation {
    let title: String?
    let subtitle: String?
    let coordinate: CLLocationCoordinate2D

    init(title: String, subtitle: String, coordinate: CLLocationCoordinate2D) {
        self.title = title
        self.subtitle = subtitle
        self.coordinate = coordinate

        super.init()
    }
}
