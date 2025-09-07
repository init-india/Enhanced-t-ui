WiFi CLI Commands & Flow

✅ Available Commands

wifi – Show current WiFi status (on/off).

wifi list – Display available WiFi networks with signal strength and security type.

wifi connect <SSID> – Connect to a specific WiFi network by SSID.

wifi disconnect – Disconnect from the current WiFi network.

wifi status – Show detailed information of the current WiFi connection.

wifi off – Turn off WiFi.

wifi on – Turn on WiFi.



---

⚡ Example User Flow: Connecting to WiFi

1. Check WiFi status

wifi

Output:
WiFi is ON. Connected to: None.


2. List available networks

wifi list

Output:

1. Home_Network (WPA2) – Strong  
2. Office_WiFi (WPA3) – Medium  
3. Guest (Open) – Weak  
4. Cafe_Free (Open) – Medium


3. Connect to a network with a password

wifi connect Home_Network

System prompts:
Enter password:
User types: ******** → Enter

Output:
Connected to Home_Network successfully.


4. View current status

wifi status

Output:

SSID: Home_Network  
Signal: Strong  
Security: WPA2  
IP Address: 192.168.1.45




---

🚫 Edge Cases

If wrong password:
Output:
Error: Authentication failed. Please retry.

If network unavailable:
Output:
Error: Network 'SSID_NAME' not found.

If WiFi is off and user tries to connect:
Output:
WiFi is currently OFF. Please turn it ON using 'wifi on'.



---

✅ Additional Enhancements

Biometric authentication prompt before connecting to a new WiFi network.

Dynamic suggestions while typing network SSID (auto-complete).

wifi forget <SSID> – To remove stored network profiles.

Logging of connected WiFi history (with timestamps).
