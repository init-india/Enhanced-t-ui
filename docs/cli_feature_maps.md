✅ Final CLI Flow — Maps


---

▶️ 1. Show Current Location & History

map +enter

➔ Behavior:

Displays:

Current location (address, coordinates).

Last 10 locations searched (with timestamp).


If Location Service is OFF → Prompt:
➔ "Location service is OFF. Enable it? (yes/no)"

If yes → Enable location.

If no → Exit command.




---

▶️ 2. Preview Route from Current Location to Destination

map <destination>

➔ Behavior:

Realtime suggestions while typing destination name.

Displays route preview:

Distance

ETA

Traffic status

Toll information

Fuel stations / Restaurants / Hospitals along the route.


If Location Service is OFF → Prompt to enable.

If no Internet or Location OFF → Use offline maps if available.

Asks:
➔ "Do you want to start navigation? (yes/no)"

If yes → Internally converts to mapnav <current-location> <destination>.

If no → Stay in preview mode.




---

▶️ 3. Preview Route from Any Source to Any Destination

maproute <from-location> <to-location>

➔ Behavior:

Realtime suggestions while typing both locations.

Displays route preview:

Distance

ETA

Traffic status

Toll information

Fuel stations / Restaurants / Hospitals.


If Location Service is OFF → Prompt to enable.

If no Internet or Location OFF → Use offline maps if available.

Asks:
➔ "Do you want to start navigation? (yes/no)"

If yes → Internally converts to mapnav <from-location> <to-location>.

If no → Stay in preview mode.




---

▶️ 4. Start Realtime Navigation

mapnav <from-location> <to-location>

➔ Behavior:

Starts realtime voice + CLI step-by-step navigation.

Displays live updates:

Next turn

Distance to next turn

ETA

Tolls on route

Nearby Fuel Stations / Restaurants / Hospitals.


Ends automatically when destination is reached.

Offline navigation supported if no internet.



---

▶️ 5. Share Current Location

mapshare <contact-number>

➔ Behavior:

Asks:
➔ Select sharing method:

SMS

WhatsApp

Email


Requires biometric authentication.

Sends location as link or coordinates.



---

▶️ 6. Info Commands During Preview / Navigation

maptraffic

➔ Displays live traffic status on route.

mapalt

➔ Shows top 3 alternative routes.

mapsteps

➔ Displays detailed step-by-step directions.


---

⚡ Security & Constraints

Location Service must be ON; prompts to enable if OFF.

Offline maps fallback used when no internet.

Biometric authentication mandatory for sharing location.

Navigation ends automatically upon reaching destination.

No mapresume; navigation is stateless.
