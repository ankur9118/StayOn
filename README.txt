StayOn - Be Always online

StayOn is an Android utility app designed to maintain continuous internet connectivity by automatically switching between available networks.

The app monitors network conditions in real time and performs automatic WiFi failover to prevent internet interruptions.

Target platform: Android phones and Android TV (no root required).

Project Concept

StayOn is a network failover and connectivity continuity app.

StayOn automatically switches to the best available saved WiFi network to maintain connectivity.

When the current WiFi disconnects, the app automatically connects to the next best available saved WiFi network.

Mobile data is used only if no WiFi network is available.

This fully automatic behavior is called AUTOPILOT MODE.

In the future, a Pro mode will allow users to define preferred WiFi networks.

If a preferred WiFi becomes available, StayOn will automatically switch to it.

Why StayOn

Android does not automatically switch between WiFi networks in the background.

Users must manually select another network when connectivity drops.

StayOn solves this problem by providing automatic network failover.

Technical Approach

StayOn uses:

Network monitoring

WiFi scanning

Signal strength evaluation

Smart decision logic

Local VPN routing (future)

A local VPN layer will allow:

seamless switching

minimized packet loss

background network control

Current Development Status

Current stage: Core network monitoring completed, failover logic in development.

Implemented features:

Android Studio project created

Git + GitHub setup completed

Jetpack Compose UI created

StayOn ON/OFF toggle implemented

NetworkMonitor implemented

Detects WiFi, mobile data, and network loss

Monitoring can be started and stopped from the UI

Single NetworkMonitor instance maintained using remember{}

Current WiFi detection

Nearby WiFi scanning

Signal strength (RSSI) evaluation

Best network selection

Initial decision engine implemented

Current Capability

The app can currently:

monitor network changes

detect connectivity status

scan nearby WiFi networks

evaluate signal strength

identify the best available network

Next Development Goal

Implement automatic WiFi switching logic:

WiFi connection lost
↓
scan available saved WiFi networks
↓
select best network based on signal strength
↓
connect to best available saved WiFi
↓
fallback to mobile data if no WiFi exists
Technical Constraints

Device cannot be rooted

App must remain Google Play Store compliant

Android restricts automatic WiFi switching

Because of these restrictions, StayOn will eventually use a local VPN-based routing approach.

Architecture Discussion

Please read the architecture discussion here:

https://github.com/ankur9118/StayOn/tree/main/StayOn%20Architecture

Source Code

Latest code available here:

https://github.com/ankur9118/StayOn

Development Status

StayOn is currently in active development.