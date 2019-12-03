import UIKit
import KotlinConfAPI

let partnersNames = [
    "android",
    "47",
    "freenow",
    "bitrise",
    "instill",
    "gradle",
    "n26",
    "kodein",
    "badoo",
    "jetbrains"
]

let partnersLogos = [
    "android": "logoAndroidBig",
    "47": "logo47Big",
    "freenow": "logoFreenowBig",
    "bitrise": "logoBitriseBig",
    "instill": "logoInstlBig",
    "gradle": "logoGradleBig",
    "n26": "logoN26Big",
    "kodein": "logoKodeinBig",
    "badoo": "logoBadooBig",
    "jetbrains": "logoJetBrains"
]

let ConfPartners = Partners()

class PartnerController : UIViewController {
    @IBOutlet weak var partnerLink: UIButton!
    @IBOutlet weak var partnerDescription: UILabel!
    @IBOutlet weak var titleText: UILabel!
    @IBOutlet weak var logo: UIImageView!
    
    var partnerId: Int!
    
    override func viewDidLoad() {
        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .dark
        }

        let name = partnersNames[partnerId]
        let partner = ConfPartners.partner(name: name)

        if (partner == nil) {
            return
        }
        
        titleText.text = partner!.title
        partnerDescription.text = ConfPartners.descriptionByName(name: name)
        partnerLink.setTitle(partner!.url, for: .normal)
        
        logo.image = UIImage(named: partnersLogos[name]!)
    }

    @IBAction func backButtonTouch(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    @IBAction func websiteTouch(_ sender: Any) {
        let urlString = (sender as! UIButton).titleLabel!.text!
        let url = URL(string: urlString)

        UIApplication.shared.open(url!)
    }
}
