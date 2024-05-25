package com.ablil.springstarter.common.matchers

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.springframework.data.domain.Sort.Direction

/**
 * Hamcrest matcher to check if a list of string is sorted in the given order
 */
class SortedInOrder(private val direction: Direction, private val isNumericSort: Boolean = false) :
    TypeSafeMatcher<List<String>>() {
    override fun describeTo(p0: Description?) {
        p0?.appendText("sorted in $direction")
    }

    override fun matchesSafely(list: List<String>): Boolean {
        return if (isNumericSort) {
            list.map { it.toInt() }
                .zipWithNext { first, second -> if (direction == Direction.ASC) first <= second else first >= second }
                .all { it }
        } else {
            list.zipWithNext { first, second -> if (direction == Direction.ASC) first <= second else first >= second }
                .all { it }
        }
    }
}
