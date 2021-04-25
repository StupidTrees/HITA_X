package com.stupidtree.hitax.ui.myprofile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stupidtree.hitax.R
import com.stupidtree.stupiduser.data.model.UserProfile
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.ui.theme.AppTheme
import com.stupidtree.hitax.ui.theme.AppBar
//
//@Preview
//@Composable
//fun test() {
//    AppBar(R.string.my_profile_title)
//}
//
//@Composable
//fun ProfilePage(viewModel: MyProfileViewModel) {
//    AppTheme {
//        Scaffold(topBar = {
//            AppBar(R.string.my_profile_title)
//        }) {
//            ProfileLayout(viewModel = viewModel)
//        }
//    }
//}
//
//@Composable
//fun ProfileLayout(viewModel: MyProfileViewModel) {
//    val profile: DataState<UserProfile> by viewModel.userProfileLiveData.observeAsState(
//        DataState(
//            DataState.STATE.NOT_LOGGED_IN
//        )
//    )
//    profile.data?.let {
//        Column(modifier = Modifier.padding(4.dp)) {
//            Row(modifier = Modifier.padding(8.dp)) {
//                Text(
//                    stringResource(R.string.username),
//                    fontSize = 16.sp,
//                    color = MaterialTheme.colors.onSecondary
//                )
//                Text(it.username ?: "", fontSize = 16.sp, color = MaterialTheme.colors.onPrimary)
//            }
//            Row(modifier = Modifier.padding(8.dp)) {
//                Text(
//                    stringResource(R.string.prompt_nickname),
//                    fontSize = 16.sp,
//                    color = MaterialTheme.colors.onSecondary
//                )
//                Text(it.nickname ?: "", fontSize = 16.sp, color = MaterialTheme.colors.primary)
//            }
//
//        }
//    }
//
//}
//
