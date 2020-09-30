package com.rine.atfunction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * 类型的文本框
 * @author rine
 * @date:2020/9/30
 */
public class ContentEditText  extends AppCompatEditText {

    public static final int CODE_PERSON = 0x05;
    public static final int CODE_PERSON2 = 0x06;
    public static final String KEY_CID = "key_id";
    public static final String KEY_NAME = "key_name";

    public ContentEditText(Context context) {
        super(context);
        setFilters(new InputFilter[]{new MyInputFilter()});
    }

    public ContentEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFilters(new InputFilter[]{new MyInputFilter()});

    }

    public ContentEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFilters(new InputFilter[]{new MyInputFilter()});
    }

    private StringBuilder builder;


    /**
     * 识别输入框的是不是@符号,如果是则回调
     */
    private class MyInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            if (source.toString().equalsIgnoreCase("@")
                    || source.toString().equalsIgnoreCase("＠")) {
                if (onJumpListener != null) {
                    onJumpListener.goToChooseContact(CODE_PERSON);
                }
            }
            return source;
        }
    }

    /**
     * 添加一个块,在文字的后面添加
     *
     * @param showText 显示到界面的内容
     * @param userId   附加属性，比如用户id,邮件id之类的，如果不需要可以为空
     */
    public void addAtSpan(String maskText, String showText, String userId) {
        builder = new StringBuilder();
        String textStr = "";
        if (!TextUtils.isEmpty(maskText)) {
            //已经添加了@
            builder.append(maskText).append(showText).append("");
            textStr =  builder.toString();
        } else {
            builder.append(showText).append("");
            textStr =  "@"+builder.toString();
        }
        getText().insert(getSelectionStart(), builder.toString());
        SpannableString sps = new SpannableString(getText());

        int start = getSelectionEnd() - builder.toString().length() - (TextUtils.isEmpty(maskText) ? 1 : 0);
        int end = getSelectionEnd() ;

        LDSpan ldSpan = new LDSpan(getContext(),"@"+showText);
        sps.setSpan(ldSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        makeSpan(sps, new UnSpanText(start, end, textStr), userId);
        setText(sps);
        setSelection(end);
    }



    /**
     * 用于显示@信息
     * @param str 全部的内容
     * @param posStr 这里为@信息的位置信息，例如： "8,7|20,7",其中8为第八位，然后7为@信息的长度，|为分割，后面的20和7和前面信息类似
     * @param color @的颜色，默认为0099EE蓝色
     */
    public static SpannableString setAtStr(String str,String posStr,int... color){
        //@信息
        String[] posStrs = posStr.split("\\|");
        SpannableString sps = new SpannableString(str);
        //获取@信息的数量
        int size = posStrs.length;
        //@信息的颜色
        int colorInt = Color.parseColor("#0099EE");
        if (color.length>0){
            colorInt = color[0];
        }
        try {
            //开始循环渲染@信息的颜色
            for (int i = 0;i<size;i++){
                if (posStrs[i].contains(",")){
                    int start = StringAtUtil.getInt(posStrs[i].split(",")[0],0);
                    int len = StringAtUtil.getInt(posStrs[i].split(",")[1],0);
                    int end = start + len;
                    //String showText = str.substring(start,len);
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorInt);
                    sps.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }catch (Exception e){
        }
        return sps;
    }

    /**
     * 用于显示@信息，与上面的区别主要是。
     * 上面的那个方法用于显示用的，这个方法主要为编辑用的
     * @param str 全部的内容
     * @param posStr 这里为@信息的位置信息，例如： "8,7|20,7|",其中8为第八位，然后7为@信息的长度，|为分割，后面的20和7和前面信息类似
     * @param ids 为@对应的id信息。以逗号,分割。例如：1,2,3,4
     * @param mContext
     * @param color @的颜色，默认为0099EE蓝色
     */
    public static SpannableString setAtStr(String str,String posStr,String ids,Context mContext,int... color){
        //@信息
        String[] posStrs = posStr.split("\\|");
        //id信息
        String[] idStrs = ids.split(",");
        SpannableString sps = new SpannableString(str);
        //获取@信息的数量
        int size = posStrs.length;
        //@信息的颜色
        int colorInt = Color.parseColor("#0099EE");
        if (color.length>0){
            colorInt = color[0];
        }
        try {
            //开始循环渲染@信息的颜色，主要是将at信息弄成一个整体
            for (int i = 0;i<size;i++){
                if (posStrs[i].contains(",")){
                    int start = StringAtUtil.getInt(posStrs[i].split(",")[0],0);
                    int len = StringAtUtil.getInt(posStrs[i].split(",")[1],0);
                    int end = start + len;
                    String userId = idStrs[i];
                    String showText = str.substring(start,end);
                    LDSpan ldSpan = new LDSpan(mContext, showText,colorInt);
                    sps.setSpan(ldSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    makeSpan(sps, new UnSpanText(start, end, showText), userId);
                }
            }
        }catch (Exception e){
        }

        return sps;
    }

    //获取@坐标列表
    public String  getUserString() {
        MyTextSpan[] spans = sortStrings();
        String strPos="";
        int postion = 0;
        int i = 0;
        for (MyTextSpan myTextSpan : spans) {
            String str = getText().toString();
//            int pos1 = str.indexOf(myTextSpan.showText,postion);
            if (i>=1){
                //如果这次值等于前面的值，则退出这次循环
                int pos1 = StringAtUtil.getInt(myTextSpan.start,0) +myTextSpan.showText.length();
                if (pos1 == postion)
                    continue;
            }
            int pos = str.indexOf(myTextSpan.showText,postion);
            int len = myTextSpan.showText.length() ;
            int end = pos+len;
            if (pos!=-1){
                String str1 = str.substring(pos,end);
                postion = pos+len;
                if (i==spans.length){
                    strPos = strPos +pos+","+ len;
                }else {
                    strPos = strPos +pos+","+len +"|";
                }
            }
            i = i + 1;
        }
        return strPos;
    }

    //先对数组排序
    private MyTextSpan[] sortStrings(){
        MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
        if (spans==null||spans.length==0){
            return spans;
        }
        int n = spans.length;  //存放数组a中元素的个数
        int i;  //比较的轮数
        int j;  //每轮比较的次数
        MyTextSpan buf;  //交换数据时用于存放中间数据
        for (i=0; i<n-1; i++){  //比较n-1轮
            for (j=i+1; j<n; j++){  //每轮比较
                int start = StringAtUtil.getInt(spans[i].start,0);
                int next = StringAtUtil.getInt(spans[j].start,0);
                if (start > next) {
                    buf= spans[i];;
                    spans[i] = spans[j];
                    spans[j] = buf;
                }
            }
        }
        return spans;
    }

    //获取用户Id列表
    public String getUserIdString() {
        String ids = "";
        MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
        int i = 0;
        for (MyTextSpan myTextSpan : spans) {
            i = i + 1;
            if (i==spans.length){
                ids = ids + myTextSpan.getUserId();
            }else {
                ids = ids + myTextSpan.getUserId() + ",";
            }
        }
        return ids;
    }

    //生成一个需要整体删除的Span
    public static void makeSpan(Spannable sps, UnSpanText unSpanText, String userId) {
        MyTextSpan what = new MyTextSpan(unSpanText.returnText, userId,unSpanText.start+"",unSpanText.end+"");

        int start = unSpanText.start;
        int end = unSpanText.end;
        sps.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        //向前删除一个字符，@后的内容必须大于一个字符，可以在后面加一个空格
        if (lengthBefore == 1 && lengthAfter == 0) {
            MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
            for (MyTextSpan myImageSpan : spans) {
                if (!text.toString().endsWith(myImageSpan.getShowText())&&  getText().getSpanEnd(myImageSpan) == start){
                    getText().delete(getText().getSpanStart(myImageSpan), getText().getSpanEnd(myImageSpan));
                    break;
                }
            }
        }

    }


    private static class MyTextSpan extends MetricAffectingSpan {

        private String showText;
        private String userId;
        private String start;
        private String end;

        public MyTextSpan(String showText, String userId,String start,String end) {
            this.showText = showText;
            this.userId = userId;
            this.start = start;
            this.end = end;
        }
        public String getShowText() {
            return showText;
        }

        public String getUserId() {
            return userId;
        }

        public String getStart() {
            return start;
        }

        public String getEnd(){
            return end;
        }

        @Override
        public void updateMeasureState(TextPaint p) {

        }

        @Override
        public void updateDrawState(TextPaint tp) {

        }
    }

    private static class UnSpanText {
        int start;
        int end;
        String returnText;

        UnSpanText(int start, int end, String returnText) {
            this.start = start;
            this.end = end;
            this.returnText = returnText;
        }
    }


    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_PERSON && resultCode == Activity.RESULT_OK) {
            String keyId = data.getStringExtra(KEY_CID);
            String keyId2 = data.getExtras().getString(KEY_CID);
            Bundle bundle = data.getExtras();
            String nameStr = data.getStringExtra(KEY_NAME);
            addAtSpan(null, nameStr, keyId);
        } else if (requestCode == CODE_PERSON2 && resultCode == Activity.RESULT_OK) {
            String keyId = data.getStringExtra(KEY_CID);
            String keyId2 = data.getExtras().getString(KEY_CID);
            Bundle bundle = data.getExtras();
            String nameStr = data.getStringExtra(KEY_NAME);
            addAtSpan("@", nameStr, keyId);
        }
    }



    private OnJumpListener onJumpListener;

    public interface OnJumpListener {
        void goToChooseContact(int requestCode);
    }
    //对外方法
    public void setOnJumpListener(OnJumpListener onJumpListener) {
        this.onJumpListener = onJumpListener;
    }
}





