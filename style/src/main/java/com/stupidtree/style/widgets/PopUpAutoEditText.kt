package com.stupidtree.style.widgets

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.stupidtree.style.R
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder
import com.stupidtree.style.databinding.DialogBottomAutoEditTextBinding
import com.stupidtree.style.databinding.DialogBottomCheckableListItemBinding

/**
 * 圆角的文本框底部弹窗
 */
class PopUpAutoEditText : TransparentBottomSheetDialog<DialogBottomAutoEditTextBinding>() {
    /**
     * View绑定区
     */

    var init_title: String? = null
    var initValue: String? = null

    /**
     * 适配器区
     */
    internal lateinit var listAdapter: LAdapter

    /**
     * 不得已放在UI里的数据
     */
    var listLiveData: MediatorLiveData<List<String>> = MediatorLiveData()
    var onConfirmListener: OnConfirmListener? = null
    var dataLoader: DataLoader? = null

    interface OnConfirmListener {
        fun OnConfirm(content: String)
    }

    interface DataLoader {
        fun loadData(str: String): LiveData<List<String>>
    }


    fun setTitle(title: String): PopUpAutoEditText {
        init_title = title
        return this
    }
    fun setInitValue(value: String): PopUpAutoEditText {
        initValue = value
        return this
    }

    fun setOnConfirmListener(onConfirmListener: OnConfirmListener): PopUpAutoEditText {
        this.onConfirmListener = onConfirmListener
        return this
    }

    fun setDataLoader(loader: DataLoader): PopUpAutoEditText {
        this.dataLoader = loader
        return this
    }

    override fun initViews(v: View) {
        listAdapter = LAdapter(requireContext(), mutableListOf())
        listAdapter.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener<String> {
            override fun onItemClick(data: String?, card: View?, position: Int) {
                data?.let {
                    onConfirmListener?.OnConfirm(it)
                    dismiss()
                }
            }
        })
        val list = binding.list
        list.adapter = listAdapter
        list.layoutManager = LinearLayoutManager(requireContext())
        if (init_title != null) {
            binding.title.text = init_title
        }
        listLiveData.observe(this) {
            listAdapter.notifyItemChangedSmooth(it)
        }

        binding.text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                dataLoader?.let {
                    if(s.isNullOrEmpty()) {
                        listLiveData.value = mutableListOf()
                        return
                    }
                    listLiveData.addSource(it.loadData(s.toString())) { dt ->
                        listLiveData.value = dt
                    }
                }
            }

        })
        binding.confirm.setOnClickListener {
            if (binding.text.toString().isNotBlank()) {
                onConfirmListener?.OnConfirm(binding.text.text.toString())
                dismiss()
            }
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.text.setText(initValue)
    }


    internal class LAdapter(mContext: Context, mBeans: MutableList<String>) :
        BaseListAdapter<String, LAdapter.LHolder>(mContext, mBeans) {


        override fun bindHolder(holder: LHolder, data: String?, position: Int) {
            if (data != null) {
                holder.binding.text.text = data
            }
            holder.binding.item.setOnClickListener { view: View? ->
                data?.let { mOnItemClickListener?.onItemClick(it, view, position) }
            }
        }


        override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): LHolder {
            return LHolder(viewBinding as DialogBottomCheckableListItemBinding)
        }

        class LHolder(view: DialogBottomCheckableListItemBinding) :
            BaseViewHolder<DialogBottomCheckableListItemBinding>(view)

        override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
            return DialogBottomCheckableListItemBinding.inflate(mInflater, parent, false)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_auto_edit_text
    }

    override fun initViewBinding(v: View): DialogBottomAutoEditTextBinding {
        return DialogBottomAutoEditTextBinding.bind(v)
    }


}