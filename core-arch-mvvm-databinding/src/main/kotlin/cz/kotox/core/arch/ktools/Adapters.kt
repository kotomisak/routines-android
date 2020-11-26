package cz.kotox.core.arch.ktools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import timber.log.Timber

open class DataBoundAdapter<T>(
	private val lifecycleOwner: LifecycleOwner,
	private val itemLayoutIdProvider: (T) -> Int,
	private val bindingVariableId: Int,
	diffCallback: DiffUtil.ItemCallback<T> = defaultComparatorCallback()
) : ListAdapter<T, DataBoundViewHolder>(diffCallback) {
	constructor(
		lifecycleOwner: LifecycleOwner,
		@LayoutRes itemLayoutId: Int,
		bindingVariableId: Int,
		diffCallback: DiffUtil.ItemCallback<T> = defaultComparatorCallback()
	) : this(lifecycleOwner, { itemLayoutId }, bindingVariableId, diffCallback)

	private val extras = hashMapOf<Int, Any>()

	private var dataSetIncreasedChangeCallback: (difference: Int) -> Unit = {}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
		val layoutInflater = LayoutInflater.from(parent.context)
		val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
		binding.setLifecycleOwner(lifecycleOwner)
		return DataBoundViewHolder(binding)
	}

	override fun getItemViewType(position: Int) = itemLayoutIdProvider.invoke(getItem(position)!!)

	override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
		holder.binding.setVariable(bindingVariableId, getItem(position))
		extras.forEach { (varId, extra) -> holder.binding.setVariable(varId, extra) }
		holder.binding.executePendingBindings()
	}

	fun bindExtra(bindingVariableId: Int, extra: Any) = this.also {
		extras.put(bindingVariableId, extra)
	}

	fun setDataSetSizeIncreasedCallback(callback: (difference: Int) -> Unit) = this.also {
		dataSetIncreasedChangeCallback = callback
	}

	override fun onCurrentListChanged(previousList: MutableList<T>, currentList: MutableList<T>) {
		super.onCurrentListChanged(previousList, currentList)
		Timber.d(">>>_F preSize=${previousList.size} vs. currSize=${currentList.size}")
		if (previousList.size < currentList.size) {
			Timber.d(">>>_F new item available")
			dataSetIncreasedChangeCallback.invoke(currentList.size - previousList.size)
		}
	}

}

class DataBoundViewHolder constructor(val binding: ViewDataBinding) : ViewHolder(binding.root)

fun <T> defaultComparatorCallback() = object : DiffUtil.ItemCallback<T>() {
	override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem

	// annoying https://issuetracker.google.com/issues/116789824
	override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}

@BindingAdapter("adapter", "items", requireAll = false)
fun <T> RecyclerView.setDataBoundAdapter(adapter: DataBoundAdapter<T>?, items: List<T>?) {
	if (adapter != null) {
		if (this.adapter == null)
			this.adapter = adapter
		(this.adapter as DataBoundAdapter<T>).submitList(items)
	}
}

@BindingAdapter("adapter", "items", requireAll = false)
fun <T> ViewPager.setAdapter(adapter: BindablePagerAdapter<T>?, items: List<T>?) {
	adapter?.let {
		if (this.adapter == null) {
			this.adapter = adapter
		}

		if (items != null) {
			(this.adapter as BindablePagerAdapter<T>).setItems(items)
		}
	}
}

class BindablePagerAdapter<T>(
	private val owner: LifecycleOwner?,
	private val bindingVariableId: Int,
	private val itemLayout: (T) -> Int
) : PagerAdapter() {

	private val items: MutableList<T> = mutableListOf()

	internal val extras = hashMapOf<Int, Any>()

	override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

	override fun getCount(): Int = items.size

	override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)

	override fun instantiateItem(container: ViewGroup, position: Int): Any {
		val layoutInflater = LayoutInflater.from(container.context)
		val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, itemLayout(items[position]), container, false)

		binding.setVariable(bindingVariableId, items[position])
		extras.forEach { (varId, extra) -> binding.setVariable(varId, extra) }
		owner?.let { binding.lifecycleOwner = owner }
		binding.executePendingBindings()
		container.addView(binding.root)
		return binding.root
	}

	fun setItems(items: List<T>) {
		this.items.clear()
		this.items.addAll(items)
		this.notifyDataSetChanged()
	}

	fun bindExtra(bindingVariableId: Int, extra: Any) = this.also {
		extras[bindingVariableId] = extra
	}
}

open class PagedAdapter<T : Any>(
	private val lifecycleOwner: LifecycleOwner,
	private val bindingVariableId: Int,
	private val itemLayoutIdProvider: (T) -> Int,
	diffCallback: DiffUtil.ItemCallback<T> = defaultComparatorCallback()
) : PagedListAdapter<T, DataBoundViewHolder>(diffCallback) {
	constructor(
		lifecycleOwner: LifecycleOwner,
		bindingVariableId: Int,
		@LayoutRes itemLayoutId: Int,
		diffCallback: DiffUtil.ItemCallback<T> = defaultComparatorCallback()
	) : this(lifecycleOwner, bindingVariableId, { itemLayoutId }, diffCallback)

	internal val extras = hashMapOf<Int, Any>()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
		val layoutInflater = LayoutInflater.from(parent.context)
		val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
		binding.lifecycleOwner = lifecycleOwner
		return DataBoundViewHolder(binding)
	}

	override fun getItemViewType(position: Int) = itemLayoutIdProvider.invoke(requireNotNull(getItem(position)))

	override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
		holder.binding.setVariable(bindingVariableId, getItem(position))
		extras.forEach { (varId, extra) -> holder.binding.setVariable(varId, extra) }
		holder.binding.executePendingBindings()
	}

	open fun bindExtra(bindingVariableId: Int, extra: Any) = this.also {
		extras[bindingVariableId] = extra
	}
}

class PagedLoadingAdapter<T : Any>(
	lifecycleOwner: LifecycleOwner,
	bindingVariableId: Int,
	private val itemLayoutIdProvider: (T) -> Int,
	private val progressLayoutId: Int,
	diffCallback: DiffUtil.ItemCallback<T> = defaultComparatorCallback()
) : PagedAdapter<T>(lifecycleOwner, bindingVariableId, itemLayoutIdProvider, diffCallback) {

	var loadingMore: Boolean? = null
		set(value) {
			if (field == true) {
				notifyItemRemoved(itemCount)
			}

			if (value == true) {
				notifyItemInserted(itemCount)
			}

			field = value
		}

	override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
		if (!isLoadingPage()) super.onBindViewHolder(holder, position)
	}

	override fun getItemCount(): Int =
		super.getItemCount() + if (isLoadingPage()) 1 else 0

	override fun getItemViewType(position: Int) =
		if (isLoadingPage() && position == itemCount - 1) {
			progressLayoutId
		} else {
			itemLayoutIdProvider.invoke(requireNotNull(getItem(position)))
		}

	override fun bindExtra(bindingVariableId: Int, extra: Any) = this.also {
		extras[bindingVariableId] = extra
	}

	private fun isLoadingPage() = loadingMore == true

}

@BindingAdapter("pagedAdapter", "pagedItems", requireAll = false)
fun <T : Any> RecyclerView.setPagedAdapter(adapter: PagedAdapter<T>?, items: PagedList<T>?) {
	adapter?.let {
		if (this.adapter == null)
			this.adapter = adapter
		if (items != null)
			(this.adapter as PagedAdapter<T>).submitList(items)
	}

}

