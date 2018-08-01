package com.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.SessionModel

interface SessionListView {
    var isUpdating: Boolean
    fun onUpdate(sessions: List<SessionModel>, favorites: List<SessionModel>)
}