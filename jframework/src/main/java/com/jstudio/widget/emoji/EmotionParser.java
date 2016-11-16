package com.jstudio.widget.emoji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.jstudio.R;
import com.jstudio.utils.BitmapUtils;
import com.jstudio.utils.SizeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jason
 */
public class EmotionParser {

    public static final String EMOTION_PATTERN = "\\[[\\u4e00-\\u9fa5a-zA-Z0-9]*\\]";
    public static EmotionParser INSTANCE;
    private static final String[] mEmotionsName = {
            "[嘻嘻]", "[哈哈]", "[喜欢]", "[晕]",
            "[泪]", "[馋嘴]", "[抓狂]", "[哼]",
            "[可爱]", "[怒]", "[汗]", "[微笑]",
            "[睡觉]", "[钱]", "[偷笑]", "[酷]",
            "[衰]", "[吃惊]", "[怒骂]", "[鄙视]",
            "[挖鼻屎]", "[色]", "[鼓掌]", "[悲伤]",
            "[思考]", "[生病]", "[亲亲]", "[抱抱]",
            "[白眼]", "[右哼哼]", "[左哼哼]", "[嘘]",
            "[委屈]", "[哈欠]", "[敲打]", "[疑问]",
            "[挤眼]", "[害羞]", "[快哭了]", "[拜拜]",

            "[黑线]", "[强]", "[弱]", "[给力]",
            "[浮云]", "[围观]", "[威武]", "[相机]",
            "[汽车]", "[飞机]", "[爱心]", "[奥特曼]",
            "[兔子]", "[熊猫]", "[不要]", "[ok]",
            "[赞]", "[勾引]", "[耶]", "[爱你]",
            "[拳头]", "[差劲]", "[握手]", "[玫瑰]",
            "[心]", "[伤心]", "[猪头]", "[咖啡]",
            "[麦克风]", "[月亮]", "[太阳]", "[啤酒]",
            "[萌]", "[礼物]", "[互粉]", "[钟]",
            "[自行车]", "[蛋糕]", "[围巾]", "[手套]",
            "[雪花]", "[雪人]", "[帽子]", "[树叶]"

    };
    private static final List<Integer> mEmotionsId = Arrays.asList(
            R.drawable.emotion_01, R.drawable.emotion_02, R.drawable.emotion_03, R.drawable.emotion_04,
            R.drawable.emotion_05, R.drawable.emotion_06, R.drawable.emotion_07, R.drawable.emotion_08,
            R.drawable.emotion_09, R.drawable.emotion_10, R.drawable.emotion_11, R.drawable.emotion_12,
            R.drawable.emotion_13, R.drawable.emotion_14, R.drawable.emotion_15, R.drawable.emotion_16,
            R.drawable.emotion_17, R.drawable.emotion_18, R.drawable.emotion_19, R.drawable.emotion_20,
            R.drawable.emotion_21, R.drawable.emotion_22, R.drawable.emotion_23, R.drawable.emotion_24,
            R.drawable.emotion_25, R.drawable.emotion_26, R.drawable.emotion_27, R.drawable.emotion_28,
            R.drawable.emotion_29, R.drawable.emotion_30, R.drawable.emotion_31, R.drawable.emotion_32,
            R.drawable.emotion_33, R.drawable.emotion_34, R.drawable.emotion_35, R.drawable.emotion_36,
            R.drawable.emotion_37, R.drawable.emotion_38, R.drawable.emotion_39, R.drawable.emotion_40,

            R.drawable.emotion_41, R.drawable.emotion_42, R.drawable.emotion_43, R.drawable.emotion_44,
            R.drawable.emotion_45, R.drawable.emotion_46, R.drawable.emotion_47, R.drawable.emotion_48,
            R.drawable.emotion_49, R.drawable.emotion_50, R.drawable.emotion_51, R.drawable.emotion_52,
            R.drawable.emotion_53, R.drawable.emotion_54, R.drawable.emotion_55, R.drawable.emotion_56,
            R.drawable.emotion_57, R.drawable.emotion_58, R.drawable.emotion_59, R.drawable.emotion_60,
            R.drawable.emotion_61, R.drawable.emotion_62, R.drawable.emotion_63, R.drawable.emotion_64,
            R.drawable.emotion_65, R.drawable.emotion_66, R.drawable.emotion_67, R.drawable.emotion_68,
            R.drawable.emotion_69, R.drawable.emotion_70, R.drawable.emotion_71, R.drawable.emotion_72,
            R.drawable.emotion_73, R.drawable.emotion_74, R.drawable.emotion_75, R.drawable.emotion_76,
            R.drawable.emotion_77, R.drawable.emotion_78, R.drawable.emotion_79, R.drawable.emotion_80,
            R.drawable.emotion_81, R.drawable.emotion_82, R.drawable.emotion_83, R.drawable.emotion_84
    );
    private static Map<String, Integer> mEmotionMapper;

    private EmotionParser() {
        mEmotionMapper = new HashMap<>(mEmotionsName.length);
        for (int i = 0; i < mEmotionsName.length; i++) {
            mEmotionMapper.put(mEmotionsName[i], mEmotionsId.get(i));
        }
    }

    public static EmotionParser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EmotionParser();
        }
        return INSTANCE;
    }

    public static synchronized SpannableString parseString(String string, final Context context) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        SpannableString spannableString = new SpannableString(string);
        if (string.contains("[")) {
            for (final Emoji emoji : getStructure(string, EMOTION_PATTERN)) {
                int resID = getDrawableID(emoji.getPhrase());
                Bitmap bitmap = null;
                if (resID != 0) {
                    bitmap = BitmapUtils.decodeFromDrawable(context, resID, SizeUtils.convertDp2Px(context, 25), SizeUtils.convertDp2Px(context, 25));
                }
                if (bitmap != null) {
                    spannableString.setSpan(new ImageSpan(context, bitmap), emoji.getStart(), emoji.getEnd(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannableString;
    }

    private static List<Emoji> getStructure(String string, String regularType) {
        Pattern pattern = Pattern.compile(regularType);
        Matcher matcher = pattern.matcher(string);
        List<Emoji> list = new ArrayList<>();
        while (matcher.find()) {
            Emoji emoji = new Emoji();
            emoji.setStart(matcher.start());
            emoji.setEnd(matcher.end());
            emoji.setPhrase(matcher.group());
            list.add(emoji);
        }
        return list;
    }

    public static List<Integer> getAllDrawableID() {
        return mEmotionsId;
    }

    public static int getDrawableID(int index) {
        return mEmotionsId.get(index);
    }

    public static String[] getAllEmotionPhrase() {
        return mEmotionsName;
    }

    public static String getEmotionPhrase(int index) {
        return mEmotionsName[index];
    }

    public static Drawable getDrawableByPhrase(Context context, String phrase) {
        if (null == mEmotionMapper) {
            return null;
        }
        return context.getResources().getDrawable(mEmotionMapper.get(phrase));
    }

    public static int getDrawableID(String phrase) {
        if (null == mEmotionMapper) {
            return 0;
        }
        if (mEmotionMapper.containsKey(phrase)) {
            return mEmotionMapper.get(phrase);
        } else {
            return 0;
        }
    }

}
