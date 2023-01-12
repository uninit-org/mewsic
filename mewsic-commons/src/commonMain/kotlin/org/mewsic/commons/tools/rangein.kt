package org.mewsic.commons.tools

infix fun IntRange.isIn(range: IntRange): Boolean {
    return this.first in range && this.last in range
}

infix fun LongRange.isIn(range: LongRange): Boolean {
    return this.first in range && this.last in range
}

infix fun ULongRange.isIn(range: ULongRange): Boolean {
    return this.first in range && this.last in range
}

infix fun IntRange.isNotIn(range: IntRange): Boolean {
    return this.first !in range || this.last !in range
}

infix fun LongRange.isNotIn(range: LongRange): Boolean {
    return this.first !in range || this.last !in range
}

infix fun ULongRange.isNotIn(range: ULongRange): Boolean {
    return this.first !in range || this.last !in range
}
