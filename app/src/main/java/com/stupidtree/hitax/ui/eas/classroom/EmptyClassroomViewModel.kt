package com.stupidtree.hitax.ui.eas.classroom

import android.app.Application
import androidx.lifecycle.*
import com.stupidtree.hitax.data.model.eas.TermItem
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.data.repository.EASRepository
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.component.data.DataState
import com.stupidtree.component.data.Trigger
import com.stupidtree.hitax.ui.eas.EASViewModel
import com.stupidtree.hitax.utils.MTransformations

class EmptyClassroomViewModel(application: Application) : EASViewModel(application) {
    /**
     * 仓库区
     */
    private val easRepository = EASRepository.getInstance(application)
    private val timetableRepository = TimetableRepository.getInstance(application)


    private val pageController = MutableLiveData<Trigger>()
    val termsLiveData: LiveData<DataState<List<TermItem>>> =
        Transformations.switchMap(pageController) {
            return@switchMap easRepository.getAllTerms()
        }
    val buildingsLiveData: LiveData<DataState<List<BuildingItem>>> =
        Transformations.switchMap(pageController) {
            return@switchMap easRepository.getTeachingBuildings()
        }
    val selectedTermLiveData = MutableLiveData<TermItem>()
    val selectedBuildingLiveData = MutableLiveData<BuildingItem>()
    val selectedWeekLiveData: MediatorLiveData<Int> =
        MTransformations.switchMap(selectedTermLiveData) { term ->
            return@switchMap timetableRepository.getCurrentWeekOfTimetable(term)
        }
    val timetableStructureLiveData: LiveData<DataState<MutableList<TimePeriodInDay>>> = Transformations.switchMap(selectedTermLiveData) {
        return@switchMap easRepository.getScheduleStructure(it)
    }
    val classroomLiveData: MediatorLiveData<DataState<List<ClassroomItem>>> =
        MTransformations.switchMap(timetableStructureLiveData) {
            val res = MediatorLiveData<DataState<List<ClassroomItem>>>()
            res.addSource(selectedWeekLiveData) { week ->
                selectedTermLiveData.value?.let { term ->
                    selectedBuildingLiveData.value?.let { building ->
                        res.addSource(easRepository.queryEmptyClassroom(term, building, week)) {
                            res.value = it
                        }
                    }
                }
            }
            res.addSource(selectedBuildingLiveData) { building ->
                selectedTermLiveData.value?.let { term ->
                    res.addSource(
                        easRepository.queryEmptyClassroom(
                            term,
                            building,
                            selectedWeekLiveData.value ?: 1
                        )
                    ) {
                        res.value = it
                    }
                }
            }
            return@switchMap res
        }


    fun startRefresh() {
        pageController.value = Trigger.actioning
    }
}