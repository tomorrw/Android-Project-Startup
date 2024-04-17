package com.tomorrow.readviewmodel.utils


fun Throwable.toUserFriendlyError(): String =
    this.message.let { if (!it.isNullOrBlank() && it.length < 100) it else "Something went wrong!" }