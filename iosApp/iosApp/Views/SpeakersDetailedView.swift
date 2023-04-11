//
//  SpeakersDetailedView.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 24.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SpeakersDetailedView: View {
    @EnvironmentObject var conference: ConferenceModel
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var speakers: [Speaker] {
        return conference.speakers.all
    }
    var focusedSpeakerId: String
    @GestureState private var dragOffset = CGSize.zero
 

    var body: some View {
        ScrollViewReader { reader in
            ScrollView {
                ForEach(speakers, id: \.id) { speaker in
                    VStack(spacing: 0) {
                        SpeakerCardBig(id: speaker.id)
                    }.id(speaker.id)
                }
            }
            .background(DesignSystem.whiteGreyColor)
            .navigationBarBackButtonHidden(true)
            .navigationBarItems(leading: BackButton())
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    VStack {
                        Text("SPEAKERS")
                            .font(DesignSystem.kcT2)
                            .foregroundColor(DesignSystem.grey50Color)
                    }
                }
            }
            .onAppear {
                if !focusedSpeakerId.isEmpty {
                    reader.scrollTo(focusedSpeakerId, anchor: .top)
                }
            }
            .gesture(DragGesture().updating($dragOffset, body: { (value, state, transaction) in
                 if(value.startLocation.x < 20 && value.translation.width > 100) {
                     self.presentationMode.wrappedValue.dismiss()
                 }
            }))
        }
    }
}

struct SpeakersDetailedView_Previews: PreviewProvider {
    static var previews: some View {
        SpeakersDetailedView(focusedSpeakerId: "80f570c3-27df-4756-b04a-76b2d6f220c4")
            .environmentObject(ConferenceModel())
    }
}
