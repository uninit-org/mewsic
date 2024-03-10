package dev.uninit.mewsic.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map

fun <T> T.score(terms: List<String>, partsFn: T.() -> Array<String>): Int {
    var score = 0
    for (term in terms) {
        var termScore = 0
        val parts = partsFn(this)
        if (parts.any { it.contains(term, ignoreCase = true) }) {
            termScore -= 1
        } else {
            termScore += parts.map { title ->
                val m = title.length
                val n = term.length
                val d = Array(m + 1) { IntArray(n + 1) }
                for (i in 0..m) {
                    d[i][0] = i
                }
                for (j in 0..n) {
                    d[0][j] = j
                }
                for (i in 1..m) {
                    for (j in 1..n) {
                        val cost = if (title[i - 1] == term[j - 1]) 0 else 1
                        d[i][j] = minOf(
                            d[i - 1][j] + 1,
                            d[i][j - 1] + 1,
                            d[i - 1][j - 1] + cost
                        )
                    }
                }
                d[m][n]
            }.minOrNull() ?: 0
        }
        score += termScore
    }
    return score
}

suspend fun <T> Flow<T>.searchQuery(query: String, parts: T.() -> Array<String>): Flow<T> {
    val queryParts = query.split(" ")
    val entries = mutableListOf<Pair<T, Int>>()

    collect {
        val score = it.score(queryParts, parts)
        if (score > 0) {
            entries.add(it to score)
        }
    }

    return entries.asFlow().map { it.first }
}
