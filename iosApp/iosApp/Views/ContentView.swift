import SwiftUI

import shared

struct ContentView: View {
    @State var selected: Screen = .menu
    @EnvironmentObject var conference: ConferenceModel
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var needsOnboarding: Bool {
        conference.needsOnboarding()
    }
    
    @State var onboardingCompleted = false

	var body: some View {
        if needsOnboarding && !onboardingCompleted {
            Welcome {
                conference.acceptPrivacyPolicy()
            } onRejectPrivacy: {
            } onAcceptNotifications: {
                conference.requestNotificationPermissions()
                conference.completeOnboarding()
                onboardingCompleted = true
            } onRejectNotifications: {
                conference.completeOnboarding()
                onboardingCompleted = true
            }
        } else {
            tabNavigation
        }
	}
    
    var tabNavigation: some View {
        GeometryReader { geometry in
            ZStack {
                VStack(spacing: 0) {
                    SelectView()
                    VStack(spacing: 0) {
                        Divider()
                        HStack(spacing: 0) {
                            Tab(name: .menu, icon: DesignSystem.menuIcon, activeIcon: DesignSystem.menuActiveIcon)
                            Tab(name: .agenda, icon: DesignSystem.timeIcon, activeIcon: DesignSystem.timeActiveIcon)
                            Tab(name: .speakers, icon: DesignSystem.speakersIcon, activeIcon: DesignSystem.speakersActiveIcon)
                            Tab(name: .bookmarks, icon: DesignSystem.bookmarksIcon, activeIcon: DesignSystem.bookmarksActiveIcon)
                            Tab(name: .map, icon: DesignSystem.locationIcon, activeIcon: DesignSystem.locationActiveIcon)
                        }
                        .padding(.bottom, 4)
                    }
                }
                .edgesIgnoringSafeArea(.bottom)
            }
        }
    }
    
    
    func SelectView() -> some View {
        NavigationView {
            switch selected {
                case .menu: MenuView()
                case .bookmarks: BookmarksView()
                case .speakers: SpeakersView()
                case .agenda: AgendaView()
                case .map: MapTabView()
            }
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }

    func Tab(name: Screen, icon: String, activeIcon: String) -> some View {
        HStack {
            Spacer()
            Image(selected == name ? activeIcon : icon)
                .foregroundColor(
                    selected == name ? DesignSystem.greyGrey5Color : DesignSystem.grey50Color
                )
                .padding(.bottom, 20)
                .padding(.top, 16)
                
            Spacer()
        }
        .background(selected == name ? DesignSystem.grey5GreyColor : DesignSystem.whiteBlackColor)
        .onTapGesture {
            self.selected = name
        }
    }
}

enum Screen {
    case menu
    case bookmarks
    case speakers
    case agenda
    case map
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
            .environmentObject(ConferenceModel())
    }
}
