package com.stupidtree.hitax.ui.news.lecture

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.stupidtree.hitax.databinding.DynamicLectureCardBinding
import com.stupidtree.hitax.utils.TimeTools
import com.stupidtree.hitax.utils.TimeTools.TTY_REPLACE
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder

class LectureListAdapter(mContext: Context, mBeans: MutableList<Map<String, String>>) :
    BaseListAdapter<Map<String, String>, LectureListAdapter.LHolder>(
        mContext, mBeans
    ) {

    class LHolder(viewBinding: DynamicLectureCardBinding) :
        BaseViewHolder<DynamicLectureCardBinding>(
            viewBinding
        )

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return DynamicLectureCardBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): LHolder {
        return LHolder(viewBinding as DynamicLectureCardBinding)
    }

    override fun bindHolder(holder: LHolder, data: Map<String, String>?, position: Int) {
        data?.let { dt ->
            holder.binding.lectureTitle
            if (dt["title"].isNullOrEmpty()) {
                holder.binding.lectureTitle.visibility = View.GONE
            } else {
                holder.binding.lectureTitle.visibility = View.VISIBLE
                holder.binding.lectureTitle.text = dt["title"]
            }
            if (dt["place"].isNullOrEmpty()) {
                holder.binding.lecturePlace.visibility = View.GONE
            } else {
                holder.binding.lecturePlace.visibility = View.VISIBLE
                holder.binding.lecturePlace.text = dt["place"]
            }
            if (dt["time"].isNullOrEmpty()) {
                holder.binding.lectureTime.visibility = View.GONE
            } else {
                holder.binding.lectureTime.visibility = View.VISIBLE
                holder.binding.lectureTime.text = dt["time"]
            }
            holder.binding.time.text =  if(dt["date"]?.startsWith("!") == true){
                val ts = dt["date"]?.substring(1)?.toLong()?:0
                TimeTools.getDateString(mContext,ts,true, TTYMode = TTY_REPLACE)
            }else{
                dt["date"]?.substring(1)?:""
            }
            if (!dt["picture"].isNullOrEmpty()) {
                holder.binding.lecturePicture.visibility = View.VISIBLE
                Glide.with(mContext).load(dt["picture"]).centerCrop()
                    .into(holder.binding.lecturePicture)
            } else {
                holder.binding.lecturePicture.visibility = View.GONE
            }

            holder.binding.lectureCard.setOnClickListener {
                mOnItemClickListener?.onItemClick(dt, it, position)
            }

        }


    }
}