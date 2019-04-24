package com.souja.lib.widget.cityPicker.Interface;


import com.souja.lib.widget.cityPicker.bean.CityBean;
import com.souja.lib.widget.cityPicker.bean.DistrictBean;
import com.souja.lib.widget.cityPicker.bean.ProvinceBean;

/**
 * 作者：liji on 2017/11/16 10:06
 * 邮箱：lijiwork@sina.com
 * QQ ：275137657
 */

public abstract class OnCityItemClickListener {
    
    /**
     * 当选择省市区三级选择器时，需要覆盖此方法
     * @param province
     * @param city
     * @param district
     */
    public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
        
    }
    
    /**
     * 取消
     */
    public void onCancel() {
        
    }
}
