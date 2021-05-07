package com.example.stickyheader.extensions

/**
 * Used to guarantee a specific result in expressions
 **/
val <T> T.exhaustive: T
    get() = this