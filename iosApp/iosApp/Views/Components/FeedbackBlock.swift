//
//  FeedbackBlock.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 02.03.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct FeedbackBlock: View {
    var showClose: Bool = false
    var onClose: () -> Void = {}
    var onSend: (String) -> Void
    
    @State var placeholder = "Would you like to share a comment?"
    @FocusState var isFocused: Bool
    @State var feedback: String = ""
    
    var body: some View {
        VStack(spacing: 0) {
            Divider()
            ZStack(alignment: .topLeading) {
                if feedback.isEmpty && !isFocused {
                    TextEditor(text: $placeholder)
                        .disabled(true)
                        .font(DesignSystem.kcT2)
                        .scrollContentBackground(.hidden)
                        .foregroundColor(DesignSystem.greyColor)
                        .accentColor(DesignSystem.violetColor)
                        .frame(height: 300)
                }
                
                TextEditor(text: $feedback)
                    .font(DesignSystem.kcT2)
                    .focused($isFocused)
                    .scrollContentBackground(.hidden)
                    .accentColor(DesignSystem.violetColor)
                    .frame(height: 300)
                
                if showClose {
                    HStack {
                        Spacer()
                        Button(action: { onClose() }) {
                            HStack {
                                Image(DesignSystem.closeIcon)
                                    .aspectRatio(contentMode: .fit)
                                    .foregroundColor(DesignSystem.greyGrey5Color)
                            }
                        }
                        .padding(.top, 8)
                    }
                }
            }
            .background(DesignSystem.grey5BlackColor)
            .padding(.leading, 16)
            .padding(.trailing, 16)
            
            Button("SEND MY COMMENT") {
                onSend(feedback)
            }
            .padding()
            .disabled(feedback.isEmpty)
            .foregroundColor(feedback.isEmpty ? DesignSystem.grey50Color : DesignSystem.greyWhiteColor)
            .background(DesignSystem.whiteGreyColor)
            .font(DesignSystem.kcT2)
            Spacer()
        }
        .background(DesignSystem.grey5BlackColor)
    }
}

struct FeedbackBlock_Previews: PreviewProvider {
    static var previews: some View {
        FeedbackBlock(showClose: true) { value in
        }
    }
}
