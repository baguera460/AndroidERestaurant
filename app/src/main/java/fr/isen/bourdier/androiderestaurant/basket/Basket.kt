package fr.isen.bourdier.androiderestaurant.basket

import android.content.Context
import fr.isen.bourdier.androiderestaurant.network.Dish
import com.google.gson.GsonBuilder

class Basket {
    var items: MutableList<BasketItem> = mutableListOf()

    fun add(dish: Dish, count: Int, context: Context) {
        val existingItem = items.firstOrNull { it.dish == dish }
        existingItem?.let {
            it.count = it.count + count
        } ?: run {
            items.add(BasketItem(count, dish))
        }
        save(context)
    }

    fun delete(item: BasketItem, context: Context) {
        items.removeAll { item.dish.name == it.dish.name }
        save(context)
    }

    fun deleteAll(context: Context) {
        items.clear()
        save(context)
    }

    private fun save(context: Context) {
        val json = GsonBuilder().create().toJson(this)

        val sharedPreferences =
            context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(BASKET_PREFERENCES_KEY, json)
        editor.apply()
    }

    companion object {
        fun current(context: Context): Basket {
            val sharedPreferences =
                context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
            val json = sharedPreferences.getString(BASKET_PREFERENCES_KEY, null)
            if (json != null) {
                return GsonBuilder().create().fromJson(json, Basket::class.java)
            }
            return Basket()
        }

        const val USER_PREFERENCES_NAME = "USER_PREFERENCES_NAME"
        const val BASKET_PREFERENCES_KEY = "BASKET_PREFERENCES_KEY"
    }
}