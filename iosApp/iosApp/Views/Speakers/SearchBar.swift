//
//  SearchBar.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 23.01.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct SearchBar: View {
    @Binding var search: String

    @FocusState var focused

    var body: some View {
        HStack(alignment: .center, spacing: 0) {
            TextField("", text: $search)
                .font(DesignSystem.kcT2)
                .accentColor(DesignSystem.violetColor)
                .focused($focused, equals: true)
                .onAppear {
                    focused = true
                }
            if !search.isEmpty {
                Button("CLEAR") {
                    search = ""
                }
                .foregroundColor(DesignSystem.greyWhiteColor)
                .font(DesignSystem.kcT2)
            }
            
        }
        .padding(.leading, 16)
        .padding(.trailing, 16)
        .padding(.bottom, 16)
        .padding(.top, 16)
    }
}
