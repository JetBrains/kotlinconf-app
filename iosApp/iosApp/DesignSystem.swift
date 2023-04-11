//
//  Styles.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 04.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct DesignSystem {
    public static let kcT2Bold: Font = Font.custom("JetBrainsMono-Bold", size: 16)
    
    public static let kcH2: Font = Font.custom("JetBrainsMono-ExtraBold", size: 32)
    public static let kcH2Uppercase = Font.custom("JetBrainsMono-ExtraBold", size: 32)
    public static let kcH3 = Font.custom("JetBrainsMono-ExtraBold", size: 18)
    
    public static let kcT3 = Font.custom("JetBrainsMono-Light", size: 16)
    public static let t2UppercaseBold = Font.custom("JetBrainsMono-Bold", size: 16)
    public static let kcT3Uppercase = Font.custom("JetBrainsMono-Light", size: 14)
    
    public static let kcT2Uppercase = Font.custom("JetBrainsMono-Light", size: 16)
    public static let kcT2 = Font.custom("JetBrainsMono-Regular", size: 16)
    public static let kcH4 = Font.custom("JetBrainsMono-ExtraBold", size: 16)
    // New Colors
    
    public static let grey5BlackColor = Color("grey-5|black")
    public static let grey5GreyColor = Color("grey-5|grey")
    public static let grey20Grey50Color = Color("grey-20|grey-50")
    public static let grey50Grey20Color = Color("grey-50|grey-20")
    public static let grey50WhiteColor = Color("grey-50|white")
    public static let grey50Color = Color("grey-50|grey-50")
    public static let greyGrey5Color = Color("grey|grey-5")
    public static let greyGrey50Color = Color("grey|grey-50")
    public static let greyGrey20Color = Color("grey|grey-20")
    public static let greyWhiteColor = Color("grey|white")
    public static let whiteGreyColor = Color("white|grey")
    public static let whiteBlackColor = Color("white|black")
    public static let blackWhiteColor = Color("black|white")
    public static let greyColor = Color("grey")
    public static let whiteColor = Color("white")
    
    
    public static let orangeColor = Color("orange")
    public static let violetColor = Color("violet")
    
    // Logo
    public static let menuLogo = "menu-logo"
    public static let birdLogo = "bird"
    public static let partyLogo = "party"
    public static let aboutLogo = "about"
    public static let closingLogo = "closing"
    public static let privacyPolicyLogo = "privacy-policy"
    public static let notificationsLogo = "notifications"

    // Icons
    public static let timeIcon = "time"
    public static let menuIcon = "menu"
    public static let menuActiveIcon = "menu-active"
    public static let locationIcon = "location"
    public static let speakersIcon = "speakers"
    public static let bookmarksIcon = "bookmarks"
    public static let timeActiveIcon = "time-active"
    public static let locationActiveIcon = "location-active"
    public static let speakersActiveIcon = "speakers-active"
    public static let bookmarksActiveIcon = "bookmarks-active"
    
    public static let searchIcon = "search"
    public static let bookmarkIcon = "bookmark"
    public static let bookmarkActiveIcon = "bookmark-active"
    public static let backIcon = "back"
    public static let closeIcon = "close"
    
    public static let codeLabIcon = "code-lab"
    
    public static let arrowLeftIcon = "arrow-left"
    public static let faceHappyIcon = "face-happy"
    public static let faceNeutralIcon = "face-neutral"
    public static let faceSadIcon = "face-sad"

    public static let faceHappyActiveIcon = "face-happy-active"
    public static let faceNeutralActiveIcon = "face-neutral-active"
    public static let faceSadActiveIcon = "face-sad-active"
    
    public static let lightIcon = "light"
    
    public static let lunchIcon = "lunch"
    public static let breakIcon = "break"
    public static let lunchActiveIcon = "lunch-active"
    public static let breakActiveIcon = "break-active"
}

extension View {
    @ViewBuilder func `if`<Content: View>(_ condition: Bool, transform: (Self) -> Content) -> some View {
        if condition {
            transform(self)
        } else {
            self
        }
    }
}
