import UIKit

class AfterController : UIViewController {
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!

    override func viewDidLoad() {
        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .dark
        }

        let recognizer = UITapGestureRecognizer(target: self, action: #selector(openWebsite))
        descriptionLabel.addGestureRecognizer(recognizer)

        let smallScreen = UIScreen.main.bounds.width < 375
        let lineHeight = CGFloat(smallScreen ? 40.0 : 60.0)
        let fontSize = CGFloat(smallScreen ? 40.0 : 60.0)

        let style = NSMutableParagraphStyle()
        style.minimumLineHeight = lineHeight
        style.maximumLineHeight = lineHeight

        let attributedString = NSMutableAttributedString(
            string: "KotlinConf\nis over. \nThank you all.\nit was great".uppercased(), attributes: [
          .font: UIFont(name: "BigShouldersDisplay-ExtraBold", size: fontSize)!,
          .foregroundColor: UIColor.white
        ])
        attributedString.addAttribute(.foregroundColor, value: UIColor.redOrange, range: NSRange(location: 0, length: 10))
        attributedString.addAttribute(.paragraphStyle, value: style, range: NSRange(location: 0, length: attributedString.length))

        titleLabel.attributedText = attributedString
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.tintColor = UIColor.redOrange
    }

    @objc func openWebsite(_ sender: Any) {
        let url = URL(string: "https://kotlinconf.com")
        UIApplication.shared.open(url!)
    }
}
