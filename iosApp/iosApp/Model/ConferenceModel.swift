//
//  ConferenceModel.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 17.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

final class ConferenceModel : ObservableObject {
    private let service = ConferenceService(context: ApplicationContext(), endpoint: "https://kotlin-conf-staging.labs.jb.gg/")

    @Published var speakers: Speakers = Speakers(all: [])
    @Published var agenda: Agenda = Agenda(days: [])
    @Published var votes: Votes = Votes(votes: [])
    @Published var cards: [SessionCardView] = []
    @Published var time: String = ""
    
    init() {
        service.speakers.subscribe { newSpeakers in
            self.speakers = newSpeakers ?? Speakers(all: [])
        }

        service.agenda.subscribe { newAgenda in
            self.agenda = newAgenda ?? Agenda(days: [])
        }
        
        service.sessionsCards.subscribe { cards in
            self.cards = cards as? [SessionCardView] ?? []
        }
        
        service.time.subscribe { time in
            guard let time = time else {return}
            self.time = "\(time.dayOfMonth) \(time.month.name) \(time.year) \(time.hours):\(time.minutes)"
        }
    }

    func toggleFavorite(sessionId: String) {
        service.toggleFavorite(sessionId: sessionId)
    }
    
    func vote(sessionId: String, rating: Score?, showPrivacy: @escaping () -> Void) {
        service.vote(sessionId: sessionId, rating: rating) { result, error in
            if result?.boolValue != true {
                showPrivacy()
            }
        }
    }
    
    func sendFeedback(sessionId: String, feedback: String,  showPrivacy: @escaping () -> Void) {
        service.sendFeedback(sessionId: sessionId, feedbackValue: feedback, completionHandler: { result, error in
            if result?.boolValue != true {
                showPrivacy()
            }
        })
    }

    func sessionsForSpeaker(id: String) -> [SessionCardView] {
        return service.sessionsForSpeaker(id: id)
    }
    
    func speakerById(id: String) -> Speaker {
        return speakers.all.first { speaker in
            speaker.id == id
        } ?? ConferenceServiceKt.UNKNOWN_SPEAKER
    }
    
    func sessionById(id: String) -> SessionCardView {
        return cards.first { card in
            card.id == id
        } ?? ConferenceServiceKt.UNKNOWN_SESSION_CARD
    }
    
    func needsOnboarding() -> Bool {
        return service.needsOnboarding()
    }
    
    func completeOnboarding() {
        service.completeOnboarding()
    }
    
    func acceptPrivacyPolicy() {
        service.acceptPrivacyPolicy()
    }
    
    func requestNotificationPermissions() {
        service.requestNotificationPermissions()
    }
    
    func partnerDescription(name: String) -> String {
        return service.partnerDescription(name: name)
    }
}
