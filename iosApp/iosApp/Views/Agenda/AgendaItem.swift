//
//  AgendaItem.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 09.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct AgendaItem: View {
    @EnvironmentObject var conference: ConferenceModel
    var session: SessionCardView
    
    var isFinished: Bool {
        session.isFinished
    }
    
    var isFavorite: Bool {
        session.isFavorite
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            ZStack {
                VStack {
                    VStack(alignment: .leading, spacing: 0) {
                        Text(session.title)
                            .font(DesignSystem.kcH4)
                            .foregroundColor(isFinished ? DesignSystem.grey50Color : DesignSystem.greyWhiteColor)
                            .padding(.top, 16)
                            .padding(.trailing, 50)
                                
                                let names = session.speakerIds.map { id in
                                conference.speakerById(id: id).name
                            }.joined(separator: ", ")
                        
                        Text(names)
                            .font(DesignSystem.kcT2)
                            .foregroundColor(isFinished ? DesignSystem.grey50Color : DesignSystem.greyGrey5Color)
                            .padding(.top, 4)
                        
                        HStack {
                            if !isFinished {
                                Text(session.locationLine.uppercased())
                                    .foregroundColor(DesignSystem.grey50Color)
                                    .font(DesignSystem.kcT2)
                            }
                            Spacer()
                            if session.isLightning {
                                HStack {
                                    Image(DesignSystem.lightIcon)
                                        .foregroundColor(DesignSystem.orangeColor)
                                        .frame(width: 24, height: 24)
                                        .padding(2)
                                    
                                    Text(session.badgeTimeLine)
                                        .font(DesignSystem.kcT2)
                                        .foregroundColor(DesignSystem.greyWhiteColor)
                                        .lineLimit(1)
                                        .padding(.trailing, 4)
                                }
                                .background(DesignSystem.grey5BlackColor)
                                .opacity(isFinished ? 0.5 : 1.0)
                                .cornerRadius(4)
                            }
                            
                            if session.isCodeLab {
                                HStack {
                                    Image(DesignSystem.codeLabIcon)
                                        .foregroundColor(DesignSystem.violetColor)
                                        .frame(width: 24, height: 24)
                                        .padding(2)
                                    
                                    Text("Birg Nerd Ranch lab")
                                        .font(DesignSystem.kcT2)
                                        .foregroundColor(DesignSystem.greyWhiteColor)
                                        .lineLimit(1)
                                        .padding(.trailing, 4)
                                }
                                .background(DesignSystem.grey5BlackColor)
                                .opacity(isFinished ? 0.5 : 1.0)
                                .cornerRadius(4)
                            }
                            if session.isAWSLab {
                                HStack {
                                    Image(DesignSystem.codeLabIcon)
                                        .foregroundColor(DesignSystem.violetColor)
                                        .frame(width: 24, height: 24)
                                        .padding(2)
                                    
                                    Text("AWS lab")
                                        .font(DesignSystem.kcT2)
                                        .foregroundColor(DesignSystem.greyWhiteColor)
                                        .lineLimit(1)
                                        .padding(.trailing, 4)
                                }
                                .background(DesignSystem.grey5BlackColor)
                                .opacity(isFinished ? 0.5 : 1.0)
                                .cornerRadius(4)
                            }
                        }
                        .padding(.top, 16)
                        
                        if isFinished {
                            VoteBlock(score: session.vote) { newScore in
                                conference.vote(sessionId: session.id, rating: newScore) {
                                }
                            }
                            .padding(.top, 16)
                        }
                    }
                    .padding(.leading, 16)
                    .padding(.trailing, 16)
                    .padding(.bottom, 16)
                }
                .background(DesignSystem.whiteGreyColor)
                    
                VStack {
                    HStack {
                        Spacer()
                        let color = isFavorite ? DesignSystem.orangeColor : DesignSystem.greyGrey5Color
                        Button(action: {
                            conference.toggleFavorite(sessionId: session.id)
                        }, label: {
                            Image(isFavorite ? DesignSystem.bookmarkActiveIcon : DesignSystem.bookmarkIcon)
                                .foregroundColor(isFinished ? color.opacity(0.5) : color)
                        })
                        .frame(width: 50, height: 50)
                    }
                    Spacer()
                }
            }

            Divider()
        }
    }
}
