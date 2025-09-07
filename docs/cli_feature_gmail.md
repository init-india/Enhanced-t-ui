▶️ 1. List Emails

Command: gmail +enter

➔ Behavior:

Displays the first 20 emails from Primary category:

[1] [Unread] [12 Sept 2025 15:34] John Doe | Meeting Tomorrow
[2] [Read] [12 Sept 2025 14:10] Amazon | Your Order Update
[3] [Replied] [11 Sept 2025 10:45] Jane Smith | Project Status
...

Supports scrolling:

down → Next 20 emails

up → Previous 20 emails




---

▶️ 2. Dynamic Search

Command: gmail +<search-term>

➔ Behavior:

Dynamically updates and displays search results as user types.

Matching results:
[1] [Unread] [12 Sept 2025 15:34] John Doe | Meeting Tomorrow
[2] [Read] [10 Sept 2025 09:20] Marketing | Newsletter Update
...



---

▶️ 3. List Emails by Contact

Command: gmail +john@example.com +enter

➔ Behavior:

Shows all emails from that contact:

[1] [Unread] [12 Sept 2025 15:34] John Doe | Meeting Tomorrow
[2] [Read] [11 Sept 2025 10:45] John Doe | Follow-up
...

Supports scrolling via up/down.



---

▶️ 4. Open and Read an Email

Command: gmail open <index> (e.g., gmail open 1)

➔ Displays full email:

From: John Doe <john@example.com>
To: me@example.com
Subject: Meeting Tomorrow
Date: 12 Sept 2025 15:34

[Body content here...]

[Options]: reply | replyall | delete | save_draft | exit


---

▶️ 5. Reply or Reply All Flow

Command: reply or replyall


➔ Behavior:

Enters editable mode:

To: john@example.com
CC: 
Subject: Meeting Tomorrow
Body:
[Editable]

Options: send | save_draft | cancel



---

▶️ 6. Compose New Email

Command: gmail compose

➔ Behavior:

CLI prompts for:

To: <type>
CC: <type>
Subject: <type>
Body:
[Editable]

Options: send | save_draft | cancel



---

▶️ 7. Drafts Management

Command: gdraft +enter

➔ Displays:

[1] [Draft] [10 Sept 2025 12:30] Jane Smith | Follow-up
[2] [Draft] [11 Sept 2025 09:15] Amazon | Order Question
...

Open Draft: gdraft 1 → Opens editable draft view.


➔ Editable Mode:

To: jane@example.com
CC: 
Subject: Follow-up
Body:
[Editable]

Options: send | save_draft | cancel


---

▶️ 8. Delete Emails

Command: gdel +<search-term> or gdel +email@example.com

➔ Behavior:

Displays matched emails with indices.

User selects deletion mode:

all → Delete all displayed emails.

range → Enter ranges: e.g., 1-3,5,7-9.


Confirmation prompt before deletion.



---

▶️ 9. Exit Gmail CLI

Command: exit

➔ Behavior:

Returns to normal CLI.



---

⚡ Security

Biometric authentication mandatory for sending or saving drafts.

Data privacy respected.



---

🌟 Example Workflow

1. gmail +enter


2. Displays first 20 mails.


3. gmail open 1


4. Reads full email.


5. Types reply


6. Edits subject/body if needed.


7. Selects send.


8. Confirmation → Sends mail securely.
