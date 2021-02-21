package com.stupidtree.hita.ui.timetable.manager

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.databinding.ActivityTimetableManagerBinding
import com.stupidtree.hita.databinding.DynamicCurriculumItemBinding
import com.stupidtree.hita.ui.base.BaseActivity
import com.stupidtree.hita.ui.base.BaseActivityWithReceiver
import com.stupidtree.hita.ui.base.BaseListAdapter
import com.stupidtree.hita.ui.base.BaseViewHolder
import com.stupidtree.hita.ui.widgets.PopUpText
import com.stupidtree.hita.utils.ActivityUtils
import com.stupidtree.hita.utils.TimeUtils

class TimetableManagerActivity :
    BaseActivity<TimetableManagerViewModel, ActivityTimetableManagerBinding>() {

    private var listAdapter: CListAdapter? = null
    private var timetableSP: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
        setToolbarActionBack(binding.mainToolBar)
        timetableSP = getSharedPreferences("timetable_pref", Context.MODE_PRIVATE)
    }

    override fun initViews() {
        //binding.mainToolBar.inflateMenu(R.menu.toolbar_curriculum_manager)
        binding.mainToolBar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.add) {
                //FragmentImportCurriculum().show(getSupportFragmentManager(), "import")
            }
            true
        }
        listAdapter = CListAdapter(this, mutableListOf())
        binding.list.adapter = listAdapter
        binding.list.layoutManager = GridLayoutManager(this, 2)
        listAdapter?.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<Timetable> {
            override fun onItemClick(data: Timetable, card: View?, position: Int) {
                ActivityUtils.startTimetableDetailActivity(getThis(), data.id)
            }

        })
        bindLiveData()
    }

    private fun bindLiveData() {
        viewModel.timetablesLiveData.observe(this) {
            listAdapter?.notifyItemChangedSmooth(it)
            binding.noneLayout.visibility = if(it.isNullOrEmpty()){
                View.VISIBLE
            }else{
                View.GONE
            }
        }
    }


//    //当选择完Excel文件后调用此函数
//    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == CHOOSE_FILE_CODE) {
//                val uri: Uri = data.getData()
//                val sPath1: String
//                sPath1 = FileOperator.getPath(this, uri) // Paul Burke写的函数，根据Uri获得文件路径
//                if (sPath1 == null) return
//                val file = File(sPath1)
//                loadCurriculumTask(this, file, Calendar.getInstance()).executeOnExecutor(HITAApplication.TPE)
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }


    @SuppressLint("ParcelCreator")
    inner class CListAdapter(context: Context, mBeans: MutableList<Timetable>) :
        BaseListAdapter<Timetable, CListAdapter.CHolder>(context, mBeans) {


        inner class CHolder(itemView: DynamicCurriculumItemBinding) :
            BaseViewHolder<DynamicCurriculumItemBinding>(itemView)

        override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
            return DynamicCurriculumItemBinding.inflate(mInflater, parent, false)
        }

        override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): CHolder {
            return CHolder(viewBinding as DynamicCurriculumItemBinding)
        }

        @SuppressLint("SetTextI18n")
        override fun bindHolder(holder: CHolder, data: Timetable?, position: Int) {
            holder.binding.title.text = data?.name
            holder.binding.card.setOnClickListener {
                data?.let { it1 -> mOnItemClickListener?.onItemClick(it1, it, position) }
            }
            holder.binding.subtitle.text = TimeUtils.printDate(data?.startTime?.time)
            holder.binding.delete.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                PopUpText().setTitle(R.string.attention)
                    .setText(getString(R.string.dialog_message_delete_curriculum))
                    .setOnConfirmListener(object : PopUpText.OnConfirmListener {
                        override fun OnConfirm() {

                        }

                    }).show(supportFragmentManager, "hint")
            }
            data?.startTime?.time?.let {
                holder.binding.icon.setImageResource(
                    when(TimeUtils.getSeason(it)){
                        TimeUtils.SEASON.SPRING->R.drawable.ic_spring
                        TimeUtils.SEASON.SUMMER->R.drawable.ic_summer
                        TimeUtils.SEASON.AUTUMN->R.drawable.ic_autumn
                        else->R.drawable.ic_winter
                    }
                )
            }

        }
    }

    companion object {
        private const val CHOOSE_FILE_CODE = 0
    }

    override fun initViewBinding(): ActivityTimetableManagerBinding {
        return ActivityTimetableManagerBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<TimetableManagerViewModel> {
        return TimetableManagerViewModel::class.java
    }

}