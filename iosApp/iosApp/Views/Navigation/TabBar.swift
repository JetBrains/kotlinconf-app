//
//  TabBar.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 01.03.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI

func TabBar(
    labels: [String],
    selectedLabel: String,
    onClick: @escaping (String) -> Void,
    @ViewBuilder _ content: () -> some View
) -> some View {
    VStack(spacing: 0) {
        content()
    }
    .navigationBarTitleDisplayMode(.inline)
    .toolbar {
        ToolbarItem(placement: .principal) {
            HStack(spacing: 0) {
                ForEach(labels, id: \.self) { label in
                    TabButton(label: label, isSelected: label == selectedLabel) {
                        onClick(label)
                    }
                }
            }
        }
    }
}

func TabButton(
    label: String,
    isSelected: Bool,
    onClick: @escaping () -> Void
) -> some View {
   var fillColor: Color {
       return isSelected ? DesignSystem.greyWhiteColor : DesignSystem.whiteGreyColor
   }
   var textColor: Color {
       return isSelected ? DesignSystem.whiteGreyColor : DesignSystem.grey50Color
   }
   
   var font: Font {
       return isSelected ? DesignSystem.kcH4 : DesignSystem.kcT2
   }
    
    return Button {
        onClick()
    } label: {
        Text(label.uppercased())
            .font(font)
            .foregroundColor(textColor)
    }
    .frame(height: 28)
    .buttonStyle(.borderedProminent)
    .tint(fillColor)
    .cornerRadius(4)
    .animation(.default, value: isSelected)
}


struct TabBarPreivew: PreviewProvider {
    static var previews: some View {
        TabBar(labels: ["Upcoming", "2", "3"], selectedLabel: "Upcoming") { value in } _: {
            
        }
    }
}
