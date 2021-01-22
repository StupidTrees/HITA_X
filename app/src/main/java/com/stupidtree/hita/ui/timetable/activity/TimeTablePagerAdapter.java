//package com.stupidtree.hita.ui.timetable.activity;
//
//import android.content.Context;
//import android.util.SparseArray;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//
//import com.stupidtree.hita.R;
//import com.stupidtree.hita.fragments.FragmentTimeTablePage;
//import com.stupidtree.hita.ui.base.BaseTabAdapter;
//import com.stupidtree.hita.ui.timetable.fragment.TimetablePageFragment;
//
//import java.util.List;
//
//public class TimeTablePagerAdapter extends BaseTabAdapter {
//    private FragmentManager mFragmentManager;
//    //保存每个Fragment的Tag，刷新页面的依据
//    protected SparseArray<String> tags = new SparseArray<>();
//    private Context context;
//
//    public TimeTablePagerAdapter(Context context, FragmentManager fm, int count) {
//        super(fm, count);
//        this.context = context;
//        mFragmentManager = fm;
//    }
//
//
//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return String.format(context.getString(R.string.timetable_tab_title), position + 1);
//    }
//
//
////    @Override
////    public Fragment getItem(int i) {
////        return getFragmentByPosition(i);
////    }
//
//    @Override
//    protected Fragment initItem(int position) {
//        return TimetablePageFragment.newInstance(position + 1);
//    }
//
//
////    @Override
////    public int getItemPosition(Object object) {
////        Fragment fragment = (Fragment) object;
////        if (tags.indexOfValue(fragment.getTag()) > -1) {
////            return super.getItemPosition(object);
////        }
////        return POSITION_NONE;
////    }
//
//
//    @Override
//    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
//        tags.remove(position);
//    }
//
//
//    @NonNull
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        //得到缓存的fragment
//        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        String tag = fragment.getTag();
//        //保存每个Fragment的Tag
//        tags.put(position, tag);
//        return fragment;
//    }
//
//
//    //拿到指定位置的Fragment
////    private Fragment getFragmentByPosition(int position) {
////        if (tags.get(position) != null)
////            return mFragmentManager.findFragmentByTag(tags.get(position));
////        else{
////            return  new FragmentTimeTablePage();
////        }
////    }
//
//
//    public List<Fragment> getFragments(){
//        return mFragmentManager.getFragments();
//    }
//
//
//
//    //刷新全部Fragment
//    public void notifyAllFragments() {
//        for (Fragment d : getFragments()) {
//            if (d instanceof TimetablePageFragment) ((TimetablePageFragment) d).NotifyRefresh();
//        }
//    }
//
//
//
//
//
//}
