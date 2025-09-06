Map CLI Flow – Refined with POIs


---

1. Current Location

Command:

map + enter

Behavior:

Shows current location with timestamp.

Lists last 10 searched/saved destinations.


Example:

[03 Sept 2025 | 22:46] Current Location: MG Road, Bengaluru
Recent Locations:
1. Electronic City
2. Airport Terminal 2
3. Whitefield
4. HSR Layout
5. Hebbal
...


---

2. Quick Destination (Default: Current Location → Destination)

Command:

map <destination>

Behavior:

From = Current Location

To = Destination typed

Shows route preview (same format as maproute).

Asks if user wants to start navigation (yes/no).


Example:

map Electronic City
Route Preview: MG Road → Electronic City
Distance: 12.3 km | ETA: 28 mins
Traffic: 🚨 Heavy congestion on Hosur Rd
Tolls: 2 (₹60 approx.)
Via: Hosur Road
Alternatives:
1. NICE Road → 14.8 km | 30 mins | Toll ₹80
2. Residency Rd → 13.5 km | 35 mins | No toll
Nearby POIs:
- Fuel: Shell Fuel Station (3.5 km)
- Restaurant: Barbeque Nation (2.1 km)
- Hospital: Apollo Clinic (4.2 km)

Do you want to navigate? (yes/no)


---

3. Custom Route (Any Source → Destination)

Command:

maproute <from> <to>

Behavior:

Previews route info for any source → destination.

Shows distance, ETA, traffic, tolls, alternatives.

Adds nearby POIs (fuel stations, restaurants, hospitals).

Asks user if navigation should start.


Example:

maproute Whitefield Airport T2
Route Preview: Whitefield → Airport Terminal 2
Distance: 41.6 km | ETA: 1 hr 12 mins
Traffic: ⚠ Moderate congestion
Tolls: 3 (₹180 approx.)
Via: ORR + Airport Rd
Alternatives:
1. KR Puram → 45.0 km | 1 hr 20 mins | No toll
2. Hebbal → 40.5 km | 1 hr 18 mins | Toll ₹200
Nearby POIs:
- Fuel: Indian Oil (2.8 km from route)
- Restaurant: McDonalds (Airport Rd, 5.3 km)
- Hospital: Columbia Asia (Hebbal, 7.2 km)

Do you want to navigate? (yes/no)


---

4. Start Navigation

Command:

mapnav <from> <to>

Behavior:

Starts live navigation.

CLI shows:

Next turn instruction

ETA & distance left

Toll info


Voice guidance auto-starts.


Example:

Navigation Started: MG Road → Electronic City
Next: In 500m, turn right onto Brigade Rd
ETA: 28 mins | Distance left: 11.8 km | Tolls: 2


---

5. Optional Commands (During Preview/Navigation)

Show traffic details:


maptraffic

Show alternative routes:


mapalt

Show step-by-step directions:


mapsteps


---

6. Share Location

Command:

mapshare <contact/number>

Behavior:

Biometric authentication required.

Choose method:
1️⃣ SMS
2️⃣ WhatsApp
3️⃣ Email

