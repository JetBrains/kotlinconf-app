//
//  MenuItem.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 04.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct MenuItem: View {
    var text: String
    var icon: String = DesignSystem.arrowLeftIcon
    
    var body: some View {
        VStack(spacing: 0) {
            HStack() {
                Text(text)
                    .font(DesignSystem.kcT2Uppercase)
                    .padding(EdgeInsets(top: 18, leading: 0, bottom: 18, trailing: 0))
                    .foregroundColor(DesignSystem.greyWhiteColor)

                Spacer()
                Image(icon)
                    .foregroundColor(DesignSystem.greyWhiteColor)
            }
            .padding(EdgeInsets(top:0, leading: 16, bottom: 0, trailing: 16))
            Divider()
        }
        .background(DesignSystem.whiteGreyColor)
    }
}

struct TwitterIcon: View {
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("TWITTER")
                .font(DesignSystem.kcT2Uppercase)
                .foregroundColor(DesignSystem.greyWhiteColor)
            Text("#KOTLINCONF23")
                .font(DesignSystem.kcT2Uppercase)
                .foregroundColor(DesignSystem.grey50Color)
            
            HStack {
                Spacer()
                Image("twitter")
                    .foregroundColor(DesignSystem.greyWhiteColor)
                    .padding(.bottom, 20)
            }
            .padding(.top, 60)
        }
        .padding(.top, 16)
        .padding(.leading, 16)
        .padding(.trailing, 16)
        .background(DesignSystem.whiteGreyColor)
    }
}


struct SlackIcon: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("SLACK CHANNEL")
                .font(DesignSystem.kcT2Uppercase)
                .foregroundColor(DesignSystem.greyWhiteColor)
            
            HStack {
                Spacer()
                Image("slack")
                    .foregroundColor(DesignSystem.greyWhiteColor)
                    .padding(.bottom, 20)
            }
            .padding(.top, 80)
        }
        .padding(.top, 16)
        .padding(.leading, 16)
        .padding(.trailing, 16)
        .background(DesignSystem.whiteGreyColor)
    }
}

struct MenuItem_Previews: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: 0) {
            MenuItem(text: "SEARCH", icon: DesignSystem.searchIcon)
            MenuItem(text: "CODE OF CONDUCT")
            VStack(spacing: 0) {
                
                Divider()
                HStack(alignment: .top, spacing: 0) {
                    TwitterIcon()
                    Divider()
                    SlackIcon()
                }
                .frame(height: 180)
            }
            .padding(.top, 16)
            Divider()
        }
    }
}
