package de.betafx.fbremotedb.remote_db

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

internal class ValueListenerList<T>(
    private val onFinished: (List<T>) -> Unit,
    private val onCancel: () -> Unit,
    private val clazz: Class<T>
) : ValueEventListener {

    override fun onDataChange(snapshot: DataSnapshot) {
        snapshot.ref.get()
            .addOnSuccessListener { dataSnapshot ->
                val list = mutableListOf<T>()
                dataSnapshot.children.forEach { ds ->
                    ds.getValue(clazz)?.let { itm ->
                        list.add(itm)
                    }
                }
                onFinished.invoke(list)
            }
            .addOnCanceledListener { onCancel() }
            .addOnFailureListener { onCancel() }
    }

    override fun onCancelled(error: DatabaseError) {
        onCancel()
    }
}

