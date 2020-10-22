package com.tisza.esemenynaptar

enum class Category(val iD: Int, val stringID: String, val imageRes: Int, val displayNameRes: Int) {
    IRODALOM(0, "irodalom", R.drawable.irodalom, R.string.irodalom), TORTENELEM(1, "tortenelem", R.drawable.tortenelem, R.string.tortenelem), ZENETORTENET(2, "zenetortenet", R.drawable.zenetortenet, R.string.zenetortenet), VIZUALIS_KULTURA(3, "vizualis_kultura", R.drawable.vizualis_kultura, R.string.vizualis_kultura);

    companion object {
        fun fromStringID(str: String): Category? {
            for (category in values()) if (category.stringID == str) return category
            return null
        }
    }

}
