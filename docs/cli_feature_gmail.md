✅ Final CLI Feature — Gmail


---

▶️ 1. List Mails

Command:

gmail +enter

Behavior:

Displays first 20 mails from Primary category.

Shows:

Timestamp

Sender Name

Subject

Status (Read / Unread / Replied)



Scrolling:

down + enter → Displays next 20 mails

up + enter → Displays previous 20 mails



---

▶️ 2. Search Mails Dynamically

Command:

gmail <search-keyword>

Behavior:

Dynamically shows matching mails while typing (searching subject and/or sender).

Lists matching mails in real time.



---

▶️ 3. Show Mails by Email Address

Command:

gmail <email-address>

Behavior:

Displays last 20 conversations with that email address.

Scroll up/down behaves same as normal list.



---

▶️ 4. Universal Mail Opening

Behavior:

From any of the following commands:

gmail +enter

gmail <email-address> + enter

gmail <search-keyword>


Displayed list will include numbered mails:

1. [Unread] From: John Doe | Subject: Meeting Reminder | Timestamp
2. [Read] From: Jane Smith | Subject: Monthly Report | Timestamp
...

User selects a mail by number to open full view:

3 + enter

Displays full mail:

To

From

CC

Subject

Timestamp

Body


After opening a mail, user can:

reply + enter → Opens editable reply mode (To, CC, Subject, Body editable).

replyall + enter → Opens editable reply-all mode.

exit + enter → Returns to normal CLI.



---

▶️ 5. Compose New Mail

Command:

gnew

Behavior:

Opens editable form:

To (email address)

CC (optional)

Subject

Body


Options:

send + enter → Sends mail.

draft + enter → Saves to drafts.

exit + enter → Exits without saving.




---

▶️ 6. Manage Drafts

Command:

gdraft

Behavior:

Shows list of drafts with timestamp, subject.

Select draft by number → Editable form appears (To, CC, Subject, Body).

send + enter → Sends mail.

draft + enter → Saves changes as draft.

exit + enter → Returns to draft list without changes.




---

▶️ 7. Delete Mails (Dedicated Command)

Command:

gdel

Behavior:

Supports deletion by:

gdel <search-keyword>

gdel <email-address> + enter


Displays numbered list of mails to delete.

Options:

Delete All → Deletes all mails in list.

Delete Range → Example:
1-5,7,10 → Deletes mails 1 to 5, 7, and 10.


Requires explicit confirmation before deletion.



---

▶️ 8. Reply / Reply All Flow

Behavior:

When reading a mail:

User types:

reply + enter

replyall + enter



Both open fully editable form:

To (editable)

CC (editable)

Subject (editable)

Body (editable)


Options:

send + enter → Sends mail.

draft + enter → Saves as draft.

exit + enter → Returns to mail view.



---

▶️ 9. Exit Gmail CLI

At any point (list, reading, composing, replying, drafts):

exit + enter

→ Returns to normal CLI prompt.


---

⚡ Universal Features

Scroll Up / Down works universally (mails list, search results, drafts).

Clear numbered interface for easy selection of mails.

Dynamic suggestions during typing (email addresses, subjects).

Explicit confirmation before deleting mails.

Consistent user prompts at every stage for clarity.

Safe and user-friendly flow, preventing accidental actions.

