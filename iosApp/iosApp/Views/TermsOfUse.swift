//
//  TermsOfUse.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 28.03.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct TermsOfUse: View {
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    @GestureState private var dragOffset = CGSize.zero
    
    var showClose = true
    var onClose: () -> Void = {}
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    HStack {
                        Text("KotlinConf App Terms of Use".uppercased())
                            .padding(.leading, 16)
                            .padding(.trailing, 16)
                            .font(DesignSystem.kcH2)
                            .padding(.top, 24)
                            .foregroundColor(DesignSystem.greyWhiteColor)
                        
                        Spacer()
                    }
                    Text("Version 1.0, effective as of March 21, 2023".uppercased())
                        .font(DesignSystem.kcT2)
                        .padding(.leading, 16)
                        .padding(.trailing, 16)
                        .padding(.top, 16)
                        .padding(.bottom, 24)
                        .foregroundColor(DesignSystem.greyWhiteColor)
                }
                .background(DesignSystem.grey5BlackColor)
                Divider()
                
                partOne
                    .padding(.top, 24)
                partTwo
                partThree
                
            }
        }
        .navigationBarBackButtonHidden(true)
        .navigationBarItems(leading: BackButton())
        .navigationBarTitleDisplayMode(.inline)
        .gesture(DragGesture().updating($dragOffset, body: { (value, state, transaction) in
             if(value.startLocation.x < 20 && value.translation.width > 100) {
                 self.presentationMode.wrappedValue.dismiss()
             }
        }))
        .background(DesignSystem.whiteGreyColor)
    }
    
    var partOne: some View {
        VStack(alignment: .leading, spacing: 0) {
            Regular(value: """
IMPORTANT! READ CAREFULLY:
THIS IS A LEGAL AGREEMENT. BY CLICKING ON THE “I AGREE” (OR SIMILAR) BUTTON THAT IS PRESENTED TO YOU AT THE TIME WHEN YOU DOWNLOAD, INSTALL, OR OTHERWISE KOTLINCONF APP, YOU BECOME A PARTY TO THIS AGREEMENT AND YOU CONSENTS TO BE BOUND BY ALL THE TERMS AND CONDITIONS SET FORTH BELOW.
JetBrains and You may each also be referred to individually as a “Party” or jointly as the “Parties”.
""")
            Subtitle(value: "1. PARTIES")
            
            Regular(value: """
1.1. “You” means a natural person entering into and exercising rights under this Agreement.
1.2. “JetBrains” or “we” means JetBrains Expo B.V., having its principal place of business at Huidekoperstraat 26, 1017 ZM, Amsterdam, the Netherlands, registered with the Dutch Chamber of Commerce under the number: 74098896, VAT ID number: NL85977129B01.
""")
            
            Subtitle(value: "2. GRANT OF RIGHTS")
            Regular(value: """
2.1. KontlinConf App is licensed for use under the Apache 2.0 license (see [https://github.com/JetBrains/kotlinconf-app/blob/master/LICENSE](https://github.com/JetBrains/kotlinconf-app/blob/master/LICENSE)).
In connection with Your use of KotlinConf App, You may:
(i) access, download, and use of any content made available by JetBrains in the KotlinConf App in connection with KotlinConf, the official event by JetBrains (“KotlinConf”); and
(ii) share Your feedback through KotlinConf App with JetBrains in compliance with the Code of Conduct available at [https://kotlinconf.com/code-of-conduct](https://kotlinconf.com/code-of-conduct).
""")
            Subtitle(value: "3. USE OF KONTLINCONF APP")
            Regular(value: """
In order to use all features of KotlinConf App, You must have an Internet connection. KotlinConf App does not require activation.
""")
            
            Subtitle(value: "4. FEEDBACK")
            Regular(value: """
You have no obligation to provide us with ideas, suggestions, feedback, or proposals (“Feedback”).  However, if You submit Feedback to us, then You grant us a non-exclusive, worldwide, royalty-free license that is sub-licensable and transferable, to make, use, sell, have made, offer to sell, import, reproduce, publicly display, distribute, modify, or publicly perform the Feedback in any manner without any obligation, royalty or restriction based on intellectual property rights or otherwise.
""")
            
            Subtitle(value: "5. LIMITED WARRANTY")
            
        }
    }
    
    var partTwo: some View {
        VStack(alignment: .leading, spacing: 0) {
            Regular(value: """
5.1. KotlinConf App is provided to You on an “as is” and “as available” basis. Use of KotlinConf App is at Your own risk.
5.2. JetBrains makes no warranty as to KotlinConf App’s use or performance. To the maximum extent permitted by applicable law, JetBrains (or its affiliates, shareholders, agents, directors, and employees), its licensors, suppliers (which shall include the providers of third party software) (collectively “JetBrains Parties”) disclaim all warranties and conditions, whether express or implied (including, but not limited to, implied warranties of merchantability; fitness for a particular purpose; title; and non-infringement) with regard to KotlinConf App and the provision of or failure to provide support services.
5.3. To the maximum extent permitted by applicable law, JetBrains Parties do not represent or warrant that KotlinConf App: (a) is accurate, reliable or correct; (b) will meet any of Your requirements; (c) will be available at any particular time or location, uninterrupted or secure; (d) is free of defects or errors and that any, if found, will be corrected; and/or (e) is free of viruses or other harmful components.
5.4. You may have other rights which may not be limited or excluded and which may vary from jurisdiction to jurisdiction. This document is not intended to negatively affect such rights.
""")
            Subtitle(value: "6. DISCLAIMER OF DAMAGES")
            Regular(value: """
6.1. To the maximum extent permitted by applicable law, in no event will JetBrains Parties be liable to You or anyone else for: (a) any loss of use, data, goodwill, or profits, whether or not foreseeable; (b) any loss or damages in connection with termination or suspension of Your access to KotlinConf App in accordance with this Agreement; or (c) any special, incidental, indirect, consequential, exemplary or punitive damages whatsoever (even if the relevant JetBrains Party has been advised of the possibility of these damages), including those (x) resulting from loss of use, data, or profits, whether or not foreseeable, (y) based on any theory of liability, including breach of contract or warranty, strict liability, negligence or other tortious action, or (z) arising from any other claim arising out of or in connection with Your use of or access to KotlinConf App or JetBrains content presented therein. the foregoing limitation of liability shall apply to the fullest extent permitted by law in the applicable jurisdiction.
6.2. The total liability of the JetBrains Parties in any matter arising out of or in relation to this Agreement is limited to the amount paid, if any, for KotlinConf App or five (5) EUR, whichever is less. This limitation will apply even if the JetBrains Parties have been advised of the possibility of liability exceeding such an amount and notwithstanding any failure of the essential purpose of any limited remedy.

""")
            
            Subtitle(value: "7. EXPORT REGULATIONS")
            Regular(value: """
7.1. You must comply with all applicable laws and regulations with regard to economic sanctions, export controls, import regulations, restrictive measures, and trade embargoes (“Sanctions”), including those of the European Union and United States. You declare and warrant that You are not a person targeted by Sanctions nor You are otherwise acting on behalf of any entity or person targeted by Sanctions. You agree that You will not download or otherwise export or re-export KotlinConf App or any related technical data directly or indirectly to any person targeted by Sanctions or download or otherwise use KotlinConf App for any end-use prohibited or restricted by Sanctions.
7.2. You must immediately report any concerns of non-compliance regarding Sanctions to [compliance@jetbrains.com](compliance@jetbrains.com), or [legal@jetbrains.com](compliance@jetbrains.com) and cooperate with JetBrains in its efforts to verify and ensure compliance with Sanctions.
""")
            
            Subtitle(value: "8. TEMPORARY SUSPENSION")
            Regular(value: """
JetBrains reserves the right to suspend Your access to KotlinConf App if Your use of KotlinConf App is in violation of this Agreement, disrupts or imminently threatens the security, integrity, or availability of KotlinConf participants or organizors.
""")
            
            Subtitle(value: "9. TERM AND TERMINATION")
            Regular(value: """
9.1. The term of this Agreement will commence upon Your acceptance of this Agreement as set out in the preamble above, and will continue for the term of Your use of KotlinConf App unless terminated earlier in accordance with this Agreement or applicable law. Upon termination, You must discontinue using KotlinConf App and delete all copies of KotlinConf App from Your device.
9.2. You may terminate this Agreement with immediate effect by notifying JetBrains of such termination.
9.3. JetBrains may terminate this Agreement if:
(i) You have materially breached this Agreement and fail to cure such breach within thirty (30) days of written notice;
(ii) JetBrains is required to do so by law, including where the provision of KotlinConf App to You is, or becomes, unlawful); or
(iii) JetBrains elects to discontinue providing KotlinConf App, in whole or in part.
9.4. JetBrains will make reasonable efforts to notify You by email (to the email address provided by You) as follows thirty (30) days prior to termination of the Agreement in the events specified in Sections 11.3(C) above, if applicable.
9.5. Upon the expiration or termination of this Agreement, Sections 4, 5, 6, 7 and 10 of this Agreement survive.
""")
            
            Subtitle(value: "10. GENERAL")
        }
    }
    
    var partThree: some View {
        VStack(alignment: .leading, spacing: 0) {
            Regular(value: """
10.1. Entire Agreement. This Agreement constitutes together with the Code of Conduct available at [https://kotlinconf.com/code-of-conduct](https://kotlinconf.com/code-of-conduct) the entire agreement between You and JetBrains with respect to Your use of KotlinConf App.
10.2. Reservation of Rights. JetBrains reserves the right at any time to alter features, functions, terms of use, JetBrains Content or other characteristics of KotlinConf App. Nothing in this Agreement limits any rights a consumer may have under applicable consumer protection laws.
10.3. Severability. If a particular term of this Agreement is not enforceable, the unenforceability of that term will not affect any other terms of this Agreement.
10.4. Interpretation. Headings and titles are for convenience only and do not affect the interpretation of this Agreement. Terms such as “including” are not exhaustive.
10.5. No Waiver. Our failure to enforce or exercise any part of this Agreement is not a waiver of that section.
10.6. Notice. JetBrains may deliver any notice to You via electronic mail to an email address provided by You, registered mail, personal delivery or renowned express courier (such as DHL, FedEx or UPS). Any such notice will be deemed to be effective (i) on the day the notice is sent to You via email, (ii) upon personal delivery, (iii) one (1) day after deposit with an express courier, (iv) or five (5) days after deposit in the mail, whichever occurs first.
10.7. Governing Law. This Agreement is governed by the laws of the Netherlands, without regard to conflict of laws principles and specifically excluding the United Nations Convention on Contracts for the International Sale of Goods. The Parties to the agreement constituted by this Agreement undertake to use best commercial efforts to amicably settle any disputes arising hereunder (“Dispute”).
10.8. Dispute Resolution. Should the Parties to this Agreement fail to settle a Dispute amicably, You and we both agree that any Dispute-related litigation may only be brought in, and shall be subject to the jurisdiction of, any competent court of the Netherlands, unless provided otherwise by applicable consumer law. Any disputes between JetBrains and an individual consumer, who is an EU resident, can be settled out of court through alternative dispute resolution (ADR). This can be done through the ODR platform accessible through: [http://ec.europa.eu/consumers/odr/](http://ec.europa.eu/consumers/odr/).
13.12. Data Privacy. By accepting this Agreement, You acknowledge that JetBrains will process personal data in accordance with the KotlinConf App Privacy Policy (available at: [https://kotlinconf.com/kotlinconf-2023-app-privacy-policy.pdf](https://kotlinconf.com/kotlinconf-2023-app-privacy-policy.pdf)).
13.13. Force Majeure. Neither Party shall be in breach of this Agreement, or otherwise liable to the other, by reason of any delay in performance, or non-performance of any of its obligations under this Agreement (except payment obligations), arising directly from an act of God, fire, flood, natural disaster, act of terrorism, strike, lock-out, labour dispute, public health emergency, civil commotion, riot, or act of war.
13.14. Children and minors. If You are under 18 years old, then by entering into this Agreement You explicitly stipulate, that (i) You have legal capacity to conclude this Agreement or that You have valid consent from a parent or legal guardian to do so and (ii) You understand the KotlinConf App Privacy Policy available at: [https://kotlinconf.com/kotlinconf-2023-app-privacy-policy.pdf](https://kotlinconf.com/kotlinconf-2023-app-privacy-policy.pdf). You may not enter into this Agreement if You are under 16 years old. If You do not understand this section, do not understand the KotlinConf App Privacy Policy or do not know whether You have the legal capacity to accept these terms, please ask Your parent or legal guardian for help.
For further information, please contact us at [info@kotlinconf.com](info@kotlinconf.com).
""")
        }
    }

}

struct TermsOfUse_Previews: PreviewProvider {
    static var previews: some View {
        TermsOfUse()
    }
}
