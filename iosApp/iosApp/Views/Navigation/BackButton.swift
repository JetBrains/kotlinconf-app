//
//  BackButton.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 30.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct BackButton: View {
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    var icon: String = DesignSystem.backIcon

    var body : some View {
        Button(action: {
            self.presentationMode.wrappedValue.dismiss()
        }) {
            HStack {
                Image(icon)
                    .foregroundColor(DesignSystem.greyGrey5Color)
            }
        }
    }
}

struct BackButton_Previews: PreviewProvider {
    static var previews: some View {
        BackButton()
    }
}
