//
//  SpeakersView.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 07.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SpeakersView: View {
    @EnvironmentObject var conference: ConferenceModel
    @State var search: String = ""
    
    @Environment(\.isSearching)
    private var isSearching: Bool
    
    private var speakers: [Speaker] {
        conference.speakers.all.filter({ it in
            let name: String = it.name
            if search.isEmpty {
                return true
                
            }
            return name.lowercased().contains(search.lowercased())
        })
    }
    
    var body: some View {
        NavigationRoot(title: "SPEAKERS") {
            ScrollView {
                VStack(spacing: 0, content: {
                    Divider()
                    ForEach(speakers, id: \.id) { speaker in
                        NavigationLink(destination: {
                            SpeakersDetailedView(
                                focusedSpeakerId: speaker.id
                            )
                        }, label: {
                            SpeakerCardSmall(
                                name: speaker.name,
                                title: speaker.position,
                                photoUrl: speaker.photoUrl
                            ).multilineTextAlignment(.leading)
                        })
                    }
                })
            }
        }
    }
}

struct SpeakersView_Previews: PreviewProvider {
    static var previews: some View {
        SpeakersView()
            .environmentObject(ConferenceModel())
    }
}
