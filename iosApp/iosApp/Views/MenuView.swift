//
//  MenuView.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 04.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct MenuView: View {
    @GestureState private var dragOffset = CGSize.zero
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>

    var body: some View {
        ZStack {
            ScrollView {
                VStack(spacing: 0) {
                    Image(DesignSystem.menuLogo)
                        .resizable()
                        .scaledToFit()
                        .padding(.top, 25)
                        .padding(.bottom, 25)
                        .padding(.leading, 16)
                        .padding(.trailing, 16)
                    
                    Divider()
                    
                    NavigationLink(destination: SearchView()) {
                        MenuItem(text: "SEARCH", icon: DesignSystem.searchIcon)
                    }
                    
                    menuItems
                    
                    Spacer()
                }
            }
        }
        .background(DesignSystem.grey5BlackColor)
        .navigationBarBackButtonHidden(true)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack {
                    Text("MENU")
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.grey50Color)
                }
            }
        }
    }
    
    var menuItems: some View {
        VStack(alignment: .leading, spacing: 0) {
            NavigationLink(destination: AboutConferenceView()) {
                MenuItem(text: "KOTLINCONF`23")
            }
            NavigationLink(
                destination: TextView(title: "THE APP", text: ABOUT_APP)
            ) {
                MenuItem(text: "THE APP")
            }
            NavigationLink(destination: PartnersView()) {
                MenuItem(text: "EXHIBITION")
            }
            NavigationLink(
                destination: TextView(title: "CODE OF CONDUCT", text: CODE_OF_CONDUCT, bigTitle: "KOTLINCONF CODE OF CONDUCT")
            ) {
                MenuItem(text: "CODE OF CONDUCT")
            }
            NavigationLink(
                destination: PrivacyPolicyFull(showClose: false)
            ) {
                MenuItem(text: "PRIVACY POLICY")
            }
            NavigationLink(
                destination: TermsOfUse()
            ) {
                MenuItem(text: "TERMS OF USE")
            }
            
            social
        }
    }
    
    
    var social: some View {
        VStack(spacing: 0) {
            Divider()
            HStack(alignment: .top, spacing: 0) {
                Button {
                    UIApplication.shared.open(URL(string: "https://twitter.com/kotlinconf")!)
                } label: {
                    TwitterIcon()
                }
                Divider()
                Button {
                    UIApplication.shared.open(URL(string: "https://kotlinlang.slack.com/messages/kotlinconf")!)
                } label: {
                    SlackIcon()
                }
            }
            .background(DesignSystem.whiteGreyColor)
            Divider()
        }
    }
}


struct MenuView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            MenuView()
        }
    }
}
