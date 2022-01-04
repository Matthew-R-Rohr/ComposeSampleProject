package com.fct.compose.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

// For larger project we would want to split these extensions into individual relevant files

/**
 * Show a short toast message
 */
@SuppressLint("ShowToast")
fun Context?.toastShort(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

/**
 * Show a long toast message
 */
@SuppressLint("ShowToast")
fun Context?.toastLong(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

/**
 * Converts [Long] UNIX to [Date]
 */
fun Long.toDateFromUnix(): Date = Date(this * 1000)

/**
 * Converts [Date] into a string formatted MM/dd/yyyy
 */
fun Date.toMonthDayYearString(): String =
    SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(this)

/**
 * Return an empty string if original string is null
 */
fun String?.returnEmptyStringIfNull() = if (this.isNullOrBlank()) "" else this