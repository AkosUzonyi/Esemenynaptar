package com.tisza.esemenynaptar

fun categoryFromStringID(str: String): Category = Category.values().filter { it.stringID == str }.first()

enum class Category(val iD: Int, val stringID: String, val imageRes: Int, val displayNameRes: Int) {
    IRODALOM(0, "irodalom", R.drawable.irodalom, R.string.irodalom), TORTENELEM(1, "tortenelem", R.drawable.tortenelem, R.string.tortenelem), ZENETORTENET(2, "zenetortenet", R.drawable.zenetortenet, R.string.zenetortenet), VIZUALIS_KULTURA(3, "vizualis_kultura", R.drawable.vizualis_kultura, R.string.vizualis_kultura);

}
