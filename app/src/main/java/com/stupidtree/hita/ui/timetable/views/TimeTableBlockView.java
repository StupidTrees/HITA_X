//package com.stupidtree.hita.ui.timetable.views;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.res.ColorStateList;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import com.stupidtree.hita.R;
//import com.stupidtree.hita.data.model.timetable.EventItem;
//
//import java.util.List;
//
//
//@SuppressLint("ViewConstructor")
//public class TimeTableBlockView extends FrameLayout {
//    Object block;
//    View card;
//    TextView title;
//    TextView subtitle;
//    ImageView icon;
//    OnCardClickListener onCardClickListener;
//    OnCardLongClickListener onCardLongClickListener;
//    OnDuplicateCardClickListener onDuplicateCardClickListener;
//    OnDuplicateCardLongClickListener onDuplicateCardLongClickListener;
//    TimeTablePreferenceRoot root;
//
//    public interface OnCardClickListener {
//        void OnClick(View v, EventItem ei);
//    }
//
//    public interface OnCardLongClickListener {
//        boolean OnLongClick(View v, EventItem ei);
//    }
//
//    public interface OnDuplicateCardClickListener {
//        void OnDuplicateClick(View v, List<EventItem> list);
//    }
//
//    @SuppressLint("ResourceAsColor")
//    public TimeTableBlockView(@NonNull Context context, @NonNull Object obj, TimeTablePreferenceRoot root) {
//
//        super(context);
//        this.block = obj;
//        this.root = root;
//
//    }
//
//    private void initEventCard(Context context) {
//        final EventItem ei = (EventItem) block;
//        inflate(context, R.layout.fragment_timetable_class_card, this);
//        card = findViewById(R.id.card);
//        title = findViewById(R.id.title);
//        subtitle = findViewById(R.id.subtitle);
//        icon = findViewById(R.id.icon);
//        int subjectColor = 0;
//        if (root.isColorEnabled()
//        ) {
//            String query;
//            if (ei.eventType == TimetableCore.EXAM && ei.getName().endsWith("考试"))
//                query = ei.getName().substring(0, ei.getName().length() - 2);
//            else query = ei.getName();
//            int getC = ColorBox.getSubjectColor(root.getTTPreference(), query);
//            card.setBackgroundTintList(ColorStateList.valueOf(getC));
//            subjectColor = getC;
//        } else {
//            if (root.getCardBackground().equals("primary")) {
//                card.setBackgroundTintList(ColorStateList.valueOf(root.getColorPrimary()));
//            } else if (root.getCardBackground().equals("accent")) {
//                card.setBackgroundTintList(ColorStateList.valueOf(root.getColorAccent()));
//            }
//
//        }
//        switch (root.getCardTitleColor()) {
//            case "subject":
//                if (root.isColorEnabled()) {
//                    if (title != null) title.setTextColor(subjectColor);
//                } else if (title != null) title.setTextColor(root.getColorPrimary());
//                break;
//            case "white":
//                if (title != null) title.setTextColor(Color.WHITE);
//                break;
//            case "black":
//                if (title != null) title.setTextColor(Color.BLACK);
//                break;
//            case "primary":
//                if (title != null) title.setTextColor(root.getColorPrimary());
//                break;
//            case "accent":
//                if (title != null) title.setTextColor(root.getColorAccent());
//                break;
//        }
//        switch (root.getSubTitleColor()) {
//            case "subject":
//                if (root.isColorEnabled()) {
//                    if (subtitle != null) subtitle.setTextColor(subjectColor);
//                } else if (subtitle != null) subtitle.setTextColor(root.getColorPrimary());
//                break;
//            case "white":
//                if (subtitle != null) subtitle.setTextColor(Color.WHITE);
//                break;
//            case "black":
//                if (subtitle != null) subtitle.setTextColor(Color.BLACK);
//                break;
//            case "primary":
//                if (subtitle != null) subtitle.setTextColor(root.getColorPrimary());
//                break;
//            case "accent":
//                if (subtitle != null) subtitle.setTextColor(root.getColorAccent());
//                break;
//        }
//        if (icon != null) {
//            if (root.cardIconEnabled()) {
//                icon.setVisibility(VISIBLE);
//                icon.setColorFilter(Color.WHITE);
//                switch (root.getIconColor()) {
//                    case "subject":
//                        if (root.isColorEnabled()) {
//                            if (icon != null) icon.setColorFilter(subjectColor);
//                        } else if (icon != null) icon.setColorFilter(root.getColorPrimary());
//                        break;
//                    case "white":
//                        if (icon != null) icon.setColorFilter(Color.WHITE);
//                        break;
//                    case "black":
//                        if (icon != null) icon.setColorFilter(Color.BLACK);
//                        break;
//                    case "primary":
//                        if (icon != null) icon.setColorFilter(root.getColorPrimary());
//                        break;
//                    case "accent":
//                        if (icon != null) icon.setColorFilter(root.getColorAccent());
//                        break;
//                }
//            } else {
//                icon.setVisibility(GONE);
//            }
//        }
//
//        card.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onCardClickListener != null) onCardClickListener.OnClick(v, ei);
//            }
//        });
//        card.setOnLongClickListener(v -> {
//            if (onCardLongClickListener != null) {
//                return onCardLongClickListener.OnLongClick(v, ei);
//            } else return false;
//        });
//        if (title != null) title.setText(ei.getName());
//        if (subtitle != null) subtitle.setText(TextUtils.isEmpty(ei.tag2) ? "" : ei.tag2);
//        card.getBackground().mutate().setAlpha((int) (255 * ((float) root.getCardOpacity() / 100)));
//        if (root.willBoldText()) {
//            title.setTypeface(Typeface.DEFAULT_BOLD);
//            if (subtitle != null) subtitle.setTypeface(Typeface.DEFAULT_BOLD);
//        }
//        if (title != null) title.setAlpha((float) root.getTitleAlpha() / 100);
//        if (subtitle != null) subtitle.setAlpha((float) root.getSubtitleAlpha() / 100);
//        if (title != null) title.setGravity(root.getTitleGravity());
//    }
//
//    public void setOnDuplicateCardClickListener(OnDuplicateCardClickListener onDuplicateCardClickListener) {
//        this.onDuplicateCardClickListener = onDuplicateCardClickListener;
//    }
//
//    public void setOnCardLongClickListener(OnCardLongClickListener onCardLongClickListener) {
//        this.onCardLongClickListener = onCardLongClickListener;
//    }
//
//    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
//        this.onCardClickListener = onCardClickListener;
//
//    }
//
//    public void setOnDuplicateCardLongClickListener(OnDuplicateCardLongClickListener onDuplicateCardLongClickListener) {
//        this.onDuplicateCardLongClickListener = onDuplicateCardLongClickListener;
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (block instanceof EventItem) {
//            initEventCard(getContext());
//        } else if (block instanceof List) {
//            initDuplicateCard(getContext());
//        }
//    }
//
//    private void initDuplicateCard(Context context) {
//        final List<EventItem> list = (List<EventItem>) block;
//        inflate(context, R.layout.fragment_timetable_duplicate_card, this);
//        card = findViewById(R.id.card);
//        title = findViewById(R.id.title);
//        icon = findViewById(R.id.icon);
//        StringBuilder sb = new StringBuilder();
//        for (EventItem ei : list) sb.append(ei.getName()).append(";\n");
//        title.setText(sb.toString());
//        if (onDuplicateCardClickListener != null) card.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onDuplicateCardClickListener.OnDuplicateClick(v, list);
//            }
//        });
//        if (onDuplicateCardLongClickListener != null)
//            card.setOnLongClickListener(v -> onDuplicateCardLongClickListener.OnDuplicateLongClick(v, list));
//
//        EventItem mainItem = list.get(0);
//        int subjectColor = -1;
//        if (root.isColorEnabled() && mainItem != null) {
//            String query;
//            if (mainItem.eventType == TimetableCore.EXAM && mainItem.getName().endsWith("考试"))
//                query = mainItem.getMainName().substring(0, mainItem.getMainName().length() - 2);
//            else query = mainItem.getMainName();
//            subjectColor = ColorBox.getSubjectColor(root.getTTPreference(), query);
//            card.setBackgroundTintList(ColorStateList.valueOf(subjectColor));
//            switch (root.getCardTitleColor()) {
//                case "subject":
//                    title.setTextColor(subjectColor);
//                    break;
//                case "white":
//                    title.setTextColor(Color.WHITE);
//                    break;
//                case "black":
//                    title.setTextColor(Color.BLACK);
//                    break;
//                case "primary":
//                    title.setTextColor(root.getColorPrimary());
//                    break;
//                case "accent":
//                    title.setTextColor(root.getColorAccent());
//                    break;
//            }
//        } else {
//            if (root.getCardBackground().equals("primary")) {
//                card.setBackgroundTintList(ColorStateList.valueOf(root.getColorPrimary()));
//            } else if (root.getCardBackground().equals("accent")) {
//                card.setBackgroundTintList(ColorStateList.valueOf(root.getColorAccent()));
//            }
//            switch (root.getCardTitleColor()) {
//                case "white":
//                    title.setTextColor(Color.WHITE);
//                    break;
//                case "black":
//                    title.setTextColor(Color.BLACK);
//                    break;
//                case "primary":
//                    title.setTextColor(root.getColorPrimary());
//                    break;
//                case "accent":
//                    title.setTextColor(root.getColorAccent());
//                    break;
//            }
//        }
//        if (icon != null) {
//            if (root.cardIconEnabled()) {
//                icon.setVisibility(VISIBLE);
//                icon.setColorFilter(Color.WHITE);
//                switch (root.getIconColor()) {
//                    case "subject":
//                        if (root.isColorEnabled()) {
//                            icon.setColorFilter(subjectColor);
//                        } else icon.setColorFilter(root.getColorPrimary());
//                        break;
//                    case "white":
//                        icon.setColorFilter(Color.WHITE);
//                        break;
//                    case "black":
//                        icon.setColorFilter(Color.BLACK);
//                        break;
//                    case "primary":
//                        icon.setColorFilter(root.getColorPrimary());
//                        break;
//                    case "accent":
//                        icon.setColorFilter(root.getColorAccent());
//                        break;
//                }
//            } else {
//                icon.setVisibility(GONE);
//            }
//        }
//        card.getBackground().mutate().setAlpha((int) (255 * ((float) root.getCardOpacity() / 100)));
//        if (root.willBoldText()) {
//            title.setTypeface(Typeface.DEFAULT_BOLD);
//        }
//        title.setAlpha((float) root.getTitleAlpha() / 100);
//        title.setGravity(root.getTitleGravity());
//    }
//
//    public interface OnDuplicateCardLongClickListener {
//        boolean OnDuplicateLongClick(View v, List<EventItem> list);
//    }
//
//    public interface TimeTablePreferenceRoot {
//        //        int locateFragment(FragmentTimeTablePage page);
//        boolean isColorEnabled();
//
//        String getCardTitleColor();
//
//        String getSubTitleColor();
//
//        String getIconColor();
//
//        boolean willBoldText();
//
//        boolean drawBGLine();
//
//        boolean cardIconEnabled();
//
//        int getCardOpacity();
//
//        int getCardHeight();
//
//        HTime getStartTime();
//
//        int getTodayBGColor();
//
//        int getTitleGravity();
//
//        int getColorPrimary();
//
//        int getColorAccent();
//
//        int getTitleAlpha();
//
//        int getSubtitleAlpha();
//
//        boolean animEnabled();
//
//        String getCardBackground();
//
//        SharedPreferences getTTPreference();
//
//        boolean drawNowLine();
//    }
//
//    public int getDow() {
//        if (block instanceof EventItem) {
//            return ((EventItem) block).DOW;
//        } else if (block instanceof List) {
//            return ((List<EventItem>) block).get(0).DOW;
//        }
//        return -1;
//    }
//
//    public int getDuration() {
//        if (block instanceof EventItem) {
//            return ((EventItem) block).getDuration();
//        } else if (block instanceof List) {
//            return ((List<EventItem>) block).get(0).getDuration();
//        }
//        return -1;
//    }
//
//    public EventItem getEvent() {
//        if (block instanceof EventItem) {
//            return ((EventItem) block);
//        } else if (block instanceof List) {
//            return ((List<EventItem>) block).get(0);
//        }
//        return null;
//    }
//
//
//}
