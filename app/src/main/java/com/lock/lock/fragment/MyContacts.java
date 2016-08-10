package com.lock.lock.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.lock.lock.recycler.ListContactsFragment;

public class MyContacts extends ListContactsFragment {

    public MyContacts() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("contact")
                .child(getUid()).orderByChild("Name");
    }
}
