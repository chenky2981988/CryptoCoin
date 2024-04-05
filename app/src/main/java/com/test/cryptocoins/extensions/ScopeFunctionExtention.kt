package com.test.cryptocoins.extensions


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */

fun <T> T?.orElse(block: () -> T) = this ?: block()