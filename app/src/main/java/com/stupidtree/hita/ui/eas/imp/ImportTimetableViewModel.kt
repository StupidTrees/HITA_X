package com.stupidtree.hita.ui.eas.imp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.repository.EASRepository
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.base.Trigger
import java.util.*

class ImportTimetableViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * 仓库区
     */
    private val easRepository = EASRepository.getInstance(application)

    /**
     * LiveData区
     */

    private val termsController: MutableLiveData<Trigger> = MutableLiveData()
    val termsLiveData: LiveData<DataState<List<TermItem>>> =
        Transformations.switchMap(termsController) {
            return@switchMap easRepository.getAllTerms()
        }


    val selectedTermLiveData: MutableLiveData<TermItem?> = MutableLiveData()

    val startDateLiveData: LiveData<DataState<Calendar>> =
        Transformations.switchMap(selectedTermLiveData) {
            Log.e("select_changed", it.toString())
            it?.let { it1 ->
                return@switchMap easRepository.getStartDateOfTerm(it1)
            }
            val r = MutableLiveData<DataState<Calendar>>()
            r.value = DataState(Calendar.getInstance())
            return@switchMap r
        }


    /**
     * 方法区
     */


    fun startRefreshTerms() {
        termsController.value = Trigger.actioning
    }

    fun changeSelectedTerm(termItem: TermItem) {
        selectedTermLiveData.value = termItem
    }

    fun getAllTerms(): List<TermItem> {
        if (termsLiveData.value != null && termsLiveData.value!!.data != null) {
            return termsLiveData.value!!.data!!
        }
        return listOf()
    }
}