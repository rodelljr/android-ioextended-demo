package com.roger.myapplication.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.roger.myapplication.R
import java.io.IOException
import java.io.Serializable

data class StateBirds(
    @SerializedName("state") @Expose var state: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("nomenclature") @Expose var nomenclature: String,
    @SerializedName("year") @Expose var year: Int,
    @SerializedName("picture") @Expose var picture: String,
    @SerializedName("thumb") @Expose var thumb: String,
    @SerializedName("description") @Expose var description: String,
    @SerializedName("male") @Expose var male: String,
    @SerializedName("female") @Expose var female: String,
    @SerializedName("history1") @Expose var history1: String,
    @SerializedName("history2") @Expose var history2: String?
) : Serializable

fun getBirdCollection(context: Context): List<StateBirds> {
    lateinit var jsonString: String
    try {
        jsonString = context.assets.open("statebirds.json")
            .bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        println(ioException)
    }
    val listStateBirds = object: TypeToken<List<StateBirds>>() {}.type
    return Gson().fromJson(jsonString, listStateBirds)
}

@SuppressLint("DiscouragedApi")
fun getDrawableFromName(state: String, context: Context ): Int {
    return context.resources.getIdentifier(state,"drawable", context.packageName)
}
//LocalContext.current