//
//  BookmarksView.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 23.01.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct BookmarksView: View {
    @EnvironmentObject var conference: ConferenceModel
    @State var selectedTab = "UPCOMING"
    
    var body: some View {
        TabBar(
            labels: ["PAST", "UPCOMING"],
            selectedLabel: selectedTab,
            onClick: { newSelected in
                selectedTab = newSelected
            }) {
                if selectedTab == "PAST" {
                    finishedCards
                } else {
                    upcomingCards
                }
        }
    }
    
    var favorites: [SessionCardView] {
        conference.cards.filter { item in
            item.isFavorite
        }
    }
    
    var finished: [SessionCardView] {
        favorites.filter { card in
            card.isFinished
        }
    }
    
    var upcoming: [SessionCardView] {
        favorites.filter { card in
            !card.isFinished
        }
    }
    
    var finishedCards: some View {
        ScrollView {
            VStack(spacing: 0) {
                ForEach(finished, id: \.id) { card in
                    FinishedCard(
                        id: card.id,
                        timeLine: card.timeLine,
                        title: card.title,
                        speakerLine: card.speakerLine,
                        locationLine: card.locationLine.uppercased(),
                        score: card.vote
                    ) { score in
                        conference.vote(sessionId: card.id, rating: score) {
                            
                        }
                    } onSendFeedback: { value in
                        conference.sendFeedback(sessionId: card.id, feedback: value) {
                            
                        }
                    } toggleFavorite: {
                        conference.toggleFavorite(sessionId: card.id)
                    }
                    Divider()
                }
            }
        }
    }
    
    var upcomingCards: some View {
        ScrollView {
            VStack(spacing: 0) {
                ForEach(upcoming, id: \.id) { card in
                    NavigationLink {
                        SessionView(id: card.id)
                    } label: {
                        UpcomingCard(timeLine: card.timeLine, title: card.title, speakerLine: card.speakerLine, locationLine: card.locationLine.uppercased(), isSoon: false) { conference.toggleFavorite(sessionId: card.id) }
                            .multilineTextAlignment(.leading)
                    }
                    Divider()
                }
            
            }
        }
        
    }
}


struct UpcomingCard : View {
    var timeLine: String
    var title: String
    var speakerLine: String
    var locationLine: String
    var isSoon: Bool
    var toggleFavorite: () -> Void
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            VStack(spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    Text(timeLine)
                        .font(DesignSystem.kcT2)
                        .foregroundColor(isSoon ? DesignSystem.orangeColor : DesignSystem.grey50Color)
                    Text(title)
                        .font(DesignSystem.kcH4)
                        .foregroundColor(DesignSystem.greyWhiteColor)
                        .padding(.top, 16)
                    Text(speakerLine)
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.greyGrey5Color)
                        .padding(.top, 4)
                    HStack {
                        Text(locationLine.uppercased())
                            .font(DesignSystem.kcT2)
                            .foregroundColor(DesignSystem.grey50Color)
                        Spacer()
                    }
                    .padding(.top, 16)
                }.padding(16)
            }.background(isSoon ? DesignSystem.grey5BlackColor : DesignSystem.whiteGreyColor)
            HStack(alignment: .top) {
                Spacer()
                Button(action: {
                    toggleFavorite()
                }, label: {
                    Image(DesignSystem.bookmarkActiveIcon)
                        .foregroundColor(DesignSystem.orangeColor)
                })
                .frame(width: 50, height: 50)
            }
        }
    }
}


struct FinishedCard : View {
    var id: String
    var timeLine: String
    var title: String
    var speakerLine: String
    var locationLine: String
    var score: Score?
    var onVote: (Score?) -> Void
    
    @State var showFeedbackForm: Bool = false
    var onSendFeedback: (String) -> Void
    var toggleFavorite: () -> Void
    
    var body: some View {
        VStack(spacing: 0) {
            NavigationLink {
                SessionView(id: id)
            } label: {
                ZStack(alignment: .topLeading) {
                    VStack(spacing: 0) {
                        VStack(alignment: .leading, spacing: 0) {
                            Text(timeLine)
                                .font(DesignSystem.kcT2)
                                .foregroundColor(DesignSystem.grey50Color)
                            Text(title)
                                .font(DesignSystem.kcH4)
                                .foregroundColor(DesignSystem.greyWhiteColor)
                                .padding(.top, 16)
                            Text(speakerLine)
                                .font(DesignSystem.kcT2)
                                .foregroundColor(DesignSystem.greyGrey5Color)
                                .padding(.top, 4)
                            VoteBlock(score: score, onClick: { score in
                                showFeedbackForm = true
                                onVote(score)
                            })
                            .padding(.top, 16)
                            
                        }.padding(16)
                    }
                    .multilineTextAlignment(.leading)
                    HStack(alignment: .top) {
                        Spacer()
                        Button(action: {
                            toggleFavorite()
                        }, label: {
                            Image(DesignSystem.bookmarkActiveIcon)
                                .foregroundColor(DesignSystem.orangeColor)
                        })
                        .frame(width: 50, height: 50)
                    }
                }
            }

            if (showFeedbackForm) {
                FeedbackBlock(showClose: true, onClose: {
                    showFeedbackForm = false
                }) { feedback in
                    showFeedbackForm = false
                    onSendFeedback(feedback)
                }
                .frame(height: 500)
            }
        }.background(DesignSystem.whiteGreyColor)
    }
}

struct UpcomingCardPreview: PreviewProvider {
    static var previews: some View {
        ScrollView {
            VStack(spacing: 0) {
                UpcomingCard(timeLine: "APRIL 13 14:00", title: "Hello, world", speakerLine: "Some speaker, and other speaker", locationLine: "My Home", isSoon: true) {}
                Divider()
                UpcomingCard(timeLine: "13 Apr 14:00", title: "Hello, world", speakerLine: "Some speaker, and other speaker", locationLine: "My Home", isSoon: false) {}
                Divider()
                FinishedCard(id: "0", timeLine: "13 Apr 14:00", title: "Hello, world", speakerLine: "Some speaker, and other speaker", locationLine: "My Home", onVote: { vote in }, onSendFeedback: { feedback in  }) {}
                Divider()
                FinishedCard(id: "0",timeLine: "13 Apr 14:00", title: "Hello, world", speakerLine: "Some speaker, and other speaker", locationLine: "My Home", onVote: { vote in }, onSendFeedback: { feedback in  }) {}
                Divider()
                FinishedCard(id: "0", timeLine: "13 Apr 14:00", title: "Hello, world", speakerLine: "Some speaker, and other speaker", locationLine: "My Home", onVote: { vote in }, onSendFeedback: { feedback in  }) {}
            }
        }
    }
}



struct BookmarksView_Previews: PreviewProvider {
    static var previews: some View {
        BookmarksView()
            .environmentObject(ConferenceModel())
    }
}
