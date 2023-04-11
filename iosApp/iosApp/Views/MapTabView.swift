//
//  MapView.swift
//  iosApp
//
//  Created by Leonid.Stashevsky on 07.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import MapboxMaps


let MAPS = [
    "mapbox://styles/grigza/cldbumgwj000401ox12d8ufs5",
    "mapbox://styles/grigza/cl9yddkcm006414rh0a39ijh4",
    "mapbox://styles/grigza/cldbra01x004q01p5ok5zpkd6"
]

let MAPS_LIGHT = [
    "mapbox://styles/grigza/clfbf5irz001601o6unu59cwt",
    "mapbox://styles/grigza/clfbezi94001501o6al2kguwx",
    "mapbox://styles/grigza/clfbej9kp001z01ln5h8philh"
]


public let MAP_LOCATION: [String : String] = [
    "Effectenbeurszaal" : MAPS[1],
    "Graanbeurszaal": MAPS[1],
    "Administratiezaal": MAPS[2],
    "Veilingzaal": MAPS[2],
    "Berlage zaal": MAPS[2],
    "Verwey kamer": MAPS[2],
    "Mendes da Costa kamer" : MAPS[1],
]


public let MAP_LOCATION_LIGHT: [String : String] = [
    "Effectenbeurszaal" : MAPS_LIGHT[1],
    "Graanbeurszaal": MAPS_LIGHT[1],
    "Administratiezaal": MAPS_LIGHT[2],
    "Veilingzaal": MAPS_LIGHT[2],
    "Berlage zaal": MAPS_LIGHT[2],
    "Verwey kamer": MAPS_LIGHT[2],
    "Mendes da Costa kamer" : MAPS_LIGHT[1],
]

public let ROOM_LOCATION: [String : CameraOptions] = [
    "Effectenbeurszaal" : GROUD_ROOMS,
    "Graanbeurszaal": GROUD_ROOMS,
    "Administratiezaal": FLOOR1_ROOMS,
    "Veilingzaal": FLOOR1_ROOMS,
    "Berlage zaal": BERLAGE_ROOM,
    "Verwey kamer": VERWEY_KAMER,
    "Mendes da Costa kamer": GROUD_ROOMS
]

struct MapTabView: View {
    
    @State var selectedIndex = 1
    
    let floors = [
        "Floor -1",
        "Floor 0",
        "Floor 1"
    ]
    
    @Environment(\.colorScheme) var colorScheme: ColorScheme
    var map: String {
        colorScheme == .dark ? MAPS[selectedIndex] : MAPS_LIGHT[selectedIndex]
    }
    
    @State var sheetPresented: Bool = false
    @State var sheetText = ""
    @State var sheetDescription = ""
    @State var sheetDisplayName = ""
    
    var body: some View {
        Navigation {
            MapSwiftUIWrapper(url: map) { label, description, displayName in
                sheetPresented = true
                sheetText = label
                sheetDescription = description
                sheetDisplayName = displayName
            }
        }
        .sheet(isPresented: $sheetPresented) {
            InfoSheet(sheetPresented: $sheetPresented, title: $sheetText, description: $sheetDescription, displayName: $sheetDisplayName)
        }
    }
    
    @State var isShowingMenu = false
    
    func Navigation(@ViewBuilder _ content: () -> some View) -> some View {
        VStack(spacing: 0) {
            content()
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                HStack(spacing: 0) {
                    buttons
                }
            }
        }
        
    }
    
    var buttons: some View {
        ForEach(Array(floors.enumerated()), id: \.offset) { item in
            floorButton(index: item.offset, isSelected: selectedIndex == item.offset)
        }
    }
    
    func floorButton(index: Int, isSelected: Bool) -> some View {
        var fillColor: Color {
            return isSelected ? DesignSystem.greyWhiteColor : DesignSystem.whiteGreyColor
        }
        var textColor: Color {
            return isSelected ? DesignSystem.whiteGreyColor : DesignSystem.grey50Color
        }
        
        var font: Font {
            return isSelected ? DesignSystem.kcH4 : DesignSystem.kcT2
        }

        return Rectangle()
            .fill(fillColor)
            .frame(width: 103, height: 28)
            .cornerRadius(4)
            .overlay(
                Text(floors[index].uppercased())
                    .foregroundColor(textColor)
                    .font(font)
            ).onTapGesture {
                selectedIndex = index
            }.animation(.default, value: isSelected)
    }
    
}

struct InfoSheet : View {
    @EnvironmentObject var conference: ConferenceModel
    @Binding var sheetPresented: Bool
    @Binding var title: String
    @Binding var description: String
    @Binding var displayName: String
    
    var name: String {
        switch title {
        case "Android":
            return "Android & Google"
        default:
            return title.lowercased()
        }
    }
    
    @State var selectedDetent: PresentationDetent = .height(300)
    var body: some View {
        ScrollView {
            VStack(alignment: .leading) {
                VStack(alignment: .leading, spacing: 16) {
                    HStack {
                        Spacer()
                        Button(action: { sheetPresented = false }) {
                            HStack {
                                Image(DesignSystem.closeIcon)
                                    .aspectRatio(contentMode: .fit)
                                    .foregroundColor(DesignSystem.greyGrey5Color)
                            }
                        }
                        
                    }
                    Text(displayName.uppercased())
                        .font(DesignSystem.kcH2)
                        .foregroundColor(DesignSystem.greyGrey5Color)
                    
                }
                .padding(.top, 20)
                .padding(.leading, 16)
                .padding(.trailing, 16)
                
                Image("\(name)-big")
                    .resizable()
                    .scaledToFit()
                    .padding(.leading, 16)
                    .padding(.trailing, 16)
                
                Text(description)
                    .font(DesignSystem.kcT2)
                    .foregroundColor(DesignSystem.greyGrey20Color)
                    .padding(.top, 24)
                    .padding(.leading, 16)
                    .padding(.trailing, 16)
            }
        }
        .presentationDetents(undimmed: [.height(300), .large], selection: $selectedDetent)
    }
}

struct MapView_Previews: PreviewProvider {
    static var previews: some View {
        MapTabView()
    }
}
