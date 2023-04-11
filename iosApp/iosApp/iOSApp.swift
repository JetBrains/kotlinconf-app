import SwiftUI

@main
struct iOSApp: App {
    @StateObject private var modelData = ConferenceModel()
    
    init() {
        URLCache.shared.memoryCapacity = 100 * 1024 * 1024
        URLCache.shared.diskCapacity = 300 * 1024 * 1024
        
        let coloredAppearance = UINavigationBarAppearance()
        coloredAppearance.configureWithOpaqueBackground()
        coloredAppearance.backgroundColor = UIColor(DesignSystem.whiteGreyColor)
        
        UINavigationBar.appearance().standardAppearance = coloredAppearance
        UINavigationBar.appearance().compactAppearance = coloredAppearance
        UINavigationBar.appearance().scrollEdgeAppearance = coloredAppearance
        
        UINavigationBar.appearance().tintColor = .white
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
                .environmentObject(modelData)
		}
	}
}
