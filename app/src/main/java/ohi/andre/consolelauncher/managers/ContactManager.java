package ohi.andre.consolelauncher.managers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import ohi.andre.consolelauncher.tuils.Tuils;

public class ContactManager {

    private Context context;

    public ContactManager(Context context) {
        this.context = context;
    }

    /**
     * Checks if READ_CONTACTS permission is granted.
     */
    public boolean hasReadContactsPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests READ_CONTACTS permission from user (if needed, should be called from Activity).
     */
    public void requestReadContactsPermission(int requestCode) {
        if (context instanceof android.app.Activity) {
            ActivityCompat.requestPermissions((android.app.Activity) context,
                    new String[]{Manifest.permission.READ_CONTACTS}, requestCode);
        }
    }

    /**
     * Returns list of all contacts' names.
     */
    @NonNull
    public List<String> getAllContactNames() {
        List<String> contacts = new ArrayList<>();
        if (!hasReadContactsPermission()) return contacts;

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null, null, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    );
                    contacts.add(name);
                }
            }
        } catch (Exception e) {
            Tuils.log(e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return contacts;
    }

    /**
     * Finds phone numbers for a given contact name.
     */
    @NonNull
    public List<String> getNumbersByContact(String contactName) {
        List<String> numbers = new ArrayList<>();
        if (!hasReadContactsPermission()) return numbers;

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                    new String[]{contactName},
                    null
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String number = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    );
                    numbers.add(number);
                }
            }
        } catch (Exception e) {
            Tuils.log(e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return numbers;
    }
}
