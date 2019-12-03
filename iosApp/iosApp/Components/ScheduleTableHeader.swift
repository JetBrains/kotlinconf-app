import Foundation
import UIKit

class ScheduleTableHeader : UITableViewHeaderFooterView {
    @IBOutlet weak var monthLabel: UILabel!
    @IBOutlet weak var dayLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!

    func configureLook(month: String, day: Int, title: String) {
        monthLabel.text = month.uppercased()
        dayLabel.text = "0" + String(day)
        titleLabel.attributedText = TextWithLineHeight(text: title, height: 54)

        let color = { () -> UIColor in
            if (title.contains("PARTY")) {
                return UIColor.redOrange
            } else {
                return UIColor.dark
            }
        }()

        titleLabel.textColor = color
    }
}
