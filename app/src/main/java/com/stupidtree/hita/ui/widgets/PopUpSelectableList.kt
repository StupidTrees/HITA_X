package com.stupidtree.hita.ui.widgets

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.stupidtree.hita.R
import com.stupidtree.hita.ui.base.BaseViewHolder
import com.stupidtree.hita.ui.base.BasicSelectableListAdapter
import java.util.*

/**
 * 圆角的文本框底部弹窗
 */
class PopUpSelectableList<T> : TransparentBottomSheetDialog() {
    /**
     * View绑定区
     */
    @JvmField
    @BindView(R.id.title)
    var title: TextView? = null

    @JvmField
    @BindView(R.id.list)
    var list: RecyclerView? = null

    @JvmField
    @BindView(R.id.confirm)
    var confirm: View? = null

    @JvmField
    @BindView(R.id.cancel)
    var cancel: View? = null

    @StringRes
    var init_title: Int? = null

    @StringRes
    var init_hint: Int? = null
    var init_text: String? = null

    /**
     * 适配器区
     */
    internal lateinit var listAdapter: LAdapter<T>

    /**
     * 不得已放在UI里的数据
     */
    var listRes: MutableList<ItemData<T>>? = null
    private var init_selected: T? = null
    private var onConfirmListener: OnConfirmListener<T>? = null

    interface OnConfirmListener<T> {
        fun OnConfirm(title: String?, key: T)
    }

    fun setTitle(@StringRes title: Int): PopUpSelectableList<T> {
        init_title = title
        return this
    }

    fun setText(text: String?): PopUpSelectableList<T> {
        init_text = text
        return this
    }

    fun setListData(titles: List<String?>, keys: List<T>): PopUpSelectableList<T> {
        listRes = ArrayList()
        for (i in 0 until titles.size.coerceAtMost(keys.size)) {
            (listRes as ArrayList<ItemData<T>>).add(ItemData(titles[i], keys[i]))
        }
        return this
    }

    fun setHint(@StringRes hint: Int): PopUpSelectableList<T> {
        init_hint = hint
        return this
    }

    fun setInitValue(value: T?): PopUpSelectableList<T> {
        init_selected = value
        return this
    }

    fun setOnConfirmListener(onConfirmListener: OnConfirmListener<T>): PopUpSelectableList<T> {
        this.onConfirmListener = onConfirmListener
        return this
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_selectable_list
    }

    override fun onStart() {
        super.onStart()
        if (init_selected != null) {
            listAdapter.setSelected(ItemData<T>(null, init_selected!!))
        }
        listAdapter.notifyDataSetChanged()
    }

    override fun initViews(v: View) {
        listAdapter = LAdapter(requireContext(), listRes!!)
        list!!.adapter = listAdapter
        list!!.layoutManager = LinearLayoutManager(requireContext())
        if (init_title != null) {
            title!!.setText(init_title!!)
        }
        cancel!!.setOnClickListener { view: View? -> dismiss() }
        confirm!!.setOnClickListener { view: View? ->
            if (onConfirmListener != null) {
                val data = listAdapter.selectedData
                Log.e("data", data.toString())
                if (data != null) {
                    onConfirmListener!!.OnConfirm(data.name, data.data)
                }
            }
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

    internal class LAdapter<C>(mContext: Context, mBeans: MutableList<ItemData<C>>) : BasicSelectableListAdapter<ItemData<C>, LAdapter.LHolder>(mContext!!, mBeans) {
        override fun getLayoutId(viewType: Int): Int {
            return R.layout.dialog_bottom_selectable_list_item
        }

        override fun createViewHolder(v: View, viewType: Int): LHolder {
            return LHolder(v)
        }

        protected override fun bindHolder(holder: LHolder, data: ItemData<C>?, position: Int) {
            if (data != null) {
                holder.text!!.text = data.name
                holder.item!!.setOnClickListener { view: View? -> selectItem(position, data) }
            }
            if (position == selectedIndex) { //若被选中
                holder.selected!!.visibility = View.VISIBLE
            } else {
                holder.selected!!.visibility = View.GONE
            }

        }

        internal class LHolder(itemView: View) : BaseViewHolder(itemView) {
            @JvmField
            @BindView(R.id.text)
            var text: TextView? = null

            @JvmField
            @BindView(R.id.item)
            var item: ViewGroup? = null

            @JvmField
            @BindView(R.id.selected)
            var selected: ImageView? = null
        }
    }
}