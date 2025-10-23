package com.example.expensetracker.util

fun Double.format(decimals: Int): String {
    return "%.${decimals}f".format(this)
}