package de.betafx.fbremotedb

class NoUidException : IllegalStateException("No uid from FirebaseAuth available. Not logged in?")
