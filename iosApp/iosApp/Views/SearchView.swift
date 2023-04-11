//
//  SearchView.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 03.03.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SearchView: View {
    @EnvironmentObject var conference: ConferenceModel
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    @GestureState private var dragOffset = CGSize.zero
    
    @State var selected = "TALKS"
    @State var query: String = ""
    
    var speakerSearchResult: [Speaker] {
        query.count == 0 ? conference.speakers.all : conference.speakers.all.filter { speaker in
            var result = false
            result = result || speaker.name.lowercased().contains(query.lowercased())
            result = result || speaker.position.lowercased().contains(query.lowercased())
            result = result || speaker.description.lowercased().contains(query.lowercased())
            return result
        }
    }
    
    var sessionsSearchResult: [SessionCardView] {
        (
            query.count == 0 ? conference.cards : conference.cards.filter { card in
            card.title.lowercased().contains(query.lowercased()) || card.speakerLine.lowercased().contains(query.lowercased()) || card.description_.lowercased().contains(query.lowercased())
            }
        ).filter{ card in
            !card.isBreak && !card.isLunch && !card.isParty
        }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            SearchBar(search: $query)
            Divider()
            SearchFilterBar(selected: $selected)
                .padding(16)
                .background(DesignSystem.whiteGreyColor)
            Divider()
            ScrollView {
                if selected == "TALKS" {
                    TalksSearchResult()
                } else {
                    SpeakersSearchResult()
                }
            }
            .background(DesignSystem.whiteGreyColor)
        }
        .background(DesignSystem.grey5BlackColor)
        .navigationBarBackButtonHidden(true)
        .navigationBarItems(trailing: BackButton(icon: DesignSystem.closeIcon))
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack {
                    Text("SEARCH")
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
    
    func TalksSearchResult() -> some View {
        VStack(alignment: .leading, spacing: 0) {
            ForEach(sessionsSearchResult, id: \.id) { card in
                NavigationLink {
                    SessionView(id: card.id)
                } label: {
                    SessionSearchCard(
                        title: card.title,
                        speakerLine: card.speakerLine,
                        description: card.description_,
                        timeLine: card.timeLine,
                        query: query
                    )
                    .multilineTextAlignment(.leading)
                }
                Divider()
            }
        }
    }
    
    func SpeakersSearchResult() -> some View {
        VStack(alignment: .leading, spacing: 0) {
            ForEach(speakerSearchResult, id: \.id) { speaker in
                NavigationLink {
                    SpeakersDetailedView(focusedSpeakerId: speaker.id)
                } label: {
                    SpeakerSearchCard(name: speaker.name, positionLine: speaker.position, description: speaker.description_, photoUrl: speaker.photoUrl, query: query)
                        .multilineTextAlignment(.leading)
                }
                Divider()
            }
        }
    }
}

struct SearchView_Previews: PreviewProvider {
    static var previews: some View {
        SearchView()
            .environmentObject(ConferenceModel())
    }
}

struct SearchFilterBar : View {
    @Binding var selected: String
    var body: some View {
        HStack(alignment: .center) {
            TabButton(label: "TALKS", isSelected: selected == "TALKS", onClick: {
                selected = "TALKS"
            })
            TabButton(label: "SPEAKERS", isSelected: selected == "SPEAKERS", onClick: {
                selected = "SPEAKERS"
            })
            Spacer()
        }
    }
}

struct SearchFilterBarPreview : PreviewProvider {
    @State static var selected: String = "TALKS"
    static var previews: some View {
        SearchFilterBar(selected: $selected)
    }
}

struct SessionSearchCard : View {
    var title: String
    var speakerLine: String
    var description: String
    var timeLine: String
    var query: String

    var body: some View {
        VStack(alignment: .leading) {
            Text(timeLine)
                .font(DesignSystem.kcT2)
                .padding(.top, 0)
                .foregroundColor(DesignSystem.grey50Grey20Color)
                .padding(.bottom, 8)
            
            titleView +
            Text("  /  ")
                .foregroundColor(DesignSystem.grey50Color)
            +
            speakerView +
            Text("  /  ")
                .foregroundColor(DesignSystem.grey50Color)
            +
            descriptionView
        }.padding(16)
    }
    
    var titleView: Text {
        Text("\(highlighted(value: title, query: query))")
            .foregroundColor(DesignSystem.greyWhiteColor)
            .font(DesignSystem.kcH4)
    }
    
    var speakerView: Text {
        Text("\(highlighted(value: speakerLine, query: query))")
            .foregroundColor(DesignSystem.greyWhiteColor)
            .font(DesignSystem.kcT2)
    }
    
    var descriptionWithoutBreaks: String {
        description
            .replacingOccurrences(of: "\n", with: "")
            .replacingOccurrences(of: "\r", with: "")
    }
    
    var descriptionView: Text {
        Text("\(highlighted(value: trimAround(value: descriptionWithoutBreaks, query: query), query: query))")
            .font(DesignSystem.kcT2)
            .foregroundColor(DesignSystem.grey50Color)
    }
}

struct SpeakerSearchCard : View {
    var name: String
    var positionLine: String
    var description: String
    var photoUrl: String
    var query: String

    var body: some View {
        HStack(alignment: .top, spacing: 0) {
            CachedAsyncImage(
                url: URL(string: photoUrl),
                content: { image in
                    image
                        .resizable()
                        .frame(width: 60, height: 60)
                        .scaledToFit()
                },
                placeholder: {
                    ProgressView()
                }
            )
            .frame(width: 60, height: 60)
            
            (titleView + Text("  /  ")
                .foregroundColor(DesignSystem.grey50Color)
             + speakerView +
             Text("  /  ")
                .foregroundColor(DesignSystem.grey50Color)
             + descriptionView)
                .padding(16)
        }
    }
    
    var titleView: Text {
        Text("\(highlighted(value: name, query: query))")
            .foregroundColor(DesignSystem.greyWhiteColor)
            .font(DesignSystem.kcH4)
    }
    
    var speakerView: Text {
        Text("\(highlighted(value: positionLine, query: query))")
            .foregroundColor(DesignSystem.greyWhiteColor)
            .font(DesignSystem.kcT2)
    }
    
    var descriptionView: Text {
        Text("\(highlighted(value: trimAround(value: description, query: query), query: query))")
            .font(DesignSystem.kcT2)
            .foregroundColor(DesignSystem.grey50Color)
    }
}

func highlighted(value: String, query: String) -> AttributedString {
    var result = AttributedString(value)
    guard let range = AttributedString(value.lowercased())
        .range(of: query.lowercased())
    else {
        return result
    }
    
    result[range].foregroundColor = .white
    result[range].backgroundColor = DesignSystem.violetColor
    return result
}

func trimAround(value: String, query: String) -> String {
    if value.isEmpty {
        return ""
    }
    guard let range = value.lowercased()
        .range(of: query.lowercased())
    else {
        let end = min(100, value.count)
        return String(value[value.startIndex..<value.index(value.startIndex, offsetBy: end)]) + "..."
    }
    
    let start = value.index(range.lowerBound, offsetBy: -50, limitedBy: value.startIndex) ?? value.startIndex
    let end = value.index(range.upperBound, offsetBy: 50, limitedBy: value.endIndex) ?? value.endIndex
    
    return "..." + String(value[start..<end]) + "..."
}

struct SpeakerSearchCardPreview : PreviewProvider {
    static var previews: some View {
        ScrollView {
            VStack(spacing: 0) {
                SpeakerSearchCard(
                    name: "Go Multiplatform", positionLine: "Wade Waren", description: "with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task", photoUrl: "https://sessionize.com/image/92c9-400o400o2-c1-9707-4c86-bc82-70b7ce05823c.def0b003-b9b2-420e-88f9-0806049698cd.jpg", query: "Kotlin"
                )
                Divider()
                SpeakerSearchCard(
                    name: "Go Multiplatform", positionLine: "Wade Waren", description: "with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task", photoUrl: "https://sessionize.com/image/92c9-400o400o2-c1-9707-4c86-bc82-70b7ce05823c.def0b003-b9b2-420e-88f9-0806049698cd.jpg", query: "Kotlin"
                )
                Divider()
                SpeakerSearchCard(
                    name: "Go Multiplatform", positionLine: "Wade Waren", description: "with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task", photoUrl: "https://sessionize.com/image/92c9-400o400o2-c1-9707-4c86-bc82-70b7ce05823c.def0b003-b9b2-420e-88f9-0806049698cd.jpg", query: "Kotlin"
                )
            }
        }
    }
}


struct SessionSearchCardPreview : PreviewProvider {
    static var previews: some View {
        SessionSearchCard(
            title: "Go Multiplatform", speakerLine: "Wade Waren", description: "with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task with any Kotlin gradient-based machine learning algorithm requires the tedious task", timeLine: "hdfdgdf time", query: "Kotlin"
        )
    }
}
