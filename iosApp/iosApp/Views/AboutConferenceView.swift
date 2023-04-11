//
//  AboutConferenceView.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 09.03.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct AboutConferenceView: View {
    @GestureState private var dragOffset = CGSize.zero
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    @EnvironmentObject var conference: ConferenceModel
    var allSpeakers: [Speaker] {
        conference.speakers.all
    }
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                Image(DesignSystem.aboutLogo)
                    .resizable()
                    .scaledToFill()
                    .frame(height: 268)
                    .padding(.bottom, 24)

                infoBlock
                titleBlock(time: "APRIL 13 / 09:00", title: "Opening Keynote")
                speakers
                Divider()
                secondDayKeynote
                shortTalks
                party
                closingPanel
                moreInfo
            }
        }
        .navigationBarBackButtonHidden(true)
        .navigationBarItems(leading: BackButton())
        .navigationBarTitleDisplayMode(.inline)
        .background(DesignSystem.grey5BlackColor)
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack {
                    Text("KOTLINCONF`23")
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.grey50Color)
                }
            }
        }
        .gesture(DragGesture().updating($dragOffset, body: { (value, state, transaction) in
             if(value.startLocation.x < 20 && value.translation.width > 100) {
                 self.presentationMode.wrappedValue.dismiss()
             }
        }))
    }
    
    var infoBlock: some View {
        VStack(alignment: .leading, spacing: 0) {
            Divider()
            VStack(alignment: .leading, spacing: 0) {
                Text("KotlinConf is the official annual conference devoted to the Kotlin programming language. Organized by JetBrains, it is the place for the community to gather and discuss all things Kotlin.")
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.greyGrey20Color)

                Text("Social media hashtag:")
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.greyGrey20Color)
                    .padding(.top, 16)

                HStack {
                    Text("#kotlinconf".uppercased())
                        .font(DesignSystem.kcT2.bold())
                    Spacer()
                }
            }
            .padding(.top, 24)
            .padding(.leading, 16)
            .padding(.trailing, 16)
            .padding(.bottom, 48)
            Divider()
        }
        .background(DesignSystem.whiteGreyColor)
    }
    
    func titleBlock(time: String, title: String) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(time.uppercased())
                .font(DesignSystem.kcT2)
                .padding(.leading, 16)
                .padding(.top, 16)
                .padding(.trailing, 16)
                .foregroundColor(DesignSystem.greyWhiteColor)
            
            Text(title.uppercased())
                .font(DesignSystem.kcH2)
                .foregroundColor(DesignSystem.greyWhiteColor)
                .padding(.leading, 16)
                .padding(.trailing, 16)
                .padding(.top, 8)
            Divider()
                .padding(.top, 25)
        }
    }
    
    var secondDayKeynote: some View {
        VStack(alignment: .leading, spacing: 0) {
            titleBlock(time: "APRIL 14 / 09:00", title: "Second Day Keynote")
            
            Divider()
            SpeakerCardVertical(name: "Kevlin Henney")
            Divider()
        }
    }
    
    var speakers: some View {
        HStack(alignment: .top) {
            VStack(alignment: .leading, spacing: 0) {
                SpeakerCard(name: "Roman Elizarov")
                SpeakerCard(name: "Grace Kloba")
            }
            Divider()
            VStack(alignment: .leading, spacing: 0) {
                SpeakerCard(name: "Svetlana Isakova")
                SpeakerCard(name: "Egor Tolstoy")
            }
            
        }
        .background(DesignSystem.whiteGreyColor)
    }
    
    var shortTalks: some View {
        VStack(alignment: .leading, spacing: 0) {
            Divider()
                .padding(.top, 48)
            
            HStack {
                Image(DesignSystem.lightIcon)
                    .foregroundColor(DesignSystem.orangeColor)
                    .frame(width: 24, height: 24)
                
                Text("28 Lightning talks!".uppercased())
                    .font(DesignSystem.kcT2.bold())
                    .foregroundColor(DesignSystem.greyWhiteColor)
                    .lineLimit(1)
                    .padding(2)
            }
            .padding(.leading, 16)
            .padding(.trailing, 16)
            .padding(.top, 16)
            
            Text("Don't miss our new Lightning Talk track! Enjoy double the inspiration with two 15-minute talks in each time slot.")
                .font(DesignSystem.kcT2)
                .foregroundColor(DesignSystem.greyGrey20Color)
                .padding(.leading, 16)
                .padding(.trailing, 16)
                .padding(.top, 16)
            
            Divider()
                .padding(.top, 24)
            
            HStack {
                Image(DesignSystem.codeLabIcon)
                    .foregroundColor(DesignSystem.violetColor)
                    .frame(width: 24, height: 24)
                
                Text("AWS labs / Big Nerd Ranch labs".uppercased())
                    .font(DesignSystem.kcT2.bold())
                    .foregroundColor(DesignSystem.greyWhiteColor)
                    .lineLimit(1)
                    .padding(2)
            }
            .padding(.leading, 16)
            .padding(.trailing, 16)
            .padding(.top, 16)
            
            Text("Sink your teeth into Kotlin with Code Labs by Big Nerd Ranch for general topics and AWS Labs for AWS/Kotlin tech!")
                .font(DesignSystem.kcT2)
                .foregroundColor(DesignSystem.greyGrey20Color)
                .padding(.leading, 16)
                .padding(.trailing, 16)
                .padding(.top, 16)
            
            Divider()
                .padding(.top, 24)
            Divider()
                .padding(.top, 49)
        }
        .background(DesignSystem.whiteGreyColor)
    }
    
    var closingPanel: some View {
        VStack(alignment: .leading, spacing: 0) {
            titleBlock(time: "APRIL 14 / 17:15", title: "Closing Panel")
            VStack {
                Text("Come to Effectenbeurszaal and seize the opportunity to ask the KotlinConf speakers your questions in person.")
                    .padding(16)
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.greyGrey20Color)
                
                VStack(alignment: .leading, spacing: 0) {
                    Image(DesignSystem.closingLogo)
                        .resizable()
                        .scaledToFill()
                        .frame(height: 190)
                }
            }.background(DesignSystem.whiteGreyColor)
            Divider()
        }
    }
    
    var party: some View {
        VStack(alignment: .leading, spacing: 0) {
            titleBlock(time: "APRIL 13 / 18:00", title: "KotlinConf’23 Party")
            VStack {
                Text("Have fun and mingle with the community at the biggest Kotlin party of the year! ")
                    .padding(16)
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.greyGrey20Color)
            }.background(DesignSystem.whiteGreyColor)
            
            Divider()
        }
    }
    
    var moreInfo: some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                HStack { Spacer() }
                Text("You can find more information about the conference on the official website:")
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.grey50Color)
                Link(destination: URL(string: "https://kotlinconf.com")!, label: {
                    Text("kotlinconf.com")
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.greyWhiteColor)
                        .underline()
                })
                .multilineTextAlignment(.leading)
                Link(destination: URL(string: "https://kotlinconf.com/kotlinconf-2023-privacy-policy-for-visitors.pdf")!, label: {
                    Text("Privacy Policy for Visitors")
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.greyWhiteColor)
                        .underline()
                })
                .multilineTextAlignment(.leading)
                .padding(.top, 16)
                Link(destination: URL(string: "https://kotlinconf.com/kotlinconf-2023-general-terms-and-conditions-for-visitors.pdf")!, label: {
                    Text("General Terms and Conditions for Visitors")
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.greyWhiteColor)
                        .underline()
                        .multilineTextAlignment(.leading)
                })
                .padding(.top, 8)
            }
            .padding(.leading, 16)
            .padding(.trailing, 16)
            .padding(.top, 16)
            .padding(.bottom, 24)
            
            Text(conference.time)
                .font(DesignSystem.kcT2)
                .foregroundColor(DesignSystem.grey50Color)
                .padding(.leading, 16)
                .padding(.bottom, 24)
        }
        .background(DesignSystem.whiteGreyColor)
    }
    
    func SpeakerCard(name: String) -> some View {
        let speaker = allSpeakers.first { speaker in
            speaker.name == name
        } ?? Speaker(id: "", name: "", position: "", description: "", photoUrl: "")
        
        return VStack(alignment: .leading, spacing: 0) {
            CachedAsyncImage(
                url: URL(string: speaker.photoUrl),
                content: { image in
                    image
                        .resizable()
                        .scaledToFit()
                },
                placeholder: {
                    ProgressView()
                }
            )
            .scaledToFit()
            
            Text(speaker.name)
                .font(DesignSystem.kcH4)
                .padding(.leading, 16)
                .padding(.trailing, 16)
                .padding(.top, 16)
                .foregroundColor(DesignSystem.greyWhiteColor)
            Text(speaker.position)
                .padding(.top, 4)
                .padding(.leading, 16)
                .padding(.trailing, 16)
                .padding(.bottom, 25)
                .font(DesignSystem.kcT2)
                .foregroundColor(DesignSystem.grey50Color)

            HStack {
                Spacer()
            }
        }
    }
    
    func SpeakerCardVertical(name: String) -> some View {
        let speaker = allSpeakers.first { speaker in
            speaker.name == name
        } ?? Speaker(id: "", name: "", position: "", description: "", photoUrl: "")
        
        return HStack(alignment: .top, spacing: 0) {
                CachedAsyncImage(
                    url: URL(string: speaker.photoUrl),
                    content: { image in
                        image
                            .resizable()
                            .scaledToFit()
                    },
                    placeholder: {
                        ProgressView()
                    }
                )
                .scaledToFit()
            
            VStack(alignment: .leading, spacing: 0) {
                HStack(alignment: .top, spacing: 0) { Spacer() }
                Text(speaker.name)
                    .font(DesignSystem.kcH4)
                    .padding(.leading, 16)
                    .padding(.trailing, 16)
                    .padding(.top, 16)
                    .foregroundColor(DesignSystem.greyWhiteColor)
                Text(speaker.position)
                    .padding(.leading, 16)
                    .padding(.trailing, 16)
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.grey50Color)
            }
        }
        .background(DesignSystem.whiteGreyColor)
    }
}

struct AboutConferenceView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            AboutConferenceView()
        }
        .environmentObject(ConferenceModel())
    }
}
