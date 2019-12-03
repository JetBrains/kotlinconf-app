import UIKit
import KotlinConfAPI

class BeforeController : UIViewController {
    private var timerWatcher: Ktor_ioCloseable!
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var daysText: UILabel!
    @IBOutlet weak var hoursText: UILabel!
    @IBOutlet weak var minutesText: UILabel!
    @IBOutlet weak var secondsText: UILabel!

    override func viewDidLoad() {
        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .dark
        }

        let smallScreen = UIScreen.main.bounds.width < 375
        let lineHeight = CGFloat(smallScreen ? 40.0 : 60.0)
        let fontSize = CGFloat(smallScreen ? 40.0 : 60.0)

        let style = NSMutableParagraphStyle()
        style.minimumLineHeight = lineHeight
        style.maximumLineHeight = lineHeight

        let attributedString = NSMutableAttributedString(
            string: "KotlinConf\nis just around\nthe corner".uppercased(), attributes: [
          .font: UIFont(name: "BigShouldersDisplay-ExtraBold", size: fontSize)!,
          .foregroundColor: UIColor.white
        ])
        attributedString.addAttribute(.foregroundColor, value: UIColor.redOrange, range: NSRange(location: 0, length: 10))
        attributedString.addAttribute(.paragraphStyle, value: style, range: NSRange(location: 0, length: attributedString.length))

        titleLabel.attributedText = attributedString
    }

    override func viewDidAppear(_ animated: Bool) {
        timerWatcher = Conference.homeState.watch { state in
            if let timer = state as? HomeState.Before {
                self.daysText.text = String(timer.days)
                self.hoursText.text = String(timer.hours)
                self.minutesText.text = String(timer.minutes)
                self.secondsText.text = String(timer.seconds)
            } else {
                self.timerWatcher.close()
                self.navigationController?.popViewController(animated: false)
            }
        }
    }

    override func viewDidDisappear(_ animated: Bool) {
        timerWatcher?.close()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.tintColor = UIColor.redOrange
    }
}
