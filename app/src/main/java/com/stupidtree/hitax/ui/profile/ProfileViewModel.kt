package com.stupidtree.hitax.ui.profile

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.stupidtree.component.data.DataState
import com.stupidtree.component.data.StringTrigger
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.stupiduser.data.repository.ProfileRepository
import java.util.*

/**
 * 层次：ViewModel
 * 其他用户（好友、搜索结果等）的资料页面绑定的ViewModel
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * 仓库区
     */
    //用户资料仓库
    private val repository: ProfileRepository = ProfileRepository.getInstance(application)

    //本地用户仓库
    private val localUserRepository: LocalUserRepository =
        LocalUserRepository.getInstance(application)


    /**
     * 数据区
     */

    //Trigger：控制↑三个的刷新
    private var profileController = MutableLiveData<StringTrigger>()

    //从用户资料仓库中拉取数据
    var userProfileLiveData =  profileController.switchMap{ input ->
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            //从用户资料仓库中拉取数据
            return@switchMap repository.getUserProfile(input.data, user.token!!)
        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }

    private var followController = MutableLiveData<Pair<String,Boolean>>()

    val followResult =  followController.switchMap{
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            //从用户资料仓库中拉取数据
            return@switchMap repository.follow(user.token!!,it.first,it.second)
        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }


    /**
     * 开始页面刷新
     *
     * @param id 这个页面是谁的资料
     */
    fun startRefresh(id: String) {
        profileController.value = StringTrigger.getActioning(id)
    }

    fun startFollow(follow:Boolean){
        userProfileLiveData.value?.data?.let {
            followController.value = Pair(it.id,follow)
        }
    }

    fun logout(context: Context) {
        localUserRepository.logout(context)
    }


}