package com.souja.lib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MDateUtils {

    /**
     * 取得时间过去了多久 （刚刚、几分钟前、几小时前、昨天、前天、几天前、几个月前、半年前、很久了）
     *
     * @param dateLong
     * @return
     */
    public static String getDateDesc(long dateLong) {
        Date now = new Date();
        long duration = now.getTime() - dateLong;
        String str;
        if (duration / 1000 / 60 <= 1) {
            str = "刚刚";
        } else if (duration / 1000 / 60 > 1 && duration / 1000 / 60 < 60) {
            str = duration / 1000 / 60 + "分钟前";
        } else if (duration / 1000 / 60 / 60 >= 1 && duration / 1000 / 60 / 60 < 24) {
            str = duration / 1000 / 60 / 60 + "小时前";
        } else if (duration / 1000 / 60 / 60 / 24 >= 1
                && duration / 1000 / 60 / 60 / 24 < 2) {
            str = "昨天";
        } else if (duration / 1000 / 60 / 60 / 24 >= 2
                && duration / 1000 / 60 / 60 / 24 < 3) {
            str = "前天";
        }
//        else if (duration / 1000 / 60 / 60 / 24 >= 3
//                && duration / 1000 / 60 / 60 / 24 < 30) {
//            str = duration / 1000 / 60 / 60 / 24 + "天前";
//        }
//        else if (duration / 1000 / 60 / 60 / 24 / 30 >= 1
//                && duration / 1000 / 60 / 60 / 24 / 30 < 6) {
//            str = duration / 1000 / 60 / 60 / 24 / 30 + "个月前";
//        } else if (duration / 1000 / 60 / 60 / 24 / 30 >= 6
//                && duration / 1000 / 60 / 60 / 24 / 30 < 12) {
//            str = "半年前";
//        }
        else {
            str = longToStringDate(dateLong);
        }
        return str;
    }

//    public static int getDayOfWeekForInt() {
//        Calendar calendar = Calendar.get();
//        Date date = new Date();
//        calendar.setTime(date);
//        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//        return dayOfWeek - 1;
//    }

    /**
     * 获取所传日期是星期几
     *
     * @param strDate yyyy-MM-DD
     */
    public static String getWeekOfDate(String strDate) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 定义日期格式
        Date date = null;
        try {
            date = format.parse(strDate);// 将字符串转换为日期
        } catch (ParseException e) {
            System.out.println("输入的日期格式不合理！");
        }
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) w = 0;
        return weekDays[w];
    }

    /**
     * 今天是星期几
     */
    public static String getTodayWeek() {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) w = 0;
        return weekDays[w];
    }

    /**
     * 获取当前日期
     *
     * @param needHms 是否需要时分秒
     * @return yyyy-MM-dd or yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentDate(boolean needHms) {
        SimpleDateFormat formatter;
        if (needHms)
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        else
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(new Date());
    }

    /**
     * 返回datePre日期后面offset天的日期
     *
     * @param datePre 比较的日期
     * @param offset  日期偏移量。正数，日期往后推；反之。
     */
    public static Date getDate(Date datePre, int offset) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(datePre);
        if (offset != 0) {
            calendar.add(calendar.DATE, offset);// 正数往后推,负数往前移动
            datePre = calendar.getTime(); // 日期偏移后的结果
        }
        return datePre;
    }

    /**
     * 当前日期
     *
     * @return yyyy-MM-dd
     */
    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(new Date());
    }

    /**
     * 当前日期
     *
     * @return yyyyMMdd
     */
    public static String getCurrentDate1() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(new Date());
    }

    /**
     * 当前日期
     *
     * @return yyyyMMddHHmmss
     */
    public static String getCurrentDate2() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(new Date());
    }

    /**
     * 当前日期
     *
     * @return HH:mm:ss
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(new Date());
    }


    /**
     * parse String yyyy-MM-dd to Date
     */
    public static Date stringToDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static long stringToDateLong(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date != null ? date.getTime() : 0;
    }


    // long类型转换为String类型
    // currentTime要转换的long类型的时间
    // formatType要转换的string类型的时间格式
    public static String longToString(long currentTime, String formatType) {
        Date date; // long类型转成Date类型
        try {
            date = longToDate(currentTime, formatType);
            return dateToString(date, formatType);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    public static long getRemindDateLong(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * yyyy-MM-dd HH时
     */
    public static String getRemindDateStr(long timemiles) {
        if (timemiles <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH时");
        Date dt = new Date(timemiles);
        int hour = dt.getHours();
        String dateDesc = sdf.format(dt);
        if (hour > 12)
            return dateDesc.replace(hour + "时", "下午 " + (hour - 12) + " 时");
        else if (hour == 12)
            return dateDesc.replace(hour + "时", "中午 12 时");
        else
            return dateDesc.replace("0" + hour + "时", "上午 " + hour + " 时")
                    .replace(hour + "时", "上午 " + hour + " 时");
    }

    public static boolean isBirthDay(long birthday) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String today = formatter.format(new Date());
        String birthStr;
        try {
            Date dt = new Date(birthday);
            birthStr = formatter.format(dt);
        } catch (Exception e) {
            e.printStackTrace();
            birthStr = "";
        }
        return today.equals(birthStr);
    }

    /**
     * yyyy-MM-dd
     */
    public static String longToStringDate(long timemiles) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = new Date(timemiles);
            return sdf.format(dt);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static String longToStringDate1(long timemiles) {
        if (timemiles <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = new Date(timemiles);
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /**
     * yyyy.MM.dd HH:mm
     */
    public static String longToStringDate2(long timemiles) {
        if (timemiles <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        Date dt = new Date(timemiles);
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /**
     * yyyy-MM-dd HH:mm
     */
    public static String longToStringDate22(long timemiles) {
        if (timemiles <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dt = new Date(timemiles);
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /**
     * MM-dd HH:mm
     */
    public static String longToStringDate4(long timemiles) {
        if (timemiles <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        Date dt = new Date(timemiles);
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /**
     * MM.dd
     */
    public static String longToStringDate5(long timemiles) {
        if (timemiles <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        Date dt = new Date(timemiles);
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /**
     * MM-dd
     */
    public static String longToStringDate6(long timemiles) {
        if (timemiles <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Date dt = new Date(timemiles);
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /**
     * @return yyyy.MM.dd
     */
    public static String longToStringDate7(long timemiles) {
        if (timemiles <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date dt = new Date(timemiles);
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /**
     * @return yyyy.MM.dd HH:mm
     */
    public static String longToStringDate8(long timemiles) {
        if (timemiles <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        Date dt = new Date(timemiles);
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /**
     * @return yyyy-MM-ddTHH:mm:ss.SSSZ
     */
    public static String longToStringDate9(long timemiles) {
        if (timemiles <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.SSSZ");
        Date dt = new Date(timemiles);
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /**
     * @return yyyy-mm-dd
     */
    public static String dateToString(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = date;
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /**
     * @return yyyy-mm-dd HH:mm:ss
     */
    public static String dateToString2(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = date;
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /**
     * 根据出生日期获取年龄
     *
     * @param birthDate 出生日期
     */
    public static int getAge(long birthDate) {
        Date date = new Date(birthDate);
        int age;
        Date now = new Date();
        SimpleDateFormat format_y = new SimpleDateFormat("yyyy");
        SimpleDateFormat format_M = new SimpleDateFormat("MM");

        String birth_year = format_y.format(date);
        String this_year = format_y.format(now);

        String birth_month = format_M.format(date);
        String this_month = format_M.format(now);

        // 初步，估算
        age = Integer.parseInt(this_year) - Integer.parseInt(birth_year);

        // 如果未到出生月份，则age - 1
        if (this_month.compareTo(birth_month) < 0)
            age -= 1;
        if (age < 0)
            age = 0;
        return age;
    }

    /**
     * 当前月份
     *
     * @return M（1-12）
     */
    public static String getCurrentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("M");
        Date dt = new Date();
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    /*
     * 判断前一个日期是否大于后一个日期
     *
     * @param d1 不传则默认为当前日期
     * @param d2 不传则默认为当前日期
     * @return true（前一个日期大于后一个日期），反之。
     */
/*
    public static boolean dateCompare(Date d1, Date d2) {
        String s1;
        if (d1 == null) s1 = getCurrentDate1();
        else s1 = dateToString1(d1);

        String s2;
        if (d2 == null) s2 = getCurrentDate1();
        else s2 = dateToString1(d2);

        return Long.parseLong(s1) >= Long.parseLong(s2);
    }
*/

//    /**
//     * 计算两个时间戳相差的天数
//     *
//     * @param stamp1 （大）
//     * @param stamp2 （小）
//     * @return
//     */
//    public static int diff_in_date(long stamp1, long stamp2) {
//        long t1 = ((stamp1 / 1000) + 28800) / 86400;
//        long t2 = ((stamp2 / 1000) + 28800) / 86400;
//        return (int)(t1 - t2);
//    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    // date类型转换为String类型
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }


    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate2(String strTime, String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date;
        try {
            date = formatter.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }


    //由出生日期获得年龄
    public static int getAge(Date birthDay) {
        try {
            Calendar cal = Calendar.getInstance();

            if (cal.before(birthDay)) {
                throw new IllegalArgumentException(
                        "The birthDay is before Now.It's unbelievable!");
            }
            int yearNow = cal.get(Calendar.YEAR);
            int monthNow = cal.get(Calendar.MONTH);
            int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
            cal.setTime(birthDay);

            int yearBirth = cal.get(Calendar.YEAR);
            int monthBirth = cal.get(Calendar.MONTH);
            int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

            int age = yearNow - yearBirth;

            if (monthNow <= monthBirth) {
                if (monthNow == monthBirth) {
                    if (dayOfMonthNow < dayOfMonthBirth) age--;
                } else {
                    age--;
                }
            }
            return age;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param week [0123456]
     */
//    public static String getStringWeek(int week) {
//        switch (week) {
//            case 0:
//                return "周日";
//            case 1:
//                return "周一";
//            case 2:
//                return "周二";
//            case 3:
//                return "周三";
//            case 4:
//                return "周四";
//            case 5:
//                return "周五";
//            case 6:
//                return "周六";
//            default:
//                LogUtil.e("week is not in[0123456] - " + week);
//                return "";
//        }
//    }

    /**
     * 得到天时分秒
     *
     * @param seconds 总秒数
     * @return xx天xx时xx分xx秒
     */
//    public static String getDayHourMinSec(Long seconds) {
//        long day = 0;
//        long hour = 0;
//        long min = 0;
//        long sec = 0;
//        if (seconds > 0) {
//            sec = seconds % 60;
//            min = seconds / 60 % 60;
//            hour = seconds / 60 / 60 % 24;
//            day = seconds / 60 / 60 / 24;
//        }
//        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
//    }

    /**
     * 获取当前日期：yyyy-MM-dd
     *
     * @param offset 日期偏移量。正数，日期往后推；反之。
     */
//    public static String getStringDate(int offset) {
//        Date date = new Date();// 取时间
//        Calendar calendar = new GregorianCalendar();
//        calendar.setTime(date);
//        if (offset != 0) {
//            calendar.add(calendar.DATE, offset);// 正数往后推,负数往前移动
//            date = calendar.getTime(); // 日期偏移后的结果
//        }
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String dateString = formatter.format(date);
//        return dateString;
//    }

}
