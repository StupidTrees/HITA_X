package com.stupidtree.hitax.ui.event

import android.content.Context
import android.os.Bundle
import android.view.View
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.model.timetable.TimeInDay
import com.stupidtree.hitax.databinding.DialogBottomTimetableClassBinding
import com.stupidtree.style.base.BaseFragment
import com.stupidtree.hitax.ui.subject.SubjectActivity
import com.stupidtree.hitax.utils.ActivityUtils
import com.stupidtree.hitax.utils.TimeTools
import com.stupidtree.style.widgets.PopUpText
import java.util.*

class EventItemFragment : BaseFragment<EventItemViewModel, DialogBottomTimetableClassBinding>() {

    interface EventParent {
        fun callDismiss()
    }
//
//    private override fun initViews(dlgView: View) {
//        courseProgressBar.setMax(100)
//        ratingBar.setStepSize(0.5f)

//        val delete = dlgView.findViewById<View>(R.id.delete)
//        delete.setOnClickListener { v ->
//            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
//            val ad = android.app.AlertDialog.Builder(requireContext())
//                .setNegativeButton(getString(R.string.button_cancel), null)
//                .setPositiveButton(getString(R.string.button_confirm)) { d, which -> deleteEvent() }
//                .create()
//            ad.setTitle(getString(R.string.dialog_title_sure_delete))
//            ad.show()
//        }
//    }

    var parent: EventParent? = null
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is EventParent) {
//            parent = context
//        }
//    }

    private fun setInfo(eventItem: EventItem) {
        binding?.teacher?.text = getString(eventItem.teacher)
        binding?.place?.text = getString(eventItem.place)
        binding?.time?.text = getString(
            R.string.event_duration_text,
            TimeInDay(eventItem.from).toString(),
            TimeInDay(eventItem.to).toString()
        )
        binding?.ttDlgName?.text = getString(eventItem.name)
        binding?.timeNumber?.text =
            (eventItem.fromNumber until eventItem.fromNumber + eventItem.lastNumber).joinToString(
                separator = ", "
            )
        binding?.ttDlgValue3Detail?.setOnClickListener {
            viewModel.eventItemLiveData.value?.let {
                ActivityUtils.searchFor(
                    requireContext(),
                    it.teacher,
                    ActivityUtils.SearchType.TEACHER
                )
            }

        }
//        if (TextUtils.isEmpty(eventItem.tag2)) {
//            classroom_detail_icon!!.visibility = View.GONE
//        } else {
//            classroom_detail_icon!!.visibility = View.VISIBLE
//            classroom_detail!!.setOnClickListener(View.OnClickListener {
//                if (TextUtils.isEmpty(eventItem.tag2) || eventItem.tag2 == "无地点") return@OnClickListener
//                val cr: Array<String> = eventItem.tag2.split("，\\[")
//                val classRooms = ArrayList(Arrays.asList(*cr))
//                if (classRooms.size > 1) {
//                    val toRemove = ArrayList<String>()
//                    for (i in classRooms.indices) {
//                        classRooms[i] = classRooms[i].substring(classRooms[i].lastIndexOf("周") + 1)
//                    }
//                    for (x in classRooms) {
//                        if (TextUtils.isEmpty(x)) toRemove.add(x)
//                    }
//                    classRooms.removeAll(toRemove)
//                    val classRoomItems = arrayOfNulls<String>(classRooms.size)
//                    for (i in classRoomItems.indices) classRoomItems[i] = classRooms[i]
//                    val ad: AlertDialog = AlertDialog.Builder(requireContext())
//                        .setTitle(HContext.getString(R.string.pick_classroom)).setItems(
//                            classRoomItems,
//                            DialogInterface.OnClickListener { dialogInterface, i ->
//                                ActivityUtils.searchFor(requireContext(), classRooms[i], "location")
//                                //ActivityUtils.startLocationActivity_name(requireContext(), classRooms.get(i));
//                            }).create()
//                    ad.show()
//                } else ActivityUtils.searchFor(requireContext(), eventItem.tag2, "location")
//                //                    Intent i = new Intent(a,ActivityExplore.class);
////                    i.putExtra("terminal",eventItem.tag2);
////                    a.startActivity(i);
//            })
//        }

        val c = Calendar.getInstance()
        c.timeInMillis = eventItem.from.time
        binding?.date?.text =
            TimeTools.getDateString(requireContext(), c, false, TimeTools.TTY_FOLLOWING)
    }


    fun getString(str: String?): String {
        if (str.isNullOrEmpty()) return getString(R.string.none)
        return str
    }

//
//    fun onOperationStart(id: String?, params: Array<Boolean?>?) {
//        ratingBar!!.visibility = View.INVISIBLE
//        courseProgress!!.text = "..."
//    }
//
//    fun onOperationDone(
//        id: String?,
//        task: BaseOperationTask<Map<String?, Int?>?>?,
//        params: Array<Boolean?>?,
//        res: Map<String?, Int?>?
//    ) {
//        if (null == res) {
//            popupRoot.callDismiss()
//            return
//        }
//        try {
//            ratingBar!!.visibility = View.VISIBLE
//            courseNumber = Objects.requireNonNull(res["now"])
//            courseProgress!!.text = java.lang.String.format(
//                HContext.getString(R.string.dialog_this_course_p),
//                courseNumber
//            )
//            val all = Objects.requireNonNull(res["total"]).toFloat()
//            val has = Objects.requireNonNull(res["now"])
//            val progress = has.toFloat() / all
//            val va = ValueAnimator.ofInt(0, (progress * 100).toInt())
//            va.duration = 700
//            va.interpolator = DecelerateInterpolator()
//            va.addUpdateListener { animation ->
//                val value = animation.animatedValue as Int
//                courseProgressBar!!.progress = value
//            }
//            va.start()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//
//    internal class RefreshTask(listRefreshedListener: OperationListener?, eventItem: EventItem) :
//        BaseOperationTask<Map<String?, Int?>?>(listRefreshedListener) {
//        //  double rate = 0;
//        var eventItem: EventItem
//        protected fun doInBackground(
//            listRefreshedListener: OperationListener?,
//            vararg booleans: Boolean?
//        ): Map<String, Int>? {
//            val res: MutableMap<String, Int> = HashMap()
//            try {
//                val subject: Subject =
//                    TimetableCore.getInstance(HContext).getSubjectByCourse(eventItem)
//                val courses: List<*> = TimetableCore.getInstance(HContext).getCourses(subject)
//                res["total"] = courses.size
//                Collections.sort<Comparable<*>>(courses)
//                val now = courses.indexOf(eventItem) + 1
//                res["now"] = now
//                //rate = TimetableCore.getInstance(HContext).getCurrentCurriculum().getSubjectByCourse(eventItem).getRate(courseNumber);
//            } catch (e: Exception) {
//                e.printStackTrace()
//                return null
//            }
//            return res
//        }
//
//        init {
//            this.eventItem = eventItem
//        }
//    }

    override fun getViewModelClass(): Class<EventItemViewModel> {
        return EventItemViewModel::class.java
    }

    override fun initViewBinding(): DialogBottomTimetableClassBinding {
        return DialogBottomTimetableClassBinding.inflate(layoutInflater)
    }

    override fun initViews(view: View) {
        arguments?.let {
            viewModel.eventItemLiveData.value = it["event"] as EventItem?
        }

        viewModel.eventItemLiveData.observe(this) {
            setInfo(it)
        }
        viewModel.progressLiveData.observe(this) {
            binding?.courseProgress?.progress =
                (((it.first + 1).toFloat() / it.second.toFloat()) * 100).toInt()
            binding?.courseCourseInSubject?.text =
                getString(R.string.dialog_this_course_p, it.first + 1)
        }


        binding?.subject?.setOnClickListener(View.OnClickListener {
            if (requireContext() !is SubjectActivity) {
                viewModel.eventItemLiveData.value?.let {
                    ActivityUtils.startSubjectActivity(requireContext(), it.subjectId)
                }
            }
        })
        binding?.nameLayout?.setOnClickListener { binding?.subject?.callOnClick() }
        binding?.delete?.setOnClickListener {
            PopUpText().setTitle(R.string.dialog_title_sure_delete)
                .setOnConfirmListener(object : PopUpText.OnConfirmListener {
                    override fun OnConfirm() {
                        viewModel.delete()
                        parent?.callDismiss()
                    }

                }).show(childFragmentManager, "sure")
        }
    }

    companion object {

        fun newInstance(eventItem: EventItem,parent:EventParent): EventItemFragment {
            val res = EventItemFragment()
            val b = Bundle()
            b.putSerializable("event", eventItem)
            res.arguments = b
            res.parent = parent
            return res
        }
    }
}