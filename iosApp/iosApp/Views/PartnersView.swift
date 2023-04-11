//
//  PartnersView.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 30.01.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct PartnersView: View {
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    @GestureState private var dragOffset = CGSize.zero

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                Group {
                    partnerRow(first: "jetbrains", second: "google")
                    partnerRow(first: "xebia", second: "adyen")
                    partnerRow(first: "kodein", second: "lunatech")
                    partnerRow(first: "sentry", second: "gradle")
                    partnerRow(first: "source", second: "aws")
                }
                
                HStack {
                    Link(destination: URL(string: "mailto:partners@kotlinconf.com")!) {
                        Text("For partnership opportunities, contact \(Text("partners@kotlinconf.com").underline())")
                            .font(DesignSystem.kcT2)
                            .foregroundColor(DesignSystem.grey50Color)
                            .padding(.leading, 16)
                            .padding(.top, 24)
                            .padding(.trailing, 16)
                            .padding(.bottom, 24)
                            .accentColor(DesignSystem.grey50Color)
                            .multilineTextAlignment(.leading)
                    }

                    Spacer()
                }
                .background(DesignSystem.whiteGreyColor)
            }
        }
        .background(DesignSystem.grey5BlackColor)
        .navigationBarBackButtonHidden(true)
        .navigationBarItems(leading: BackButton())
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack {
                    Text("EXHIBITION")
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
    
    func title(_ text: String) -> some View {
        VStack(spacing: 0) {
            HStack {
                Text(text.uppercased())
                    .font(DesignSystem.kcH2)
                    .foregroundColor(DesignSystem.greyGrey5Color)
                
                Spacer()
            }
            .padding(16)
            Divider()
        }
    }
    
    func partnerRow(first: String, second: String? = nil, height: CGFloat = 187) -> some View {
        VStack(spacing: 0) {
            HStack(spacing: 0) {
                partnerCard(name: first, height: height)
                Divider()
                if second == nil {
                    partnerPlaceholder(height: height)
                } else {
                    partnerCard(name: second!, height: height)
                }
            }.padding(0)
            Divider()
        }
    }
    
    func partnerPlaceholder(height: CGFloat) -> some View {
        Rectangle()
            .frame(height: height)
            .foregroundStyle(DesignSystem.whiteGreyColor)
    }
    func partnerCard(name: String, height: CGFloat) -> some View {
        NavigationLink(destination: PartnerView(name: name), label: {
            ZStack {
                Rectangle()
                VStack(alignment: .leading) {
                    HStack(alignment: .top, content: {
                        Image(name)
                            .padding(.top, 16)
                            .padding(.leading, 16)
                        
                        Spacer()
                    })
                    Spacer()
                }
            }
        })
        .frame(height: height)
        .foregroundStyle(DesignSystem.whiteGreyColor)
    }
}

struct PartnersView_Previews: PreviewProvider {
    static var previews: some View {
        PartnersView()
    }
}
