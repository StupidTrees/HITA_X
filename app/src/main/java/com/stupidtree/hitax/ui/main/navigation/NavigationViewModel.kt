package com.stupidtree.hitax.ui.main.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.component.data.Trigger
import com.stupidtree.hita.theta.data.repository.MessageRepository
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class NavigationViewModel(application: Application) : AndroidViewModel(application) {

    private val timetableRepository = TimetableRepository.getInstance(application)
    private val localUserRepository = LocalUserRepository.getInstance(application)
    private val messageRepo = MessageRepository.getInstance(application)

    private val recentTimetableController = MutableLiveData<Trigger>()
    val recentTimetableLiveData: LiveData<Timetable?> =
        Transformations.switchMap(recentTimetableController) {
            return@switchMap timetableRepository.getRecentTimetable()
        }
    val timetableCountLiveData: LiveData<Int> =
        Transformations.switchMap(recentTimetableController) {
            return@switchMap timetableRepository.getTimetableCount()
        }


    val unreadMessageLiveData: LiveData<DataState<Int>> =
        Transformations.switchMap(recentTimetableController) {
            val lu = localUserRepository.getLoggedInUser()
            if (lu.isValid()) {
                return@switchMap messageRepo.countUnread(lu.token!!, "all")
            } else {
                return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
            }
        }

    fun startRefresh() {
        recentTimetableController.value = Trigger.actioning
    }
}