//package com.stupidtree.hita.ui.base
//
//import android.content.Context
//import android.util.SparseArray
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.stupidtree.hita.R
//import com.stupidtree.hita.ui.base.BaseEditableListAdapter.EditableViewHolder
//import com.stupidtree.hita.ui.base.BaseEditableListAdapter.EditableViewHolder.ValueChangeListener
//import java.util.*
//
//abstract class BaseEditableListAdapter internal constructor(mContext: Context, mBeans: MutableList<SparseArray<String>>, private var valueMap: HashMap<String, Any>) : BaseListAdapter<SparseArray<String>, EditableViewHolder>(mContext, mBeans) {
//    private var isEditMode = false
//    private val valueMapEditing: HashMap<String, Any> = HashMap()
//    fun activateEditSmooth() {
//        isEditMode = true
//        notifyItemChangedSmooth(ArrayList(mBeans))
//        valueMapEditing.clear()
//        valueMapEditing.putAll(valueMap)
//        // notifyDataSetChanged();
//    }
//
//    fun activateEdit() {
//        isEditMode = true
//        valueMapEditing.clear()
//        valueMapEditing.putAll(valueMap)
//        notifyDataSetChanged()
//    }
//
//    fun saveEditSmooth(): HashMap<String, Any> {
//        isEditMode = false
//        valueMap.putAll(valueMapEditing)
//        notifyItemChangedSmooth(ArrayList(mBeans))
//        return valueMap
//    }
//
//    fun saveEdit(): HashMap<String, Any> {
//        isEditMode = false
//        valueMap.putAll(valueMapEditing)
//        notifyDataSetChanged()
//        return valueMap
//    }
//
//    abstract fun bindHolderData(holder: EditableViewHolder?, position: Int, data: Any?)
//    override fun onBindViewHolder(holder: EditableViewHolder, position: Int) {
//        super.onBindViewHolder(holder, position)
//        if (isEditMode && holder.edit != null) {
//            holder.edit!!.visibility = View.VISIBLE
//            if (holder.show != null) holder.show!!.visibility = View.GONE
//        } else if (holder.edit != null) {
//            holder.edit!!.visibility = View.GONE
//            if (holder.show != null) holder.show!!.visibility = View.VISIBLE
//        }
//        if (position + indexBias <= mBeans.size - 1) {
//            val key = mBeans[position + indexBias][KEY]!!
//            val name = mBeans[position + indexBias][NAME]!!
//            holder.name.text = name
//            val data = valueMap[key]
//            holder.setShowValue(data)
//            holder.valueChangeListener = object : ValueChangeListener {
//                override fun onValueChanged(v: View?, value: Any) {
//                    valueMapEditing[key] = value
//                }
//            }
//            if (isEditMode && holder.edit != null) {
//                holder.setEditValue(data)
//            }
//            bindHolderData(holder, position, data)
//        } else {
//            bindHolderData(holder, position, null)
//        }
//    }
//
//    abstract class EditableViewHolder(itemView: View, type: Int) : RecyclerView.ViewHolder(itemView) {
//        var show: TextView? = itemView.findViewById(R.id.show)
//        var name: TextView = itemView.findViewById(R.id.name)
//        var edit: View? = itemView.findViewById(R.id.edit)
//        var item: ViewGroup = itemView.findViewById(R.id.item)
//        var valueChangeListener: ValueChangeListener? = null
//        var type = 0
//
//
//        abstract fun setEditValue(value: Any?)
//        abstract fun setShowValue(value: Any?)
//        interface ValueChangeListener {
//            fun onValueChanged(v: View?, value: Any)
//        }
//    }
//
//
//    companion object {
//        const val NAME = 646
//        const val KEY = 398
//        const val TYPE = 480
//    }
//
//}