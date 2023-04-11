//
//  Party.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 06.03.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct Party: View {
    var isFinished: Bool
    var body: some View {
        Rectangle()
            .frame(height: 150)
            .opacity(0)
            .overlay {
                GeometryReader { reader in
                Image(DesignSystem.partyLogo)
                    .opacity(isFinished ? 0.5 : 1.0)
                    .frame(height: 150)
                }
            }
    }
}

struct Party_Previews: PreviewProvider {
    static var previews: some View {
        Party(isFinished: false)
    }
}
