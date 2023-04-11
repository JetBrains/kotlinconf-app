//
//  AgendaItemBig.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 11.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SessionView: View {
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    @GestureState private var dragOffset = CGSize.zero
    @EnvironmentObject var conference: ConferenceModel
    var id: String
    
    @State var vote: Bool = false
    @State var feedback: String = ""
    @State var selectedDetent: PresentationDetent = .height(80)
    @State var showPrivacy = false
    
    @Environment(\.colorScheme) var colorScheme: ColorScheme
    var THEMED_MAP: [String:String] {
        colorScheme == .dark ? MAP_LOCATION : MAP_LOCATION_LIGHT
    }

    var session: SessionCardView {
        conference.sessionById(id: id)
    }
    
    var title: String {
        session.title
    }
    
    var description: String {
        session.description_
    }
    
    var speakersIds: [String] {
        session.speakerIds
    }
    
    var score: Score? {
        session.vote
    }
    
    var time: String {
        session.timeLine
    }
    
    var isFavorite: Bool {
        session.isFavorite
    }
    
    var feedbackEnabled: Bool {
        !feedback.isEmpty
    }
 
    var body: some View {
        if showPrivacy {
            Welcome {
                conference.acceptPrivacyPolicy()
                showPrivacy = false
            } onRejectPrivacy: {
                showPrivacy = false
            }
        } else {
            ZStack {
                ScrollView {
                    card
                }
                .navigationBarBackButtonHidden(true)
                .navigationBarItems(leading: backButton)
                .navigationBarTitleDisplayMode(.inline)
                .sheet(isPresented: $vote) {
                    voteBlock
                        .presentationDetents(undimmed: [.height(80), .medium], selection: $selectedDetent)
//                        .interactiveDismissDisabled()
                }
            }.onAppear {
                vote = session.isFinished
            }.onDisappear {
                vote = false
            }
            .gesture(DragGesture().updating($dragOffset, body: { (value, state, transaction) in
                if(value.startLocation.x < 20 && value.translation.width > 100) {
                    self.presentationMode.wrappedValue.dismiss()
                }
            }))
        }
    }
    
   
    var backButton: some View {
        Button(action: {
            self.presentationMode.wrappedValue.dismiss()
        }) {
            HStack {
                Image(DesignSystem.backIcon)
                    .foregroundColor(DesignSystem.greyGrey5Color)
            }
        }
    }
    
    var voteBlock: some View {
        VStack {
            HStack(alignment: .top, spacing: 0) {
                Text("How was the talk?")
                    .font(DesignSystem.kcT2)
                Spacer()
                
                Button {
                    selectedDetent = .medium
                    conference.vote(sessionId: session.id, rating: .good) {
                        showPrivacy = true
                    }
                } label: {
                    let color = score == .good ? DesignSystem.orangeColor : DesignSystem.greyWhiteColor
                    Image(DesignSystem.faceHappyIcon)
                        .foregroundColor(color)
                }
                
                Button {
                    selectedDetent = .medium
                    conference.vote(sessionId: session.id, rating: .ok) {
                        showPrivacy = true
                    }
                } label: {
                    let color = score == .ok ? DesignSystem.orangeColor : DesignSystem.greyWhiteColor
                    Image(DesignSystem.faceNeutralIcon)
                        .foregroundColor(color)
                }
                .padding(.leading, 30)
                
                Button {
                    selectedDetent = .medium
                    conference.vote(sessionId: session.id, rating: .bad) {
                        showPrivacy = true
                    }
                } label: {
                    let color = score == .bad ? DesignSystem.orangeColor : DesignSystem.greyWhiteColor
                    Image(DesignSystem.faceSadIcon)
                        .foregroundColor(color)
                }
                .padding(.leading, 30)
            }
            .frame(height: 72)
            .padding(.top, 20)
            .padding(.leading, 16)
            .padding(.trailing, 16)
            
            if selectedDetent == .medium {
                FeedbackBlock { value in
                    conference.sendFeedback(sessionId: session.id, feedback: feedback) {
                        showPrivacy = true
                    }
                    selectedDetent = .height(80)
                }
            }
        }
    }
    
    var card: some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    HStack(alignment: .top) {
                        Text(time)
                            .font(DesignSystem.kcT2)
                            .padding(.top, 0)
                            .foregroundColor(DesignSystem.grey50Grey20Color)
                        Spacer()
                        Button(action: {
                            conference.toggleFavorite(sessionId: id)
                        }, label: {
                            Image(isFavorite ? DesignSystem.bookmarkActiveIcon : DesignSystem.bookmarkIcon)
                                .foregroundColor(isFavorite ? DesignSystem.orangeColor : DesignSystem.greyGrey5Color)
                        })
                    }
                    .padding(.top, 12)
                    sessionTitle()
                    speakers()
                }
                .padding(.leading, 16)
                .padding(.trailing, 16)
                
                HStack(alignment: .top) {
                    speakersPhoto()
                    Spacer()
                }
                .padding(.leading, 16)
                
                Divider()
            }
            .background(DesignSystem.grey5BlackColor)
            
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
            .padding(.leading,16)
            .padding(.top, 24)
            
             Text(description)
                 .font(DesignSystem.kcT2)
                 .foregroundColor(DesignSystem.greyGrey20Color)
                 .padding(.top, 24)
                 .padding(.leading, 16)
                 .padding(.trailing, 16)
            
            HStack {
                Text(session.locationLine.uppercased())
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.grey50Color)
                Spacer()
            }
            .padding(.top, 24)
            .padding(.leading, 16)
            
            Divider()
                .padding(.top, 16)
            
            let location = session.locationLine
            NavigationLink {
                MapTabView()
            } label: {
                MapSwiftUIWrapper(url: THEMED_MAP[location]!, onClick: { _, _, _ in
                }, cameraOptions: ROOM_LOCATION[location]!, isFrozen: true)
            }
            .frame(height: 400)
            
            .padding(.leading, 0)
            .padding(.trailing, 0)
            Divider()
                .padding(.bottom, 32)
        }
        .background(DesignSystem.whiteGreyColor)
    }
    
    func sessionTitle() -> some View {
        Text(title.uppercased())
            .font(DesignSystem.kcH2)
            .padding(.top, 0)
            .foregroundColor(DesignSystem.greyWhiteColor)
        
            .padding(.top, 16)
    }
    
    func speakers() -> some View {
        VStack(alignment: .leading, spacing: 4) {
            ForEach(speakersIds.map { id in conference.speakerById(id: id) }, id: \.self) { speaker in
                NavigationLink(destination: {
                    SpeakersDetailedView(
                        focusedSpeakerId: speaker.id
                    )
                }, label: {
                    Text(speaker.name)
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.greyGrey20Color)
                        .multilineTextAlignment(.leading)
                })
            }
        }.padding(.top, 12)
    }
    
    func speakersPhoto() -> some View {
        HStack(spacing: 8) {
            ForEach(speakersIds.map { id in conference.speakerById(id: id) }, id: \.self) { speaker in
                NavigationLink(destination: {
                    SpeakersDetailedView(
                        focusedSpeakerId: speaker.id
                    )
                }, label: {
                    avatar(speaker: speaker)
                })
            }
        }
        .padding(.top, 24)
    }
    
    var avatarSize: CGFloat {
        speakersIds.count > 2 ? 76 : 120
    }
    
    func avatar(speaker: Speaker) -> some View {
        CachedAsyncImage(
            url: URL(string: speaker.photoUrl),
            content: { image in
                image
                    .resizable()
                    .frame(width: avatarSize, height: avatarSize)
                    .scaledToFit()
            },
            placeholder: {
                ProgressView()
            }
        )
        .scaledToFit()
        .frame(width: avatarSize, height: avatarSize)
    }
}
