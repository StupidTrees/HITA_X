package com.stupidtree.hita.ui.timetable.subject

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.databinding.FragmentTimetableSubjetsBinding
import com.stupidtree.hita.ui.base.BaseListAdapter
import com.stupidtree.hita.ui.timetable.FragmentTimeTableChild
import com.stupidtree.hita.utils.ActivityUtils
import com.stupidtree.hita.utils.EditModeHelper
import java.util.*
import kotlin.Comparator

class SubjectsFragment : FragmentTimeTableChild<SubjectsViewModel, FragmentTimetableSubjetsBinding>(), EditModeHelper.EditableContainer<Pair<TermSubject, Float>> {
    private var subjectsAdapter: SubjectsListAdapter? = null
    private var teachersListAdapter: TeachersListAdapter? = null

    private var editModeHelper: EditModeHelper<Pair<TermSubject, Float>>? = null


//    fun onEditClosed() {}
//    fun onEditStarted() {}
//    fun onItemCheckedChanged(position: Int, checked: Boolean, currentSelected: Int) {}
//    fun onDelete(toDelete: Collection<*>) {
//        deleteSubjectTask(toDelete).execute()
//    }
//
//    fun onRefreshStart(id: String?, params: Array<Boolean?>?) {}
//    fun onListRefreshed(id: String?, params: Array<Boolean>, newList: List<Subject>?) {
//        if (isDetached() || isRemoving()) return
//        var anim = false
//        if (params.size > 0) anim = params[0]
//        subjectsList.setVisibility(View.VISIBLE)
//        editModeHelper.closeEditMode()
//        if (anim) {
//            listRes!!.clear()
//            listRes!!.addAll(newList!!)
//            subjectsAdapter.notifyDataSetChanged()
//            subjectsList.scheduleLayoutAnimation()
//        } else subjectsAdapter.notifyItemChangedSmooth(newList)
//    }
//

    override fun initViews(view: View) {
        binding?.usercenterSubjectsList?.setItemViewCacheSize(20)
        subjectsAdapter = root?.getTimetableSP()?.let { SubjectsListAdapter(requireContext(), mutableListOf(), it) }
        binding?.usercenterSubjectsList?.layoutManager = LinearLayoutManager(requireContext())
        binding?.usercenterSubjectsList?.adapter = subjectsAdapter

        teachersListAdapter = TeachersListAdapter(requireContext(), mutableListOf())
        binding?.teachersList?.adapter = teachersListAdapter
        binding?.teachersList?.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)


        subjectsAdapter?.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener<Pair<TermSubject, Float>> {

            override fun onItemClick(data: Pair<TermSubject, Float>, card: View?, position: Int) {
                ActivityUtils.startSubjectActivity(requireContext(),data.first.id)
                //                if (position == listRes!!.size) {
//                    FragmentAddEvent.newInstance().setInitialType("course").show(childFragmentManager, "ade")
//                } else if (!listRes!![position].getType().equals(Subject.TAG)) {
//                    ActivityUtils.startSubjectActivity_name(getActivity(), listRes!![position].getName())
//                }
            }
        })
        subjectsAdapter?.setOnItemLongClickListener(object : BaseListAdapter.OnItemLongClickListener<Pair<TermSubject, Float>> {

            override fun onItemLongClick(data: Pair<TermSubject, Float>, view: View?, position: Int): Boolean {
                if (data.first.type == TermSubject.TYPE.TAG) return false
                editModeHelper?.activateEditMode(position)
                return true
            }
        })
        subjectsAdapter?.let {
            editModeHelper = EditModeHelper(requireContext(), it, this)
        }
        editModeHelper?.init(view, R.id.edit_layout, R.layout.edit_mode_bar_3)
        editModeHelper?.smoothSwitch = true


        viewModel.subjectsLiveData.observe(this) {
            if (subjectsAdapter?.beans?.isNullOrEmpty() == true) {
                subjectsAdapter?.notifyDataSetChanged(it)
                binding?.usercenterSubjectsList?.scheduleLayoutAnimation()
            } else {
                subjectsAdapter?.notifyItemChangedSmooth(it, false, Comparator { o1, o2 ->
                    return@Comparator if (o1.second != o2.second) 1 else o1.first.name.compareTo(o2.first.name)
                })
            }
        }

        viewModel.teacherInfoLiveData.observe(this) {
            if (teachersListAdapter?.beans?.isNullOrEmpty() == true) {
                teachersListAdapter?.notifyDataSetChanged(it)
                binding?.teachersList?.scheduleLayoutAnimation()
            } else {
                teachersListAdapter?.notifyItemChangedSmooth(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    override fun initViewBinding(): FragmentTimetableSubjetsBinding {
        return FragmentTimetableSubjetsBinding.inflate(layoutInflater)
    }

//    internal class refreshListTask(listRefreshedListener: ListRefreshedListener?) : BasicRefreshTask<List<Subject?>?>(listRefreshedListener) {
//        protected fun doInBackground(listRefreshedListener: ListRefreshedListener?, vararg booleans: Boolean?): List<Subject> {
//            super.doInBackground(listRefreshedListener, booleans)
//            val newList: MutableList<Subject> = ArrayList<Subject>()
//            val tc: TimetableCore = TimetableCore.getInstance(HContext)
//            if (!tc.isDataAvailable()) return newList
//            val all: List<Subject> = tc.getSubjects(null)
//            //tc.getAllEvents();
//            val exam: MutableList<Subject> = ArrayList<Subject>()
//            val other: MutableList<Subject> = ArrayList<Subject>()
//            val mooc: MutableList<Subject> = ArrayList<Subject>()
//            for (s in all) {
//                if (s.isExam()) exam.add(s) else if (s.isMOOC()) mooc.add(s) else other.add(s)
//            }
//            if (exam.size > 0) newList.add(Subject.getTagInstance(HContext.getString(R.string.counted_in_GPA)))
//            newList.addAll(exam)
//            if (other.size > 0) newList.add(Subject.getTagInstance(HContext.getString(R.string.not_counted_in_GPA)))
//            newList.addAll(other)
//            if (mooc.size > 0) newList.add(Subject.getTagInstance("MOOC"))
//            newList.addAll(mooc)
//            return newList
//        }
//    }
//
//    internal inner class deleteSubjectTask(toDelete: Collection<Subject>) : AsyncTask<Any?, Any?, Any?>() {
//        var toDelete: Collection<Subject>
//        protected override fun doInBackground(objects: Array<Any>): Any {
//            try {
//                for (s in toDelete) {
//                    TimetableCore.getInstance(HContext).deleteSubject(s.getName(), TimetableCore.getInstance(HContext).getCurrentCurriculumId())
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                return false
//            }
//            return true
//        }
//
//        protected override fun onPostExecute(o: Any) {
//            super.onPostExecute(o)
//            if (o as Boolean) {
//                Toast.makeText(requireContext(), R.string.delete_success, Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(requireContext(), R.string.delete_failed, Toast.LENGTH_SHORT).show()
//            }
//            val i = Intent(TIMETABLE_CHANGED)
//            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(i)
//        }
//
//        init {
//            this.toDelete = toDelete
//        }
//    }


    override fun getViewModelClass(): Class<SubjectsViewModel> {
        return SubjectsViewModel::class.java
    }

    override fun onEditClosed() {
        binding?.titleSubject?.visibility = View.VISIBLE
    }

    override fun onEditStarted() {
        binding?.titleSubject?.visibility = View.GONE
    }

    override fun onItemCheckedChanged(position: Int, checked: Boolean, currentSelected: Int) {

    }

    override fun onDelete(toDelete: Collection<Pair<TermSubject, Float>>?) {
    }

    override fun refresh() {
        root?.getCurriculum()?.let {
            viewModel.startRefresh(it.id)
        }
    }
}