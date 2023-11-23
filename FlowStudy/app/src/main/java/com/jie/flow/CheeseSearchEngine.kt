package com.jie.flow

import java.util.*

class CheeseSearchEngine(val cheeses: List<String>) {
    var count = cheeses.size

    fun search(query: String): List<String> {
        var query = query
        query = query.lowercase(Locale.getDefault())
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val result: MutableList<String> = LinkedList()
        for (i in 0 until count) {
            if (cheeses[i].lowercase(Locale.getDefault()).contains(query)) {
                result.add(cheeses[i])
            }
        }
        return result
    }
}