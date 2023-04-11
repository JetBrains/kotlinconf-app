//
//  Break.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 06.03.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct Break: View {
    var duration: String
    var title: String
    
    var isLive: Bool
    var isBreak: Bool
    
    var breakIcon: String {
        isLive ? DesignSystem.breakActiveIcon : DesignSystem.breakIcon
    }
    
    var lunchIcon: String {
        isLive ? DesignSystem.lunchIcon : DesignSystem.lunchActiveIcon
    }
    
    @State private var opacity = 0.0
    
    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Divider()
            }
            .frame(height: 24)
            Divider()
            HStack {
                Text(duration.uppercased())
                    .font(DesignSystem.kcH4)
                    .foregroundColor(DesignSystem.greyWhiteColor)
                
                Text("/ \(title)")
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.greyWhiteColor)
                
                Spacer()
                
                let icon = isBreak ? breakIcon : lunchIcon
                Image(icon)
                    .foregroundColor(isLive ? DesignSystem.orangeColor : DesignSystem.grey50Color)
                    .opacity(isLive ? opacity : 1.0)
                    .onAppear {
                        withAnimation(.linear(duration: 2.0).repeatForever()) {
                            opacity += 1.0
                        }
                    }
            }
            .padding(16)
            Divider()
            HStack {
                Divider()
            }
            .frame(height: 24)
            Divider()
        }
        .background(DesignSystem.whiteGreyColor)
    }
}

struct Break_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 0) {
            Break(duration: "30 Min", title: "Lunch", isLive: false, isBreak: false)
            Break(duration: "30 Min", title: "Lunch", isLive: true, isBreak: false)
            Break(duration: "30 Min", title: "Lunch", isLive: false, isBreak: true)
            Break(duration: "30 Min", title: "Lunch", isLive: true, isBreak: true)
        }
    }
}
