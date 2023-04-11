//
//  AgendaDayRibbon.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 24.01.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct AgendaDayRibbon: View {
    var day: String
    var text: String {
        String(repeating: (day + "  ").uppercased(), count: 100)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Spacer()
                Divider()
                Spacer()
            }
            .frame(height: 24)
            
            Divider()
            VStack {
                Text(text)
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.greyColor)
            }
            .background(DesignSystem.whiteColor)
            .clipped()
            .fixedSize()
            .frame(width: 0, alignment: .leading)
            Divider()
            HStack {
                Spacer()
                Divider()
                Spacer()
            }
            .frame(height: 24)
            Divider()
        }
    }
}

struct AgendaDayRibbon_Previews: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: 0) {
            AgendaDayRibbon(day: "APRIL 14")
            AgendaDayRibbon(day: "APRIL 14")
        }
    }
}
