package de.betafx.fbremotedb.remote_db

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

internal class ValueListenerBoolean(
    private val onFinished: (Boolean) -> Unit,
    private val onCancel: () -> Unit,
    private val condition: (Iterable<DataSnapshot>?) -> Boolean
) : ValueEventListener {

    override fun onDataChange(snapshot: DataSnapshot) {
        snapshot.ref.get()
            .addOnSuccessListener { dataSnapshot ->
                if (condition.invoke(dataSnapshot.children)) {
                    onFinished(true)
                } else {
                    onFinished(false)
                }
            }
            .addOnCanceledListener { onCancel() }
            .addOnFailureListener { onCancel() }
    }

    override fun onCancelled(error: DatabaseError) {
        onCancel()
    }
}
