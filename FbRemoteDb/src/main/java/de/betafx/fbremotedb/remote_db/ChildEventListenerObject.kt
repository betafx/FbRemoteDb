package de.betafx.fbremotedb.remote_db

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ChildEventListenerObject<T>(
    private val onChildAdded: (T) -> Unit,
    private val onChildChanged: (T) -> Unit,
    private val onChildRemoved: (T) -> Unit,
    private val onChildMoved: (T) -> Unit,
    private val onCancelled: () -> Unit,
    private val clazz: Class<T>
) : ChildEventListener {

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        snapshot.getValue(clazz)?.let {
            onChildAdded.invoke(it)
        }
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        snapshot.getValue(clazz)?.let {
            onChildChanged.invoke(it)
        }
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        snapshot.getValue(clazz)?.let {
            onChildRemoved.invoke(it)
        }
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        snapshot.getValue(clazz)?.let {
            onChildMoved.invoke(it)
        }
    }

    override fun onCancelled(error: DatabaseError) {
        onCancelled()
    }

}