package com.stupidtree.hita.ui.widgets

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.databinding.DialogBottomCheckableListBinding
import com.stupidtree.hita.databinding.DialogBottomCheckableListItemBinding
import com.stupidtree.hita.ui.base.BaseListAdapter
import com.stupidtree.hita.ui.base.BaseViewHolder
import java.util.*

/**
 * 圆角的文本框底部弹窗
 */
class PopUpCheckableList<T> : TransparentBottomSheetDialog<DialogBottomCheckableListBinding>() {
    /**
     * View绑定区
     */

    @StringRes
    var init_title: Int? = null

    /**
     * 适配器区
     */
    internal lateinit var listAdapter: LAdapter<T>

    /**
     * 不得已放在UI里的数据
     */
    var listRes: MutableList<ItemData<T>>? = null
    var onConfirmListener: OnConfirmListener<T>? = null

    interface OnConfirmListener<T> {
        fun OnConfirm(title: String?, key: T)
    }

    fun setTitle(@StringRes title: Int): PopUpCheckableList<T> {
        init_title = title
        return this
    }

    fun setListData(titles: List<String?>, keys: List<T>): PopUpCheckableList<T> {
        listRes = ArrayList()
        for (i in 0 until Math.min(titles.size, keys.size)) {
            (listRes as ArrayList<ItemData<T?>>).add(ItemData(titles[i], keys[i]))
        }
        return this
    }

    fun setOnConfirmListener(onConfirmListener: OnConfirmListener<T>?): PopUpCheckableList<T> {
        this.onConfirmListener = onConfirmListener
        return this
    }


    override fun onStart() {
        super.onStart()
        listAdapter.notifyDataSetChanged()
    }

    override fun initViews(v: View) {
        listAdapter = LAdapter(requireContext(), listRes!!)
        listAdapter.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener<ItemData<T>> {
            override fun onItemClick(data: ItemData<T>, card: View?, position: Int) {
                if (onConfirmListener != null) {
                    onConfirmListener!!.OnConfirm(data.name, data.data)
                    dismiss()
                }
            }
        })
        val list = binding.list
        list.adapter = listAdapter
        list.layoutManager = LinearLayoutManager(requireContext())
        if (init_title != null) {
           binding.title.setText(init_title!!)
        }
    }

    class ItemData<K>(var name: String?, var data: K) {
        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val keyData = o as ItemData<*>
            return data == keyData.data
        }

        override fun hashCode(): Int {
            return Objects.hash(data)
        }
    }
    override fun initViewBinding(): DialogBottomCheckableListBinding {
        return DialogBottomCheckableListBinding.inflate(layoutInflater)
    }

    internal class LAdapter<C>(mContext: Context, mBeans: MutableList<ItemData<C>>) : BaseListAdapter<ItemData<C>, LAdapter.LHolder>(mContext, mBeans) {


        override fun bindHolder(holder: LHolder, data: ItemData<C>?, position: Int) {
            if (data != null) {
                holder.binding.text.text = data.name
            }
            holder.binding.item.setOnClickListener { view: View? ->
                data?.let { mOnItemClickListener?.onItemClick(it, view, position) }
            }
        }


        override fun getViewBinding(viewType: Int): ViewBinding {
            return DialogBottomCheckableListItemBinding.inflate(mInflater)
        }

        override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): LHolder {
            return LHolder(viewBinding as DialogBottomCheckableListItemBinding)
        }

        class LHolder(view:DialogBottomCheckableListItemBinding) : BaseViewHolder<DialogBottomCheckableListItemBinding>(view) {

        }
    }


}