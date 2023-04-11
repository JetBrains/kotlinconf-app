//
//  AgendaHeader.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 09.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct AgendaHeader: View {
    var title: String
    var isLive = false
    
    @State private var opacity = 0.0
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .center, spacing: 0) {
                Text(title)
                    .font(DesignSystem.kcH2)
                    .foregroundColor(DesignSystem.greyGrey5Color)
                    .padding(.leading, 16)
                    .padding(.top, 16)
                    .padding(.bottom, 16)
                
                Spacer()
                if isLive {
                    Circle()
                        .frame(width: 12, height: 12)
                        .foregroundColor(DesignSystem.orangeColor)
                        .opacity(opacity)
                        .onAppear {
                            withAnimation(.linear(duration: 1.0).repeatForever()) {
                                opacity += 1.0
                            }
                        }
                    
                    Text("NOW")
                        .foregroundColor(DesignSystem.orangeColor)
                        .font(DesignSystem.kcT2)
                        .padding(.leading, 6)
                }
            }
            .padding(.trailing, 16)
            .frame(height: 68)
            
            Divider()
        }
        .background(DesignSystem.grey5BlackColor)
    }
}

struct AgendaHeader_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 0, content: {
            AgendaHeader(title: "12:00-12-40", isLive: true)
            AgendaHeader(title: "12:00-12-40", isLive: false)
            AgendaHeader(title: "12:00-12-40", isLive: false)
            AgendaHeader(title: "12:00-12-40", isLive: true)
        })
    }
}
