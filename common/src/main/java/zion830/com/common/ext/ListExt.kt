package zion830.com.common.ext

import kotlin.random.Random

fun <E> List<E>?.isNullOrEmpty(): Boolean = (this == null || isEmpty())

fun <E> List<E>?.isNotNullOrEmpty(): Boolean = !isNullOrEmpty()

fun <T> List<T>.random(): T = get(Random.nextInt(0, size - 1))

fun <T> Array<T>.random(): T = get(Random.nextInt(0, size - 1))

fun <E : Any> List<E?>.filterNotNull(): List<E> = filterNotNullTo(arrayListOf())