package de.betafx.fbremotedb.remote_db

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ChildEventListenerList<T>(
    private val onChildAdded: (List<T>) -> Unit,
    private val onChildChanged: (List<T>) -> Unit,
    private val onChildRemoved: (List<T>) -> Unit,
    private val onChildMoved: (List<T>) -> Unit,
    private val onCancelled: () -> Unit,
    private val clazz: Class<T>
) : ChildEventListener {

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        val list = mutableListOf<T>()
        snapshot.children.forEach { ds ->
            ds.getValue(clazz)?.let { itm ->
                list.add(itm)
            }
        }
        onChildAdded.invoke(list)
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        val list = mutableListOf<T>()
        snapshot.children.forEach { ds ->
            ds.getValue(clazz)?.let { itm ->
                list.add(itm)
            }
        }
        onChildChanged.invoke(list)
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        val list = mutableListOf<T>()
        snapshot.children.forEach { ds ->
            ds.getValue(clazz)?.let { itm ->
                list.add(itm)
            }
        }
        onChildRemoved.invoke(list)
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        val list = mutableListOf<T>()
        snapshot.children.forEach { ds ->
            ds.getValue(clazz)?.let { itm ->
                list.add(itm)
            }
        }
        onChildMoved.invoke(list)
    }

    override fun onCancelled(error: DatabaseError) {
        onCancelled()
    }

}
