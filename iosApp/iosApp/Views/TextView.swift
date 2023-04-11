//
//  CodeOfConductView.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 04.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct TextView: View {
    @GestureState private var dragOffset = CGSize.zero
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    var title: String
    var text: String

    var bigTitle: String? = nil

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                HStack {
                    Spacer()
                }
                if bigTitle != nil {
                    title(bigTitle!)
                }
                VStack(alignment: .leading, spacing: 0) {
                    Text(.init(text))
                        .font(DesignSystem.kcT2Uppercase)
                        .lineSpacing(22 - 16)
                        .foregroundColor(DesignSystem.greyGrey20Color)
                        .padding(.bottom, 24)
                        .padding(.top, 24)
                        .padding(.leading, 16)
                        .padding(.trailing, 16)
                }
            }
        }
        .background(DesignSystem.whiteGreyColor)
        .navigationBarBackButtonHidden(true)
        .navigationBarItems(leading: BackButton())
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack {
                    Text(title)
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
        .background(DesignSystem.grey5BlackColor)
    }
    
}

struct CodeOfConductView_Previews: PreviewProvider {
    static var previews: some View {
        TextView(title: "CODE OF CONDUCT", text: CODE_OF_CONDUCT)
    }
}

let CODE_OF_CONDUCT = "This event is dedicated to providing a harassment-free experience for everyone, regardless of gender, sexual orientation, ability, physical appearance, body size, race, or religion. We do not tolerate harassment of event participants in any form. Sexual language and imagery is not appropriate for any event venue, including talks. Event participants violating these rules may be sanctioned or expelled from the event without a refund at the discretion of the event organizers. Any form of written or verbal communication that can be harassing to any attendee, speaker, or staff member is not allowed at this event. Harassment includes offensive verbal comments related to gender, sexual orientation, ability, physical appearance, body size, race, or religion; sexual images in public spaces; deliberate intimidation; stalking; following; photography or recording without the subject's consent photography or recording; sustained disruption of talks or other activities; inappropriate physical contact; and unwelcome sexual attention. Exhibitors in the expo hall, sponsor or vendor booths, or similar areas are also subject to the anti-harassment policy. Exhibitors should not use sexualized images, activities, or other materials. Booth staff, including volunteers, should not wear sexualized clothing, uniforms, or costumes, or otherwise create a sexualized environment. We expect participants to follow these rules at all event venues and event-related social gatherings. Please inform an event staff member (identified by their official t-shirts and/or special badges) if you feel a violation has taken place. Participants asked to stop any harassing behavior are expected to comply immediately. Event staff will be happy to help participants contact hotel/venue security or local law enforcement, provide escorts, or otherwise ensure those experiencing harassment feel safe for the duration of the event. We value your attendance."

let ABOUT_APP = """
The KotlinConf application is developed by the JetBrains team with Kotlin Multiplatform Mobile shared logic, Compose on Android, and Swift UI on iOS.

Check out the [GitHub repository](https://github.com/JetBrains/kotlinconf-app) for the source code and more technical details about the application.

Enjoy the app, and please share your feedback to help us make it even better!
"""
