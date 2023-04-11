//
//  VoteBlock.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 01.03.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct VoteBlock: View {
    var score: Score?
    var onClick: (Score?) -> Void
    
    var body: some View {
        HStack(alignment: .top, spacing: 0) {
            Text("How was the talk?")
                .font(DesignSystem.kcT2)
                .foregroundColor(DesignSystem.greyGrey50Color)
            Spacer()
            
            Button {
                onClick(score == .good ? nil : .good)
            } label: {
                let color = score == .good ? DesignSystem.orangeColor : DesignSystem.greyWhiteColor
                Image(score == .good ? DesignSystem.faceHappyActiveIcon : DesignSystem.faceHappyIcon)
                    .foregroundColor(color)
            }
            
            Button {
                onClick(score == .ok ? nil : .ok)
            } label: {
                let color = score == .ok ? DesignSystem.orangeColor : DesignSystem.greyWhiteColor
                Image(score == .ok ? DesignSystem.faceNeutralActiveIcon : DesignSystem.faceNeutralIcon)
                    .foregroundColor(color)
            }
            .padding(.leading, 30)
            
            Button {
                onClick(score == .bad ? nil : .bad)
            } label: {
                let color = score == .bad ? DesignSystem.orangeColor : DesignSystem.greyWhiteColor
                Image(score == .bad ? DesignSystem.faceSadActiveIcon : DesignSystem.faceSadIcon)
                    .foregroundColor(color)
            }
            .padding(.leading, 30)
        }
    }
}

struct VoteBlock_Previews: PreviewProvider {
    
    @State
    static var score: Score? = .good
    
    static var previews: some View {
        VoteBlock(score: score) { it in
            score = it
        }
    }
}
