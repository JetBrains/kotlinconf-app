//
//  ViewController.h
//  konan
//
//  Created by jetbrains on 30/08/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SessionsViewController : UIViewController

//@property (weak, nonatomic) IBOutlet UIRefreshControl *pullToRefresh;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

- (IBAction)tabSelected:(id)sender;
- (IBAction)refreshSessions:(id)sender;

@end

