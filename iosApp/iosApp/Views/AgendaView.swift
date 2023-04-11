//
//  AgendaView.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 07.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct AgendaView: View {
    @EnvironmentObject var conference: ConferenceModel
    
    var days: [Day] {
        conference.agenda.days
    }
    
    var firstLive: TimeSlot? {
        days.flatMap { day in
            day.timeSlots
        }.first { slot in slot.isLive }
    }
    
    var labels: [String] {
        days.map { day in day.title }
    }
    
    @State var selectedLabel: String = "APRIL 13"
    @State var scrollLabel: String = ""

    var body: some View {
        TabBar(
            labels: labels,
            selectedLabel: selectedLabel,
            onClick: { label in
            scrollLabel = label
        }) {
            agendaView
        }
    }

    var agendaView: some View {
        ScrollView {
            ScrollViewReader { reader in
                VStack(alignment: .leading, spacing: 0) {
                    ForEach(days, id: \.title) { day in
                        AgendaDayRibbon(day: day.title)
                            .id(day.title)
                            .overlay {
                                GeometryReader { (reader) -> Color in
                                    let f = reader.frame(in: .named("scroll"))
                                    if selectedLabel != day.title && f.minY <= 200 && f.minY >= -200 {
                                        DispatchQueue.main.async {
                                            selectedLabel = day.title
                                        }
                                    }
                                    
                                    return Color.white.opacity(0)
                                }
                            }
                        dayView(day: day)
                    }
                }.onChange(of: scrollLabel) { newValue in
                    if scrollLabel != "" {
                        reader.scrollTo(newValue, anchor: .top)
                        scrollLabel = ""
                    }
                }
                .onChange(of: firstLive) { newValue in
                    let id = newValue?.id
                    if id != nil {
                        reader.scrollTo(id, anchor: .top)
                        let day = "APRIL \(newValue?.startsAt.dayOfMonth ?? 13)"
                        DispatchQueue.main.async {
                            selectedLabel = day
                        }
                    }
                }
                .onAppear {
                    let id = firstLive?.id
                    if id != nil {
                        reader.scrollTo(id, anchor: .top)
                        let day = "APRIL \(firstLive?.startsAt.dayOfMonth ?? 13)"
                        DispatchQueue.main.async {
                            selectedLabel = day
                        }
                    }
                }
            }
        }.coordinateSpace(name: "scroll")
    }
    
    func dayView(day: Day) -> some View {
        ForEach(day.timeSlots, id: \.id) { group in
            if (group.isBreak ||  group.isLunch) {
                if (!group.isFinished) {
                    Break(duration: group.duration, title: group.title, isLive: group.isLive, isBreak: group.isBreak)
                        .id(group.id)
                }
            } else if group.isParty {
                AgendaHeader(title: group.title, isLive: group.isLive)
                    .id(group.id)
                Party(isFinished: group.isFinished)
                Divider()
            } else {
                AgendaHeader(title: group.title, isLive: group.isLive)
                    .id(group.id)
                ForEach(group.sessions, id: \.id) { session in
                    NavigationLink(destination: {
                        SessionView(id: session.id)
                    }, label: {
                        AgendaItem(session: session)
                            .multilineTextAlignment(.leading)
                    })
                }
            }
        }
    }
}

struct AgendaView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            AgendaView()
        }
            .environmentObject(ConferenceModel())
    }
}


struct ScrollViewOffsetPreferenceKey : PreferenceKey {
    static var defaultValue: CGFloat = 0
    
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value += nextValue()
    }
    
    typealias Value = CGFloat
}
