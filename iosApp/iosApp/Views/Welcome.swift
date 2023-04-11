//
//  PrivacyPolicy.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 10.03.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

enum WelcomeState {
    case privacy
    case privacyDetails
    case notifications
}

struct Welcome: View {
    var onAcceptPrivacy: () -> Void = {}
    var onRejectPrivacy: () -> Void = {}
    var onAcceptNotifications: () -> Void = {}
    var onRejectNotifications: () -> Void = {}
    
    @State var state: WelcomeState = .privacy

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            ScrollView {
                switch state {
                case .privacy:
                    privacyView
                case .privacyDetails:
                    PrivacyPolicyFull(showClose: true) {
                        state = .privacy
                    }
                case .notifications:
                    notificationsView
                }
            }
            .background(DesignSystem.whiteGreyColor)
            
            Divider()
            HStack(alignment: .center, spacing: 0) {
                Button {
                    if (state == .privacy || state == .privacyDetails) {
                        state = .notifications
                        onRejectPrivacy()
                    } else {
                        onRejectNotifications()
                    }
                } label: {
                    HStack(alignment: .top, spacing: 0) {
                        Spacer()
                        Text("REJECT")
                            .padding(.leading, 16)
                            .padding(.trailing, 16)
                            .padding(.top, 24)
                            .padding(.bottom, 24)
                            .foregroundColor(DesignSystem.greyWhiteColor)
                        Spacer()
                    }
                }
                Divider()
                Button {
                    if (state == .privacy || state == .privacyDetails) {
                        state = .notifications
                        onAcceptPrivacy()
                    } else {
                        onAcceptNotifications()
                    }
                } label: {
                    HStack(alignment: .center, spacing: 0) {
                        Spacer()
                        Text("ACCEPT")
                            .padding(.leading, 16)
                            .padding(.trailing, 16)
                            .padding(.top, 24)
                            .padding(.bottom, 24)
                            .foregroundColor(DesignSystem.whiteGreyColor)
                        Spacer()
                    }
                    .background(DesignSystem.greyWhiteColor)
                }
            }
            .frame(height: 70)
            .ignoresSafeArea()
        }
    }
    
    
    var privacyView: some View {
        VStack(alignment: .leading, spacing: 0) {
            Image(DesignSystem.privacyPolicyLogo)
                .resizable()
                .scaledToFill()
                .padding(.top, 36)
            
            Text("PRIVACY POLICY")
                .padding(.leading, 16)
                .font(DesignSystem.kcH2)
                .padding(.top, 48)
                .foregroundColor(DesignSystem.greyWhiteColor)
            
            Text(PRIVACY_SHORT)
                .font(DesignSystem.kcT2)
                .padding(.leading, 16)
                .padding(.trailing, 16)
                .padding(.top, 24)
                .foregroundColor(DesignSystem.greyWhiteColor)
            
            Button {
                state = .privacyDetails
            } label: {
                Text("READ THE APP PRIVACY POLICY")
                    .underline()
                    .padding(.leading, 16)
                    .padding(.trailing, 16)
                    .padding(.top, 24)
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.greyWhiteColor)
            }
            
            HStack {
                Rectangle()
                    .frame(width: 12, height: 12)
                    .foregroundColor(DesignSystem.blackWhiteColor)
                    .border(DesignSystem.grey50Color)
                
                Rectangle()
                    .frame(width: 12, height: 12)
                    .foregroundColor(DesignSystem.whiteBlackColor)
                    .border(DesignSystem.grey50Color)
            }
            .padding(.leading, 16)
            .padding(.top, 24)
        }
    }

    var notificationsView: some View {
        VStack(alignment: .leading, spacing: 0) {
            Image(DesignSystem.notificationsLogo)
                .resizable()
                .scaledToFill()
                .padding(.top, 36)
            
            Text("Do you want reminders?".uppercased())
                .padding(.leading, 16)
                .font(DesignSystem.kcH2)
                .padding(.top, 48)
                .foregroundColor(DesignSystem.greyWhiteColor)
            
            Text("Get reminders about talks that you don’t want to miss. You will receive a message 5 minutes before the session begins so you have time to get ready.")
                .font(DesignSystem.kcT2)
                .padding(.leading, 16)
                .padding(.trailing, 16)
                .padding(.top, 24)
                .foregroundColor(DesignSystem.greyWhiteColor)
            
            
            HStack {
                Rectangle()
                    .frame(width: 12, height: 12)
                    .foregroundColor(DesignSystem.whiteBlackColor)
                    .border(DesignSystem.grey50Color)
                Rectangle()
                    .frame(width: 12, height: 12)
                    .foregroundColor(DesignSystem.blackWhiteColor)
                    .border(DesignSystem.grey50Color)
            }
            .padding(.leading, 16)
            .padding(.top, 24)
        }
    }
}

struct PrivacyPolicyFull: View {
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    @GestureState private var dragOffset = CGSize.zero
    
    var showClose = true
    var onClose: () -> Void = {}
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    HStack {
                        Text("KotlinConf App Privacy Policy".uppercased())
                            .padding(.leading, 16)
                            .padding(.trailing, 16)
                            .font(DesignSystem.kcH2)
                            .padding(.top, 24)
                            .foregroundColor(DesignSystem.greyWhiteColor)
                        
                        Spacer()
                        
                        if showClose {
                            Button {
                                onClose()
                            } label: {
                                Image(DesignSystem.closeIcon)
                                    .foregroundColor(DesignSystem.greyWhiteColor)
                                    .padding(.trailing, 16)
                            }
                        }
                    }
                    Text("Version 1.0 of March 21, 2023".uppercased())
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
                partTwo
                partThree
                partFour
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
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack {
                    Text("APP PRIVACY POLICY")
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.grey50Color)
                }
            }
        }
        .background(DesignSystem.whiteGreyColor)
    }
    
    var partOne: some View {
        VStack(alignment: .leading, spacing: 0) {
            Regular(value: "In this KotlinConf App Privacy Policy (“Privacy Policy”), we describe the types of data, including Personal Data (collectively, \"data\"), that we and our associated companies collect from you when you use the KotlinConf app and the functionalities and services it offers (collectively, the \"App\"), how we and our associated companies use and disclose that data, and your options to access or update your data. The data controllers are JetBrains Expo B.V. with its business address at Huidekoperstraat 26, 1017 ZM, the Netherlands, and [its associated companies](https://www.jetbrains.com/company/contacts/#headquarters-international-sales): JetBrains N.V., JetBrains s.r.o. and JetBrains GmbH. JetBrains and its associated companies act as joint data controllers, who are jointly responsible for compliance with data protection legislation. JetBrains Expo B.V. is primarily responsible for exercising the rights of data subjects and providing information about data processing.")
            
            Subtitle(value: "DEFINITIONS")
            Regular(value: "The following definitions are used throughout this Privacy Policy:")
            
            Subtitle(value: "JetBrains Website")
            Regular(value: "The website of JetBrains which can be found on [https://www.jetbrains.com/](https://www.jetbrains.com/)")

            Subtitle(value: "KotlinConf")
            Regular(value: "The official event by [JetBrains](https://www.jetbrains.com/) under the name KotlinConf. KotlinConf 2023 takes place in Amsterdam between the dates of April 12th and 14th of 2023.")

            Subtitle(value: "Personal Data")
            Regular(value: "Personal Data means any data relating to an identified or identifiable natural person. All other capitalized terms used in this Privacy Policy shall have the same meaning as defined in article 4 GDPR (such as personal data, controller, processor, data subject, and others), unless defined otherwise in this definition clause.")
            
            Subtitle(value: "Why We Collect Data and What We Collect".uppercased())
        }
    }
    
    var partTwo: some View {
        VStack(alignment: .leading, spacing: 0) {
            
            Regular(value: "We collect data for various reasons as reflected in [our general Privacy Policy](https://www.jetbrains.com/legal/docs/privacy/privacy/) which can be found on the JetBrains Website, but for this App specifically we collect data for only the following reasons: \na) To provide you with the basic functionalities of the App. Upon your (re-)installation of the App we generate and collect a random ID, which is required to provide you with the basic functionalities of this App, such as in any case the account management (used for the setup or management of a user’s account) and other processing connected with the use of this App. Please note that we do not collect, store or process any Personal Data, such as your Device ID, so we can’t trace the random ID back to any device or person. We use [Google Cloud Platform](https://cloud.google.com/terms/cloud-privacy-notice), as third party service provider, acting as a data processor, to assist us in hosting and providing the App to you or in our operations. The legal basis for this data processing is the performance of a contract between you and us. \n b) To improve the App and KotlinConf. We may collect anonymous data based on your use of the App and we may use feedback that you provide voluntarily through the App, in particular on the talks during KotlinConf, as applicable. We use such data to better understand the usage patterns of the App, the options it provides and the behavior, preferences and feedback of our KoltinConf audience. Collection of data based on your use of the App and feedback that you provide voluntarily through the App is done anonymously but if you decide to share any Personal Data in your feedback the legal basis for this data processing shall be your consent. Categories of data involved in data processing include: generated random ID, data about usage of the App and services, votes, comments, voluntarily provided feedback, and any data provided in survey responses, Where appropriate, we will prompt you to give us your consent to the collection and processing of your data as described above. This will in any case happen within the App upon your first use of it, in a clear and conspicuous manner. You can manage your Personal Data and opt-outs as described in the Transparency section below.")

            Subtitle(value: "Your Privacy Choices And Rights")
            Regular(value: "Your Privacy Choices. The privacy choices you may have about your Personal Data are determined by applicable law and are described below. Mobile Devices. We may send push notifications through the App. You may opt out from receiving these push notifications by changing the settings on your mobile device. Please note you must separately opt out in each browser and on each device.")

            Subtitle(value: "Children")
            Regular(value: "This App is not designed for and is not offered to children under the age of 16. If we discover that a person under the age of 16 has submitted information directly to us, we will endeavor to delete the information from our systems.")
            
            Subtitle(value: "Transparency")
            Regular(value: "To respect your privacy, before using your Personal Data we will inform you about the categories of Personal Data we collect and the purposes we use them for. We will also inform you about the data management options that you may have. For this purpose, we use this Privacy Policy and the [Terms of Use](https://kotlinconf.com/kotlinconf-2023-app-license.pdf) found in the App. In this case, the App does not collect, store or process any Personal Data. Should you wish to get more detailed information on this topic, please do not hesitate to contact us.")
            
            Subtitle(value: "Sharing")
        }
    }
    
    var partThree: some View {
        VStack(alignment: .leading, spacing: 0) {
            Regular(value: "Collected Personal Data are shared based on this Privacy Policy. Additionally, we share collected Personal Data within the JetBrains group of companies described above, which act as joint data controllers and process Personal Data for the purposes described above. We may share your Personal Data with [Google Cloud Platform](https://cloud.google.com/terms/cloud-privacy-notice) which third party hosts and helps us provide you with this App. We may also share your Personal Data with certain third parties if we are obliged to do so under applicable legislation (especially with tax authorities or with other government bodies exercising their statutory powers) or if such sharing is necessary to achieve the purposes defined above (especially with government bodies or with parties harmed as a result of violations of applicable laws). To adhere to the requirements of the California Consumer Privacy Act (CCPA), we hereby notify you that JetBrains will not a) retain, use, sell, or otherwise disclose any Personal Data for any purpose other than to provide the App; or b) retain, use, sell, or disclose such Personal Data outside of the direct relationship between you and JetBrains; or c) use Personal Data other than as described within this Privacy Policy.")
            Subtitle(value: "Security")
            Regular(value: "We take steps to ensure that your information is treated securely and in accordance with this Privacy Policy. To secure your Personal Data, this App is designed with security and privacy in mind. Unfortunately, no system is 100% secure, and we cannot ensure or warrant the security of any information you provide to us. To the fullest extent permitted by applicable law, we do not accept liability for unauthorized access, acquisition, use, disclosure, or loss of personal information. We encrypt your data in transfer and at rest where it is technically feasible. External web resources are protected by SSL encryption. We review the processes of Personal Data usage before implementing them. This is done to minimize data usage and to make sure that you as the data owner are informed about the processing. When the reason for data storage expires, we remove your Personal Data from our servers or anonymize it for further usage. By the nature of the activity within which your data is collected, in particular when using the App to provide feedback. it may appear in our internal datasets used for researching the results of the voting. Before using the datasets for research or other purposes you are not informed of, we remove or anonymize your Personal Data in the datasets. We are using [Google Cloud Platform](https://cloud.google.com/terms/cloud-privacy-notice) as a third party to host the App and to process your data in accordance with this Privacy Policy. While choosing the partners, we ascertain their compliance with legal regulations and security standards to make sure your data are stored in a secure location with appropriate security measures in place.")
            
            Subtitle(value: "Location of Your Information")
            Regular(value: "Any servers or services of the App that contain Personal Data are located within the EU. At times, JetBrains may process or transfer some of your Personal Data to our affiliate companies outside of the EU. Any such transfer will be made in accordance with the applicable laws on data protection and this Privacy Policy and will be based on a relevant adequacy decision of the European Commission, especially on standard data protection clauses.")
            
            Subtitle(value: "Third-Party Links")
            Regular(value: "The App may contain links to other websites/applications and other websites/applications may reference or link to our App. These third-party services are not controlled by us. We encourage our users to read the privacy policies of each website and application with which they interact. We do not endorse, screen, or approve, and are not responsible for, the privacy practices or content of such other websites or applications. Providing personal information to third-party websites or applications is at your own risk.")
            
            Subtitle(value: "Data Retention, Withdrawal of Approval, Access to Data and Your Rights")
            Regular(value: "As the App does not collect, store or process any Personal Data, there is no Personal Data that needs to be removed from our servers if you choose to cease using this App at any time. Generally, we retain your Personal Data as long as we need to in order to achieve the purpose for which it was collected. We may retain your information if it is required to comply with legal obligations and/or defense in case of violation of any JetBrains terms of use and/or Privacy Policies. We may also have copies of your information in application logs, weblogs, and/or backups made for security and support purposes or consent text accepted before the Personal Data collection. These backups will not be accessible as separately delineated information. We may store Personal Data as reflected above for as long as you are using the App and/or for as long as you have it installed on your device. Further, we may keep the data to protect ourselves from damage in case of litigation in accordance with the current legislation. Please note, however, that you must retain a copy of all data that you have placed on our servers in the case of any loss; further, if you cease using our software and/or services, we will not be responsible for the retention of any of your data.\n If applicable, you are responsible for the correctness of the Personal Data you provide to us. We expect you to check the Personal Data you provide to us and if any inconsistency takes place, update your Personal Data or report the inconsistency to JetBrains. \nAs permitted by applicable law, EU residents may request a copy of the information that we hold about them. As we do not collect, store or process any Personal Data with the App we do not hold any information about you. \n Moreover, as set out in locally applicable Personal Data protection law, you may have the right to: (i) request access to your Personal Data; (ii) request rectification of your Personal Data; (iii) request erasure of your Personal Data; (iv) request a restriction on the processing of your Personal Data; (v) request Personal Data portability; or (vi) object to the processing of your Personal Data. Please note that as we do not collect, store or process any Personal Data with the App, some of the rights can’t be exercised.\nRight of Access. You may have the right to obtain from us a confirmation as to whether or not Personal Data concerning you are being processed, and, where that is the case, to request access to your Personal Data. The information about Personal Data processing includes the purposes of the processing, the categories of Personal Data concerned, and the recipients or categories of recipients to whom your Personal Data have been or will be disclosed, etc. However, this is not an absolute right and the interests of other individuals may restrict your right of access. Further, you may have the right to obtain a copy of your Personal Data undergoing processing. For additional copies requested, we may charge a reasonable fee based on administrative costs.\nRight to Rectification. You may have the right to obtain from us the rectification of inaccurate Personal Data. Depending on the purposes of the processing, you may have the right to have incomplete Personal Data made complete, in particular by providing a supplementary statement.\n Right to Erasure (Right to Be Forgotten). Under certain circumstances, you may have the right to require us to delete your Personal Data. \n Right to a Restriction of Processing. Under certain circumstances, you may have the right to require us to restrict the processing of your Personal Data. In this case, the respective Personal Data will be marked and may only be processed by us for certain purposes. \n Right to Personal Data portability. Under certain circumstances, you may have the right to receive the Personal Data concerning you, which you have provided to us, in a structured, commonly used, and machine-readable format, and to transmit these Personal Data to another entity. \n Right to Object. Under certain circumstances, you may have the right to object, on grounds relating to their particular situation, at any time to the processing of your Personal Data by us and we can be required to no longer process your Personal Data. \n Where applicable, these rights can be exercised via the email address [privacy@jetbrains.com](privacy@jetbrains.com). \n You may also contact JetBrains to get up-to-date information about your Personal Data processing and any Personal Data recipients. \n You may lodge a complaint related to the processing of your personal data with the competent data protection supervisory authority, i.e. in the Netherlands the Authority Personal Data (Autoriteit Persoonsgegevens), with its visiting address at Hoge Nieuwstraat 8, 2514 EL Den Haag, phone number: +31 (0)70 8888 500. For more information, please visit [https://autoriteitpersoonsgegevens.nl](https://autoriteitpersoonsgegevens.nl).")
        }
    }
    
    var partFour: some View {
        VStack(alignment: .leading, spacing: 0) {

            Subtitle(value: "Changes to this Policy")
            Regular(value: "This Privacy Policy is current as of the Effective Date set forth above. We may change this Privacy Policy from time to time, so please be sure to check back periodically. We will post any changes to this Privacy Policy in the App. If we make any changes to this Privacy Policy that materially affect our practices with regard to the Personal Data we have previously collected from you, we will endeavor to provide you with an advance notice of such change by highlighting the change upon your subsequent use of the App.")
            
            Subtitle(value: "Reaching out")
            Regular(value: "If you have any questions or concerns, or you feel that this Privacy Policy has been violated in any way, please let us know immediately by contacting [privacy@jetbrains.com](privacy@jetbrains.com).")
        }
    }
}

func Subtitle(value: String) -> some View {
    Text(value.uppercased())
        .font(DesignSystem.kcT2.bold())
        .padding(.leading, 16)
        .padding(.trailing, 16)
        .padding(.top, 24)
        .padding(.bottom, 16)
        .foregroundColor(DesignSystem.blackWhiteColor)
}

func Regular(value: String) -> some View {
    Text(.init(value))
        .font(DesignSystem.kcT2)
        .padding(.leading, 16)
        .padding(.trailing, 16)
        .foregroundColor(DesignSystem.greyGrey20Color)
}

struct PrivacyPolicyFull_Previews: PreviewProvider {
    static var previews: some View {
        ScrollView {
            PrivacyPolicyFull()
        }
    }
}


struct Welcome_Previews: PreviewProvider {
    static var previews: some View {
        Welcome()
    }
}

private let PRIVACY_SHORT = "The app provides the opportunity for you to provide feedback about talks. We collect such input anonymously. In case you decide to post any personal data in your feedback, we shall process your data in accordance with the Privacy Policy"

