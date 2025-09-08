CLI System Settings Technical Design

1. Hotspot

Command: hotspot + enter

Flow:

Displays Hotspot Status: On or Off.


Case: Hotspot is Off

Hotspot Status: Off
1. Turn On Hotspot
2. Exit
Enter option: 1
→ Hotspot turned On.

Case: Hotspot is On

Hotspot Status: On
Connected Devices:
1. Device A
2. Device B
3. Device C

Options:
1. Turn Off Hotspot
2. Exit
Enter option: 1
→ Hotspot turned Off.


---

2. WiFi

Command: wifi + enter

Flow:

Displays WiFi Status: On or Off.


Case: WiFi is Off

WiFi Status: Off
1. Turn On WiFi
2. Exit
Enter option: 1
→ WiFi turned On.

Case: WiFi is On

WiFi Status: On
Available Networks:
1. Network_A (Secured)
2. Network_B (Open)
3. Network_C (Secured)

Options:
Select network number to connect or 0 to Exit: 1
→ Enter password: ********
→ Connected to Network_A.

1. Turn Off WiFi
2. Exit
Enter option: 1
→ WiFi turned Off.


---

3. Bluetooth

Command: bluetooth + enter

Flow:

Displays Bluetooth Status: On or Off.


Case: Bluetooth is Off

Bluetooth Status: Off
1. Turn On Bluetooth
2. Exit
Enter option: 1
→ Bluetooth turned On.

Case: Bluetooth is On

Bluetooth Status: On
Available Devices:
1. Device_X
2. Device_Y
3. Device_Z

Options:
Select device number to pair or 0 to Exit: 1
→ Enter password: ********
→ Paired with Device_X.

1. Turn Off Bluetooth
2. Exit
Enter option: 1
→ Bluetooth turned Off.


---

4. Location

Command: location + enter

Flow:

Current Location Status: On
1. Turn Off Location
2. Exit
Enter option: 1
→ Location turned Off.

Or, if Location was Off:

Current Location Status: Off
1. Turn On Location
2. Exit
Enter option: 1
→ Location turned On.


---

5. Brightness

Command: brightness + enter

Flow:

Set Brightness:
1. Adaptive
2. Enter Percentage
3. Exit
Enter option: 2
→ Enter brightness percentage (0-100): 70
→ Brightness set to 70%.


---

6. Volume

Command: volume + enter

Flow:

Set Volume:
1. Notification Volume
2. Alarm Volume
3. In-Call Volume
4. Media Volume
5. Ring Volume
6. SMS Volume
7. Exit

Enter option (1-7): 1
→ Enter volume percentage (0-100): 80
→ Notification Volume set to 80%.


---

7. Power Off

Command: poweroff + enter

Flow:

Are you sure you want to power off? (yes/no): yes
→ System powering off...


---

8. Restart

Command: restart + enter

Flow:

Are you sure you want to restart? (yes/no): yes
→ System restarting...


---

Exit System Settings

Command: exit + enter

Flow:

Returning to main CLI.
