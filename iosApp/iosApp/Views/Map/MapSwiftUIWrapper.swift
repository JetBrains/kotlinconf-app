import Foundation
import SwiftUI
import MapboxMaps
import CoreLocation

struct MapSwiftUIWrapper : UIViewControllerRepresentable {
    let url: String
    let onClick: (String, String, String) -> Void
    var cameraOptions: CameraOptions? = nil
    var isFrozen: Bool = false

    func makeUIViewController(context: Context) -> MapViewController {
        return MapViewController(url: url, onClick: onClick, cameraOptions: cameraOptions, isFrozen: isFrozen)
    }
      
    func updateUIViewController(_ uiViewController: MapViewController, context: Context) {
        uiViewController.mapView.mapboxMap.loadStyleURI(StyleURI(rawValue: url)!)
    }
}

class MapViewController: UIViewController {
    internal var mapView: MapView!
    let url: String
    let onClick: (String, String, String) -> Void
    var cameraOptions: CameraOptions? = nil
    var isFrozen: Bool = false

    init(url: String, onClick: @escaping (String, String, String) -> Void, cameraOptions: CameraOptions?, isFrozen: Bool) {
        self.url =  url
        self.onClick = onClick
        self.cameraOptions = cameraOptions
        self.isFrozen = isFrozen
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override public func viewDidLoad() {
        super.viewDidLoad()
        let resourceOptions = ResourceOptions(accessToken: "")
        let options = MapInitOptions(
            resourceOptions: resourceOptions,
            styleURI: StyleURI(rawValue: url)
        )

        mapView = MapView(frame: view.bounds, mapInitOptions: options)
        mapView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        mapView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(onMapClick)))
        
        if isFrozen {
            mapView.isUserInteractionEnabled = false
        }

        if let camera: CameraOptions = cameraOptions {
            mapView.mapboxMap.setCamera(to: camera)
        }
        
        self.view.addSubview(mapView)
    }

    @objc private func onMapClick(_ sender: UITapGestureRecognizer) {
        let screenPoint = sender.location(in: mapView)
        mapView.mapboxMap.queryRenderedFeatures(with: screenPoint) { [weak self] result in
        switch result {
            case .success(let features):
                guard let props = features.first?.feature.properties else { return }
                guard props["Click"] != nil else { return }
                
                guard let name = props["Name"]??.rawValue as? String else { return }
                guard let description = props["Description"]??.rawValue as? String else { return }
            
                if let displayName = props["DisplayName"]??.rawValue as? String {
                    self?.onClick(name, description, displayName)
                } else {
                    self?.onClick(name, description, name)
                }
                
                
                break
            case .failure(_): break
            }
        }
    }
}


public let EXHIBITION = CameraOptions(
    center: CLLocationCoordinate2D(latitude: 52.374888312974583, longitude: 4.8958040599143544),
    zoom: 18.5,
    bearing: 126.69985108898653
)

public let GROUD_ROOMS = CameraOptions(
    center: CLLocationCoordinate2D(latitude: 52.375221940889247, longitude: 4.8966121025625),
    zoom: 18.5,
    bearing: 126.69985108898653
)

public let FLOOR1_ROOMS = CameraOptions(
    center: CLLocationCoordinate2D(latitude: 52.37500901272729, longitude: 4.8962740928733979),
    zoom: 18.5,
    bearing: 126.69985108898653
)


public let BERLAGE_ROOM = CameraOptions(
    center: CLLocationCoordinate2D(latitude: 52.374625105863458, longitude: 4.895626846724042),
    zoom: 18.5,
    bearing: 126.69985108898653
)

public let VERWEY_KAMER = CameraOptions(
    center: CLLocationCoordinate2D(latitude: 52.374783815163447, longitude: 4.8960855441916067),
    zoom: 18.5,
    bearing: 126.69985108898653
)
