package com.stupidtree.hitax.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.DialogBottomCheckableListBinding
import com.stupidtree.hitax.databinding.DialogBottomCheckableListItemBinding
import com.stupidtree.hitax.ui.base.BaseListAdapter
import com.stupidtree.hitax.ui.base.BaseViewHolder
import com.stupidtree.hitax.ui.base.TransparentBottomSheetDialog
import java.util.*

/**
 * 圆角的文本框底部弹窗
 */
class PopUpCheckableList<T> : TransparentBottomSheetDialog<DialogBottomCheckableListBinding>() {


    var initTitle: String? = null

    /**
     * 适配器区
     */
    private lateinit var listAdapter: LAdapter<T>

    /**
     * 不得已放在UI里的数据
     */
    var listRes: MutableList<ItemData<T>>? = null
    var onConfirmListener: OnConfirmListener<T>? = null

    interface OnConfirmListener<T> {
        fun OnConfirm(title: String?, key: T)
    }

    fun setTitle(title: String): PopUpCheckableList<T> {
        initTitle = title
        return this
    }

    fun setListData(titles: List<String?>, keys: List<T>): PopUpCheckableList<T> {
        listRes = ArrayList()
        for (i in 0 until titles.size.coerceAtMost(keys.size)) {
            listRes?.add(ItemData(titles[i], keys[i]))
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
            override fun onItemClick(data: ItemData<T>?, card: View?, position: Int) {
                if (onConfirmListener != null) {
                    if (data != null) {
                        onConfirmListener!!.OnConfirm(data.name, data.data)
                    }
                    dismiss()
                }
            }
        })
        val list = binding.list
        list.adapter = listAdapter
        list.layoutManager = LinearLayoutManager(requireContext())
        if (initTitle != null) {
            binding.title.text = initTitle!!
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
    internal class LAdapter<C>(mContext: Context, mBeans: MutableList<ItemData<C>>) : BaseListAdapter<ItemData<C>, LAdapter.LHolder>(mContext, mBeans) {


        override fun bindHolder(holder: LHolder, data: ItemData<C>?, position: Int) {
            if (data != null) {
                holder.binding.text.text = data.name
            }
            holder.binding.item.setOnClickListener { view: View? ->
                data?.let { mOnItemClickListener?.onItemClick(it, view, position) }
            }
        }


        override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): LHolder {
            return LHolder(viewBinding as DialogBottomCheckableListItemBinding)
        }

        class LHolder(view: DialogBottomCheckableListItemBinding) : BaseViewHolder<DialogBottomCheckableListItemBinding>(view) {

        }

        override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
            return DialogBottomCheckableListItemBinding.inflate(mInflater, parent, false)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_checkable_list
    }

    override fun initViewBinding(v: View): DialogBottomCheckableListBinding {
        return DialogBottomCheckableListBinding.inflate(layoutInflater)
    }


}