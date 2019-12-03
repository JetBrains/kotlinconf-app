import UIKit
import MarqueeLabel

class ScheduleTableSmallHeader : UITableViewHeaderFooterView {
    @IBOutlet weak var title: MarqueeLabel!

    func displayDay(title: String) {
        self.title.text = title
        self.title.textColor = UIColor.dayGray

        self.title.restartLabel()
        self.title.speed = .rate(21.0)
    }
}
