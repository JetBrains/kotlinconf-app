//
//  NavigationRoot.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 09.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI


struct NavigationRoot<Content>: View where Content : View {
    var title: String
    var content: Content
    
    init(title: String, @ViewBuilder _ content: () -> Content) {
        self.title = title
        self.content = content()
    }
    
    var body: some View {
        VStack(spacing: 0) {
            content
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                HStack(spacing: 0) {
                    Text(title)
                        .font(DesignSystem.kcT2)
                        .foregroundColor(DesignSystem.grey50Color)
                }
            }
        }
    }
}

struct NavigationRoot_Previews: PreviewProvider {
    static var previews: some View {
        NavigationRoot(title: "Navigation") {
            Text("Hello, Navigation!")
        }
    }
}
