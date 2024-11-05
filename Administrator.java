package com.example.seg2505_projet;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * The Administrator class represents an administrative user in the system.
 * It extends the User class and has predefined credentials for authentication.
 * The administrator has the ability to manage other users, such as requesters.
 *
 * Author: Alexandre Belanger
 * Author: Ethan Michael
 */
public class Administrator extends User {
    // Firebase connection to reference the Users node
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference databaseReferenceUsers = database.getReference("Users");
    final DatabaseReference databaseReferenceParts = database.getReference("Parts");
    final DatabaseReference databaseReferenceOrders = database.getReference("Orders");

    // Predefined username and password for the administrator
    private static final String USERNAME = "a";
    private static final String PASSWORD = "a";

    // List to store requesters retrieved from Firebase
    final ArrayList<Requester> requesterList = new ArrayList<>();

    /**
     * Constructs a new Administrator object with a predefined username and password.
     */
    public Administrator() {
        super(USERNAME, PASSWORD);
    }

    /**
     * Deletes a specific requester from Firebase.
     *
     * @param r The Requester object to be deleted.
     */
    public void deleteRequester(Requester r) {
        databaseReferenceUsers.child(r.getUsername()).setValue(null);
    }

    /**
     * Deletes all requesters from Firebase.
     * Iterates through the requesterList and calls deleteRequester for each requester.
     */
    public void purgeAllRequesters() {
        for (Requester requester : requesterList) {
            deleteRequester(requester);
        }
    }

    /**
     * Purges all parts from Firebase.
     * Iterates through the parts list and deletes each part based on its ID.
     */
    public void purgeAllParts() {
        ArrayList<Hardware> parts = new ArrayList<>(MainActivity.sk.getPartList());
        for (Hardware part : parts) {
            databaseReferenceParts.child(String.valueOf(part.getId())).setValue(null);
        }
    }

    /**
     * Adds a new requester to Firebase.
     *
     * @param r The Requester object to be added.
     */
    public void addRequester(Requester r) {
        databaseReferenceUsers.child(r.getUsername()).setValue(r);
    }

    /**
     * Retrieves the number of requesters currently stored in the requesterList.
     *
     * @return The size of the requesterList.
     */
    public int getNumberOfRequesters() {
        return requesterList.size();
    }

    /**
     * Retrieves the list of requesters from the Firebase server.
     * Updates the requesterList with the retrieved requesters and logs their information.
     *
     * @return The updated requesterList.
     */
    public ArrayList<Requester> getRequesterList() {
        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the list to prevent duplicates
                requesterList.clear();

                // Add all children to the array list
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Requester requester = child.getValue(Requester.class);
                    if (requester != null) {
                        Log.d("FirebaseData", "Requester: " + requester.getFirstName() +
                                ", Creation Time: " + requester.getCreationTime());
                        requesterList.add(requester);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Administrator", "Failed to read requesters", error.toException());
            }
        });
        return requesterList;
    }
}
