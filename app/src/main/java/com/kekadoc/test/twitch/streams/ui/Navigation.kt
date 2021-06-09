package com.kekadoc.test.twitch.streams.ui

import android.os.Bundle

interface Navigation {
    fun navigate(id: Int, data: Bundle = Bundle.EMPTY)
}