package com.stupidtree.hita.utils

import android.app.Activity
import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.stupidtree.hita.R
import com.stupidtree.hita.ui.base.BaseCheckableListAdapter
import com.stupidtree.hita.ui.widgets.PopUpText
import java.util.*

class EditModeHelper<T>(val mContext: Context, private val listAdapter: BaseCheckableListAdapter<T, *>,
                        val editableContainer: EditableContainer<T>) {
    var isEditMode = false
        private set

    private
    var editLayout: ViewGroup? = null
    private var cancel: ImageView? = null
    private var selectAll: ImageView? = null
    private var delete: ImageView? = null
    private var selectedNum: TextView? = null
    private val containerPoorChildren: MutableList<View> = mutableListOf()
    var smoothSwitch = false


    fun closeEditMode() {
        if (!smoothSwitch) listAdapter.closeEdit() else listAdapter.closeEditSmooth()
        isEditMode = false
        editLayout?.visibility = View.GONE
        for (v in containerPoorChildren) {
            v.visibility = View.VISIBLE
        }
        editableContainer.onEditClosed()
    }

    fun activateEditMode(initPos: Int) {
        if (!smoothSwitch) listAdapter.activateEditSmooth(initPos) else listAdapter.activateEditSmooth(initPos)
        isEditMode = true
        editLayout?.visibility = View.VISIBLE
        for (v in containerPoorChildren) {
            v.visibility = View.GONE
        }
        editableContainer.onEditStarted()
    }

    fun init(a: Activity, containerId: Int) {
        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        val viewGroup: ViewGroup = a.findViewById(containerId)
        for (i in 0 until viewGroup.childCount) {
            containerPoorChildren.add(viewGroup.getChildAt(i))
        }
        val barView: View = inflater.inflate(R.layout.edit_mode_bar_1, viewGroup)
        // viewGroup.addView(barView);
        init(barView)
    }

    fun init(a: View, containerId: Int) {
        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        val viewGroup: ViewGroup = a.findViewById(containerId)
        for (i in 0 until viewGroup.childCount) {
            containerPoorChildren.add(viewGroup.getChildAt(i))
        }
        val barView: View = inflater.inflate(R.layout.edit_mode_bar_1, viewGroup)
        // viewGroup.addView(barView);
        init(barView)
    }

    fun init(a: Activity, containerId: Int, editBarLayout: Int) {
        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        val viewGroup: ViewGroup = a.findViewById(containerId)
        for (i in 0 until viewGroup.childCount) {
            containerPoorChildren.add(viewGroup.getChildAt(i))
        }
        val barView: View = inflater.inflate(editBarLayout, viewGroup)
        init(barView)
    }

    fun init(a: View, containerId: Int, editBarLayout: Int) {
        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        val viewGroup: ViewGroup = a.findViewById<ViewGroup>(containerId)
        for (i in 0 until viewGroup.childCount) {
            containerPoorChildren.add(viewGroup.getChildAt(i))
        }
        val barView: View = inflater.inflate(editBarLayout, viewGroup)
        init(barView)
    }

    fun init(v: View) {
        cancel = v.findViewById(R.id.cancel)
        delete = v.findViewById(R.id.delete)
        editLayout = v.findViewById(R.id.edit_layout)
        selectAll = v.findViewById(R.id.select_all)
        selectedNum = v.findViewById(R.id.num_selected)
        listAdapter.setOnItemSelectedListener(object : BaseCheckableListAdapter.OnItemSelectedListener {
            override fun onItemSelected(v: View?, checked: Boolean, position: Int, selectedNum: Int) {
                if (this@EditModeHelper.selectedNum != null) this@EditModeHelper.selectedNum?.text = mContext.getString(R.string.number_of_items_selected, selectedNum)
                editableContainer.onItemCheckedChanged(position, checked, selectedNum)
                if (selectedNum == 0) closeEditMode()
            }
        })
        cancel?.setOnClickListener { closeEditMode() }
        delete?.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            showDeleteConfirmDialog()
        }
        selectAll?.setOnClickListener {
            if (!listAdapter.selectAll()) {
                closeEditMode()
            }
        }
        closeEditMode()
    }

    fun init(v: Activity) {
        cancel = v.findViewById(R.id.cancel)
        delete = v.findViewById(R.id.delete)
        editLayout = v.findViewById(R.id.edit_layout)
        selectAll = v.findViewById(R.id.select_all)
        selectedNum = v.findViewById(R.id.num_selected)
        listAdapter.setOnItemSelectedListener(object : BaseCheckableListAdapter.OnItemSelectedListener {
            override fun onItemSelected(v: View?, checked: Boolean, position: Int, selectedNum: Int) {
                if (this@EditModeHelper.selectedNum != null) this@EditModeHelper.selectedNum?.text = mContext.getString(R.string.number_of_items_selected, selectedNum)
                editableContainer.onItemCheckedChanged(position, checked, selectedNum)
                if (selectedNum == 0) closeEditMode()
            }
        })
        cancel?.setOnClickListener { closeEditMode() }
        delete?.setOnClickListener {
            showDeleteConfirmDialog()
        }
        selectAll?.setOnClickListener {
            if (smoothSwitch) {
                if (!listAdapter.selectAllSmooth()) {
                    closeEditMode()
                }
            } else {
                if (!listAdapter.selectAll()) {
                    closeEditMode()
                }
            }
        }
        closeEditMode()
    }

    private fun showDeleteConfirmDialog() {
        if(mContext is AppCompatActivity){
            PopUpText().setTitle(R.string.dialog_title_sure_delete)
                    .setOnConfirmListener(object : PopUpText.OnConfirmListener {
                        override fun OnConfirm() {
                            editableContainer.onDelete(listAdapter.checkedItem)
                        }

                    }).show(mContext.supportFragmentManager,"sure")
        }

    }

    interface EditableContainer<T> {
        fun onEditClosed()
        fun onEditStarted()
        fun onItemCheckedChanged(position: Int, checked: Boolean, currentSelected: Int)
        fun onDelete(toDelete: Collection<T>?)
    }

}