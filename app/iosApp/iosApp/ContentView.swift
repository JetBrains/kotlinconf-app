import SwiftUI
import shared

// MARK: - Legacy ComposeView (non-native navigation, all iOS versions)

struct ComposeView: UIViewControllerRepresentable {
    let topLevelRoute: TopLevelRoute

    func makeUIViewController(context: Context) -> UIViewController {
        return Main_iosKt.MainViewController(topLevelRoute: topLevelRoute)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

// MARK: - Native navigation types (iOS 26+)

@available(iOS 26.0, *)
struct RouteWrapper: Hashable, Identifiable {
    let id = UUID()
    let route: AppRoute

    static func == (lhs: RouteWrapper, rhs: RouteWrapper) -> Bool {
        lhs.id == rhs.id
    }

    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
}

@available(iOS 26.0, *)
@Observable
class TabNavigationCoordinator {
    var path: [RouteWrapper] = []

    func push(_ route: AppRoute) {
        path.append(RouteWrapper(route: route))
    }

    func pop() {
        if !path.isEmpty {
            path.removeLast()
        }
    }

    func popToRoot() {
        path.removeAll()
    }
}

@available(iOS 26.0, *)
@Observable
class AppNavigationCoordinator {
    var selectedTabIndex: Int = 1
    let tabCoordinators: [Int: TabNavigationCoordinator]

    init() {
        tabCoordinators = [
            1: TabNavigationCoordinator(), // Schedule
            2: TabNavigationCoordinator(), // Speakers
            5: TabNavigationCoordinator(), // Golden Kodee
            3: TabNavigationCoordinator(), // Map
            4: TabNavigationCoordinator(), // Info
        ]
    }

    var currentTabCoordinator: TabNavigationCoordinator {
        tabCoordinators[selectedTabIndex]!
    }

    func activateTab(for route: TopLevelRoute) {
        if route is ScheduleScreen { selectedTabIndex = 1 }
        else if route is SpeakersScreen { selectedTabIndex = 2 }
        else if route is GoldenKodeeScreen { selectedTabIndex = 5 }
        else if route is MapScreen { selectedTabIndex = 3 }
        else if route is InfoScreen { selectedTabIndex = 4 }
    }
}

@available(iOS 26.0, *)
struct NativeNavComposeView: UIViewControllerRepresentable {
    let topLevelRoute: TopLevelRoute
    let coordinator: TabNavigationCoordinator
    let appCoordinator: AppNavigationCoordinator

    func makeUIViewController(context: Context) -> UIViewController {
        return Main_iosKt.MainViewController(
            topLevelRoute: topLevelRoute,
            onNavigate: { route in
                self.coordinator.push(route)
            },
            onGoBack: {
                self.coordinator.pop()
            },
            onSet: { route in
                self.coordinator.popToRoot()
                if let topLevel = route as? TopLevelRoute {
                    self.appCoordinator.activateTab(for: topLevel)
                } else {
                    self.coordinator.push(route)
                }
            },
            onActivate: { route in
                self.appCoordinator.activateTab(for: route)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@available(iOS 26.0, *)
struct DetailComposeView: UIViewControllerRepresentable {
    let route: AppRoute
    let coordinator: TabNavigationCoordinator
    let appCoordinator: AppNavigationCoordinator

    func makeUIViewController(context: Context) -> UIViewController {
        return Main_iosKt.ScreenViewController(
            route: route,
            onNavigate: { newRoute in
                self.coordinator.push(newRoute)
            },
            onGoBack: {
                self.coordinator.pop()
            },
            onSet: { route in
                self.coordinator.popToRoot()
                if let topLevel = route as? TopLevelRoute {
                    self.appCoordinator.activateTab(for: topLevel)
                } else {
                    self.coordinator.push(route)
                }
            },
            onActivate: { route in
                self.appCoordinator.activateTab(for: route)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@available(iOS 26.0, *)
struct TabContentView: View {
    let topLevelRoute: TopLevelRoute
    let coordinator: TabNavigationCoordinator
    let appCoordinator: AppNavigationCoordinator

    var body: some View {
        NavigationStack(path: Binding(
            get: { coordinator.path },
            set: { coordinator.path = $0 }
        )) {
            NativeNavComposeView(
                topLevelRoute: topLevelRoute,
                coordinator: coordinator,
                appCoordinator: appCoordinator
            )
            .ignoresSafeArea(.all)
            .navigationBarHidden(true)
            .navigationDestination(for: RouteWrapper.self) { wrapper in
                DetailComposeView(
                    route: wrapper.route,
                    coordinator: coordinator,
                    appCoordinator: appCoordinator
                )
                .ignoresSafeArea(.all)
                .navigationTitle("")
                .toolbarTitleDisplayMode(.inline)
            }
        }
    }
}

@available(iOS 26.0, *)
struct NativeNavContentView: View {
    @State private var appCoordinator = AppNavigationCoordinator()

    var body: some View {
        TabView(selection: $appCoordinator.selectedTabIndex) {
            Tab("Schedule", systemImage: "clock", value: 1) {
                TabContentView(
                    topLevelRoute: ScheduleScreen(),
                    coordinator: appCoordinator.tabCoordinators[1]!,
                    appCoordinator: appCoordinator
                )
            }
            Tab("Speakers", systemImage: "person.2", value: 2) {
                TabContentView(
                    topLevelRoute: SpeakersScreen(),
                    coordinator: appCoordinator.tabCoordinators[2]!,
                    appCoordinator: appCoordinator
                )
            }
            Tab("Golden Kodee", systemImage: "trophy", value: 5) {
                TabContentView(
                    topLevelRoute: GoldenKodeeScreen(),
                    coordinator: appCoordinator.tabCoordinators[5]!,
                    appCoordinator: appCoordinator
                )
            }
            Tab("Map", systemImage: "mappin.and.ellipse", value: 3) {
                TabContentView(
                    topLevelRoute: MapScreen(),
                    coordinator: appCoordinator.tabCoordinators[3]!,
                    appCoordinator: appCoordinator
                )
            }
            Tab("Info", systemImage: "info.circle", value: 4) {
                TabContentView(
                    topLevelRoute: InfoScreen(),
                    coordinator: appCoordinator.tabCoordinators[4]!,
                    appCoordinator: appCoordinator
                )
            }
        }
        .tabBarMinimizeBehavior(.automatic)
    }
}

// MARK: - ContentView

struct ContentView: View {
    var body: some View {
        if #available(iOS 26.0, *) {
            NativeNavContentView()
        } else {
            ComposeView(topLevelRoute: ScheduleScreen())
                .ignoresSafeArea(.all)
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
