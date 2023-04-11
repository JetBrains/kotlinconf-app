//
//  SpeakerCardSmall.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 08.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct SpeakerCardSmall: View {
    var name: String
    var title: String
    var photoUrl: String
    
    var body: some View {
        VStack(spacing: 0) {
            HStack(alignment: .center, spacing: 0) {
                CachedAsyncImage(
                    url: URL(string: photoUrl),
                    content: { image in
                        image
                            .resizable()
                            .frame(width: 84, height: 84)
                            .scaledToFit()
                    },
                    placeholder: {
                        ProgressView()
                    }
                )
                .scaledToFit()
                .frame(width: 84, height: 84)

                VStack(alignment: .leading, spacing: 0) {
                    Text(name)
                        .font(DesignSystem.kcH4)
                        .foregroundColor(DesignSystem.greyGrey5Color)
                        
                    Text(title)
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.greyGrey20Color)
                        .lineLimit(1)
                        .truncationMode(.tail)
                        .padding(.top, 4)
                }
                .padding(.leading, 16)
                .padding(.trailing, 16)
                Spacer()
            }
            Divider()
        }
    }
}

struct SpeakerCardSmall_Previews: PreviewProvider {
    static var previews: some View {
        ScrollView {
            VStack(spacing: 0) {
                SpeakerCardSmall(name: "Brooklyn Simmons", title: "Senior researcher at INR RAS and MIPT", photoUrl: "https://sessionize.com/image/92c9-400o400o2-c1-9707-4c86-bc82-70b7ce05823c.def0b003-b9b2-420e-88f9-0806049698cd.jpg")
                SpeakerCardSmall(name: "Robert Fox", title: "Marketing Coordinator", photoUrl: "https://sessionize.com/image/92c9-400o400o2-c1-9707-4c86-bc82-70b7ce05823c.def0b003-b9b2-420e-88f9-0806049698cd.jpg")
            }
        }
    }
}
