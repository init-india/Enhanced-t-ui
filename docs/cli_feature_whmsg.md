WhatsApp CLI Final Flow


---

✅ 1️⃣ Incoming Notification Alert

When new WhatsApp messages arrive (single or multiple):

🔔 You have 7 new WhatsApp messages:
1. [5 Sept 2025: 15:10] Alice S: "Don't forget the meeting tomorrow..."
2. [5 Sept 2025: 14:55] Bob J: "Are you free to talk later?"
3. [5 Sept 2025: 14:50] Charlie D: "Sent the files, check your email."
...
7. [5 Sept 2025: 13:05] Zoe P: "Where are we meeting today?"

Type 'wh + enter' to view conversations


---

✅ 2️⃣ Command: wh + enter

Displays first 20 chat conversations:

🟢 WhatsApp Conversations (1-20 of 78):
[1] [5 Sept 2025: 15:10] Alice S: "Don't forget the meeting tomorrow..." [Unread]
[2] [5 Sept 2025: 14:55] Bob J: "Are you free to talk later?" [Replied]
[3] [5 Sept 2025: 14:50] Charlie D: "Sent the files, check your email." [Unread]
...
[20] [4 Sept 2025: 16:20] Zoe P: "See you soon." [Read]

Suggestions: type down+enter for next, up+enter for previous, exit+enter to return to CLI, [number]+enter to open thread


---

✅ 3️⃣ Scroll Example

User types: down + enter


🟢 WhatsApp Conversations (21-40 of 78):
[21] [4 Sept 2025: 15:10] Mark T: "Let's catch up later." [Unread]
...
[40] [3 Sept 2025: 12:50] Sam W: "Happy Birthday!" [Read]

Suggestions: down+enter for next, up+enter for previous, exit+enter to return, [number]+enter to open thread


---

✅ 4️⃣ Command: wh Alice (or wh + 1)

Shows full thread with that contact:

📄 WhatsApp Chat with Alice S:
[5 Sept 2025: 15:10] Alice S ➔ You: "Don't forget the meeting tomorrow..." [Unread]
[5 Sept 2025: 15:12] You ➔ Alice S: "Sure, noted!" [Sent]
[4 Sept 2025: 10:05] Alice S ➔ You: "How’s the project?" [Read]

Suggestions: reply+enter to reply, exit+enter to return to conversation list


---

✅ 5️⃣ Reply Flow

User types reply + enter


✏️ Reply to Alice S:

Message (editable): [Your reply goes here...]

Type message and hit enter to send  
Type exit+enter to cancel

After typing the reply and hitting enter:

✅ Message sent to Alice S at 5 Sept 2025: 15:25


---

✅ 6️⃣ New Message Flow

User types: whnew 9876543210 or whnew John


📩 New WhatsApp Message to John (9876543210):

Message (editable): [Your message goes here...]

Type message and hit enter to send  
Type exit+enter to cancel

After message sent:

✅ New message sent to John at 5 Sept 2025: 15:30


---

✅ 7️⃣ Share File / Location / Contact

From inside message reply:

📎 Type:
[file path] + enter → To send a file  
location + enter → To send current location  
contact + enter → To send a contact card

Or type exit+enter to cancel


---

✅ 8️⃣ Biometric Authentication

Every time user wants to read a message thread or open a new message:


🔐 Please authenticate to continue reading WhatsApp messages
[biometric scan prompt]


---

✅ 9️⃣ Exit Logic

Anywhere in flow, typing exit + enter returns to main CLI.
