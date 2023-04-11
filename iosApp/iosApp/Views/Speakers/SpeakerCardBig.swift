//
//  SpeakerCardBig.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 10.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SpeakerCardBig: View {
    @EnvironmentObject var conference: ConferenceModel
    var id: String

    var speaker: Speaker {
        conference.speakerById(id: id)
    }

    var sessions: [SessionCardView] {
        conference.sessionsForSpeaker(id: id)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    HStack {
                        Text(speaker.name.uppercased())
                            .font(DesignSystem.kcH2)
                            .foregroundColor(DesignSystem.greyGrey5Color)
                    }
                    
                    Text(speaker.position)
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.greyGrey20Color)
                        .padding(.top, 16)
                }
                .padding(.top, 24)
                .padding(.leading, 16)
                .padding(.trailing, 16)
            
                Divider()
                    .padding(.top, 24)
                CachedAsyncImage(
                    url: URL(string: speaker.photoUrl),
                    content: { image in
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                    },
                    placeholder: {
                        EmptyView()
                    }
                )
                .scaledToFit()
            }
            .background(DesignSystem.grey5BlackColor)
            .padding(.top, 24)
            Divider()
            
            VStack(alignment: .leading, spacing: 0) {
                Text(speaker.description_)
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.greyGrey20Color)
                    .padding(.top, 24)
                    .padding(.bottom, 12)
                
                if sessions.count > 0 {
                    Text("TALKS:")
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.grey50Color)
                        .padding(.top, 12)
                }
            }
            .padding(.leading, 16)
            .padding(.trailing, 16)

            VStack(alignment: .leading, spacing: 0) {
                Divider()
                    .padding(.top, 12)
                ForEach(sessions, id: \.title) { session in
                    NavigationLink(destination: {
                        SessionView(id: session.id)
                    }, label: {
                        sessionCard(session)
                            .multilineTextAlignment(.leading)
                    })
                    Divider()
                }
            }
        }
        .background(DesignSystem.whiteGreyColor)
    }
    
    func sessionCard(_ session: SessionCardView) -> some View {
        ZStack {
            HStack(alignment: .top, spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    Text(session.timeLine)
                        .font(DesignSystem.kcT2)
                        .padding(.top, 0)
                        .foregroundColor(DesignSystem.grey50Grey20Color)

                    Text(session.title)
                        .font(DesignSystem.kcH4)
                        .foregroundColor(DesignSystem.greyWhiteColor)
                        .padding(.top, 16)
                    
                    HStack {
                        Text(session.locationLine.uppercased())
                            .font(DesignSystem.kcT2)
                            .foregroundColor(DesignSystem.grey50Color)
                        Spacer()
                    }
                    .padding(.top, 16)
                    .padding(.bottom, 16)
                    
                    VStack {
                        if session.isLightning {
                            HStack {
                                Image(DesignSystem.lightIcon)
                                    .foregroundColor(DesignSystem.orangeColor)
                                    .frame(width: 24, height: 24)
                                
                                Text("lightning talk")
                                    .font(DesignSystem.kcT2)
                                    .foregroundColor(DesignSystem.greyWhiteColor)
                                    .lineLimit(1)
                                    .padding(2)
                            }
                            .background(DesignSystem.grey5BlackColor)
                            .cornerRadius(4)
                        }
                        
                        if session.isCodeLab {
                            HStack {
                                Image(DesignSystem.codeLabIcon)
                                    .foregroundColor(DesignSystem.violetColor)
                                    .frame(width: 24, height: 24)
                                
                                Text("Big Nerd Ranch lab")
                                    .font(DesignSystem.kcT2)
                                    .foregroundColor(DesignSystem.greyWhiteColor)
                                    .lineLimit(1)
                                    .padding(2)
                            }
                            .background(DesignSystem.grey5BlackColor)
                            .cornerRadius(4)
                        }
                        if session.isAWSLab {
                            HStack {
                                Image(DesignSystem.codeLabIcon)
                                    .foregroundColor(DesignSystem.violetColor)
                                    .frame(width: 24, height: 24)
                                
                                Text("AWS lab")
                                    .font(DesignSystem.kcT2)
                                    .foregroundColor(DesignSystem.greyWhiteColor)
                                    .lineLimit(1)
                                    .padding(2)
                            }
                            .background(DesignSystem.grey5BlackColor)
                            .cornerRadius(4)
                        }
                    }
                    .padding(.bottom, 16)
                }
                .padding(.top, 16)
                .padding(.leading, 19)
                .padding(.trailing, 16)
                
                Button(action: {
                    conference.toggleFavorite(sessionId: session.id)
                }, label: {
                    Image(session.isFavorite ? DesignSystem.bookmarkActiveIcon : DesignSystem.bookmarkIcon)
                        .foregroundColor(
                            session.isFavorite ? DesignSystem.orangeColor : DesignSystem.greyWhiteColor
                        )
                        .frame(width: 50, height: 50)
                })
            }
        }
    }
}

struct SpeakerCardLarge_Previews: PreviewProvider {
    let model = ConferenceModel()
    static var previews: some View {
        ScrollView {
            SpeakerCardBig(id: "80f570c3-27df-4756-b04a-76b2d6f220c4")
            SpeakerCardBig(id: "80f570c3-27df-4756-b04a-76b2d6f220c4")
            SpeakerCardBig(id: "80f570c3-27df-4756-b04a-76b2d6f220c4")
        }
        .environmentObject(ConferenceModel())
    }
}
