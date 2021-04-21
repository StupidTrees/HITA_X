package com.stupidtree.style.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.stupidtree.style.R
import com.stupidtree.style.base.BaseViewHolder
import com.stupidtree.style.base.BasicMultipleCheckableListAdapter
import com.stupidtree.style.databinding.DialogBottomSelectableListBinding
import com.stupidtree.style.databinding.DialogBottomSelectableListItemBinding
import java.util.*
import kotlin.collections.ArrayList

/**
 * 圆角的文本框底部弹窗
 */
class PopUpMultipleCheckableList<T>(@StringRes val title: Int, @StringRes hint: Int, private val minCheck:Int) : TransparentBottomSheetDialog<DialogBottomSelectableListBinding>(
) {

    /**
     * 适配器区
     */
    internal lateinit var listAdapter: LAdapter<T>

    /**
     * 不得已放在UI里的数据
     */
    private var listRes: MutableList<ItemData<T>>? = null
    private var initSelected: List<T> = mutableListOf()
    private var onConfirmListener: OnConfirmListener<T>? = null

    interface OnConfirmListener<T> {
        fun onConfirm(titles: List<String?>, data: List<T>)
    }

    fun setListData(titles: List<String?>, keys: List<T>): PopUpMultipleCheckableList<T> {
        listRes = ArrayList()
        for (i in 0 until titles.size.coerceAtMost(keys.size)) {
            (listRes as ArrayList<ItemData<T>>).add(ItemData(titles[i], keys[i]))
        }
        return this
    }

    fun setInitValues(value: List<T>): PopUpMultipleCheckableList<T> {
        initSelected = value
        return this
    }

    fun setOnConfirmListener(onConfirmListener: OnConfirmListener<T>): PopUpMultipleCheckableList<T> {
        this.onConfirmListener = onConfirmListener
        return this
    }


    override fun onStart() {
        super.onStart()
        val l = mutableListOf<ItemData<T>>()
        for(d in initSelected){
            l.add(ItemData(null,d))
        }
        listAdapter.setChecked(l)
    }

    override fun initViews(v: View) {
        listAdapter = LAdapter(requireContext(), listRes!!,minCheck)
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.title.setText(title)
        binding.cancel.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener {
            val data = listAdapter.getCheckedData()
            val titles = mutableListOf<String?>()
            val keys = mutableListOf<T>()
            for (d in data) {
                titles.add(d.name)
                keys.add(d.data)
            }
            onConfirmListener?.onConfirm(titles, keys)
            dismiss()
        }
    }

    class ItemData<K>(var name: String?, var data: K) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val keyData = other as ItemData<*>
            return data == keyData.data
        }

        override fun hashCode(): Int {
            return Objects.hash(data)
        }

    }

    @SuppressLint("ParcelCreator")
    internal class LAdapter<C>(mContext: Context, mBeans: MutableList<ItemData<C>>,minCheck: Int) : BasicMultipleCheckableListAdapter<ItemData<C>, LAdapter.LHolder>(
            mContext, mBeans,minCheck) {

        override fun bindHolder(holder: LHolder, data: ItemData<C>?, position: Int) {
            if (data != null) {
                holder.binding.text.text = data.name
                holder.binding.item.setOnClickListener { checkItem(position) }
            }
            if (selectedIndex.contains(position)) { //若被选中
                holder.binding.selected.visibility = View.VISIBLE
            } else {
                holder.binding.selected.visibility = View.GONE
            }

        }

        internal class LHolder(view: DialogBottomSelectableListItemBinding) : BaseViewHolder<DialogBottomSelectableListItemBinding>(view)

        override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): LHolder {
            return LHolder(viewBinding as DialogBottomSelectableListItemBinding)
        }

        override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
            return DialogBottomSelectableListItemBinding.inflate(mInflater, parent, false)
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_selectable_list
    }

    override fun initViewBinding(v: View): DialogBottomSelectableListBinding {
        return DialogBottomSelectableListBinding.bind(v)
    }
}