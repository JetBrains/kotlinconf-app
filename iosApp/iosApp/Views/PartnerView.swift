//
//  PartnerView.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 30.01.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct PartnerView: View {
    @EnvironmentObject var conference: ConferenceModel
    @GestureState private var dragOffset = CGSize.zero
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var name: String
    
    var displayName: String {
        (name == "android" || name == "google") ? "Android & Google" : name
    }
    
    @Environment(\.colorScheme) var colorScheme: ColorScheme
    var map: String {
        colorScheme == .dark ? MAPS[1] : MAPS_LIGHT[1]
    }
    
    var descriptionText: String {
        conference.partnerDescription(name: displayName)
    }
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                HStack {
                    Spacer()
                    Image("\(displayName)-big")
                        .resizable()
                        .frame(width: 270, height: 112)
                        .scaledToFit()
                    Spacer()
                }
                .frame(height: 176)
                .background(DesignSystem.grey5BlackColor)
                .padding(0)
                
                Text(displayName.uppercased())
                    .font(DesignSystem.kcT2Bold)
                    .foregroundColor(DesignSystem.greyGrey5Color)
                    .padding(.leading, 16)
                    .padding(.top, 24)
                
                Text(descriptionText)
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.greyGrey20Color)
                    .padding(.leading, 16)
                    .padding(.trailing, 16)
                    .padding(.top, 24)
                
                HStack {
                    Text("EXHIBITION")
                        .font(DesignSystem.kcT2)
                }
                .foregroundColor(DesignSystem.grey50Color)
                .padding(.leading, 19)
                .padding(.top, 24)

                Divider()
                    .padding(.top, 16)
                NavigationLink {
                    MapTabView()
                } label: {
                    MapSwiftUIWrapper(url: map, onClick: { _, _, _ in
                    }, cameraOptions: EXHIBITION, isFrozen: true)
                }
                .frame(height: 400)
                
                Divider()
                    .padding(.bottom, 24)
            }
            .background(DesignSystem.whiteGreyColor)
        }
        .navigationBarBackButtonHidden(true)
        .navigationBarItems(leading: BackButton())
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack {
                    Text(name.capitalized)
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
}

struct PartnerView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            PartnerView(name: "android")
        }
    }
}
