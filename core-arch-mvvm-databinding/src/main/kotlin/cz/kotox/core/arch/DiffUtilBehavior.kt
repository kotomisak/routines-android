package cz.kotox.core.arch

interface DiffUtilBehavior {

	/**
	 *  THE SAME ITEM (by id)
	 *  Check whether two objects represent the same item.
	 *  For example check id's equality.
	 */
	fun isItemTheSame(other: Any): Boolean

	/**
	 * THE SAME VISUAL ITEM
	 * Check whether two items have the same data.
	 * If visual representations are the same.
	 * This method is called only if isItemsTheSame return true for these items.
	 */
	fun isContentTheSame(other: Any): Boolean {
		return equals(other)
	}
}