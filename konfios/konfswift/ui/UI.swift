//
//  UI.swift
//  konfswift
//
//  Created by Marcin Moskala on 15/09/2018.
//  Copyright © 2018 Yan Zhulanow. All rights reserved.
//

import Foundation
import UIKit
import TagListView_ObjC
import konfios
import MBProgressHUD

public class UI: KTKotlinx_coroutines_core_nativeCoroutineDispatcher {
    override public func dispatch(context: KTStdlibCoroutineContext, block: KTKotlinx_coroutines_core_nativeRunnable) {
        DispatchQueue.main.async {
            block.run()
        }
    }
}
