package com.xrone.julis.compous.Communication.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xrone.julis.compous.Communication.Model.ApiDefine;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;


import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class FormatUtils {

    private FormatUtils() {}

    /**
     * 获取最近时间字符串
     */

    private static final long MINUTE = 60 * 1000;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long WEEK = 7 * DAY;
    private static final long MONTH = 31 * DAY;
    private static final long YEAR = 12 * MONTH;

    public static String getRelativeTimeSpanString(@NonNull DateTime dateTime) {
        long offset = System.currentTimeMillis() - dateTime.getMillis();
        if (offset > YEAR) {
            return (offset / YEAR) + "Years ago";
        } else if (offset > MONTH) {
            return (offset / MONTH) + "Months ago";
        } else if (offset > WEEK) {
            return (offset / WEEK) + "Weeks ago";
        } else if (offset > DAY) {
            return (offset / DAY) + "Days ago";
        } else if (offset > HOUR) {
            return (offset / HOUR) + "Hous ago";
        } else if (offset > MINUTE) {
            return (offset / MINUTE) + "Mins ago";
        } else {
            return "Just Now";
        }
    }

    /**
     * 检测是否是用户accessToken
     */
    public static boolean isAccessToken(String accessToken) {
        if (TextUtils.isEmpty(accessToken)) {
            return false;

        } else {
            try {
                //noinspection ResultOfMethodCallIgnored
                UUID.fromString(accessToken);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 生成自增Integer
     */

    private static final AtomicInteger requestCodeSeed = new AtomicInteger(1);

    public static int generateRequestCode() {
        return requestCodeSeed.getAndIncrement();
    }

    /**
     * 修复头像地址的历史遗留问题
     */
    public static String getCompatAvatarUrl(String avatarUrl) {
        if (!TextUtils.isEmpty(avatarUrl) && avatarUrl.startsWith("//gravatar.com/avatar/")) {
            return "http:" + avatarUrl;
        } else {
            return avatarUrl;
        }
    }

    /**
     * 标准URL检测
     */

    public static boolean isUserLinkUrl(@Nullable String url) {
        return !TextUtils.isEmpty(url) && url.startsWith(ApiDefine.USER_LINK_URL_PREFIX);
    }

    public static boolean isTopicLinkUrl(@Nullable String url) {
        return !TextUtils.isEmpty(url) && url.startsWith(ApiDefine.TOPIC_LINK_URL_PREFIX);
    }

    /**
     * CNode兼容性的Markdown转换
     * '@'协议转换为'/user/'相对路径
     * 解析裸链接
     * 最外层包裹'<div class="markdown-text"></div>'
     * 尽可能的实现和服务端渲染相同的结果
     */
//
//    private static final Markdown md = new Markdown();
//
//    public static String renderMarkdown(String text) {
//        // 保证text不为null
//        text = TextUtils.isEmpty(text) ? "" : text;
//        // 解析@协议
//        text = text.replaceAll("@([\\w\\-]+)\\b(?![\\]\\<\\.])", "[@$1](" + ApiDefine.USER_PATH_PREFIX + "$1)");
//        // 解析裸链接
//        text = text + " ";
//        text = text.replaceAll("((http|https|ftp)://[\\w\\-.:/?=&#%]+)(\\s)", "[$1]($1)$3");
//        text = text.trim();
//        // 渲染markdown
//        try {
//            StringWriter out = new StringWriter();
//            md.transform(new StringReader(text), out);
//            text = out.toString();
//        } catch (ParseException e) {
//            // nothing to do
//        }
//        // 添加样式容器
//        return "<div class=\"markdown-text\">" + text + "</div>";
//    }

    /**
     * CNode兼容性的Html处理
     * 过滤xss
     * 保留div的class属性，但是会清除其他非白名单属性
     * 通过baseUrl补全相对地址
     */

    private static final Cleaner cleaner = new Cleaner(Whitelist.relaxed().addAttributes("div", "class")); // 主要目的是保留最外层div的markdown-text属性

    public static Document handleHtml(String html) {
        // 保证html不为null
        html = TextUtils.isEmpty(html) ? "" : html;
        // 过滤xss
        return cleaner.clean(Jsoup.parseBodyFragment(html, ApiDefine.HOST_BASE_URL));
    }
    public static DateTime getDateTime(String time){
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime createTime = DateTime.parse(time, format);
        return createTime;
    }
}
