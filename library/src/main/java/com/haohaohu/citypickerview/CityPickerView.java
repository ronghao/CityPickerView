package com.haohaohu.citypickerview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohaohu.citypickerview.bean.bean.CityBean;
import com.haohaohu.citypickerview.bean.bean.DistrictBean;
import com.haohaohu.citypickerview.bean.bean.ProvinceBean;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 城市选择View
 *
 * @author haohao(ronghao3508@gmail.com) on 2017/10/13 下午 02:07
 * @version v1.0
 */
public class CityPickerView extends LinearLayout {

    private WheelView mViewProvince;

    private WheelView mViewCity;

    private WheelView mViewDistrict;

    //省份数据
    ArrayList<ProvinceBean> mProvinceBeanArrayList = new ArrayList<>();

    //城市数据
    ArrayList<ArrayList<CityBean>> mCityBeanArrayList;

    //地区数据
    ArrayList<ArrayList<ArrayList<DistrictBean>>> mDistrictBeanArrayList;

    //key - 省 value - 市
    protected Map<String, CityBean[]> mPro_CityMap = new HashMap<String, CityBean[]>();
    //key - 市 values - 区
    protected Map<String, DistrictBean[]> mCity_DisMap = new HashMap<String, DistrictBean[]>();
    //key - 区 values - 邮编
    protected Map<String, DistrictBean> mDisMap = new HashMap<String, DistrictBean>();

    private ProvinceBean[] mProvinceBeenArray;

    /**
     * 第一次默认的显示省份，一般配合定位，使用
     */
    private String defaultProvinceName = "北京";

    /**
     * 第一次默认得显示城市，一般配合定位，使用
     */
    private String defaultCityName = "北京";

    /**
     * 第一次默认得显示，一般配合定位，使用
     */
    private String defaultDistrict = "东城区";

    //选中
    private ProvinceBean mProvinceBean;
    private CityBean mCityBean;
    private DistrictBean mDistrictBean;

    public CityPickerView(Context context) {
        super(context);
        initView();
    }

    public CityPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CityPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.citypick_layout, this);
        mViewProvince = (WheelView) findViewById(R.id.id_province);
        mViewCity = (WheelView) findViewById(R.id.id_city);
        mViewDistrict = (WheelView) findViewById(R.id.id_district);

        mViewProvince.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                updateCities();
            }
        });

        mViewCity.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                updateAreas();
            }
        });

        mViewDistrict.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                mDistrictBean = mCity_DisMap.get(mProvinceBean.getName()
                        + mCityBean.getName())[mViewDistrict.getSeletedIndex()];
            }
        });
        initProvinceDatas(getContext());
        setUpData();
    }

    public String getName() {
        return getProvince() + getCity() + getDistrict();
    }

    public String getProvince() {
        return mProvinceBean.getName();
    }

    public String getCity() {
        return mCityBean.getName();
    }

    public String getDistrict() {
        return mDistrictBean.getName();
    }

    public void setLoc(String provinceName, String cityName, String district) {
        defaultProvinceName = provinceName;
        defaultCityName = cityName;
        defaultDistrict = district;
        setUpData();
    }

    protected void initProvinceDatas(Context context) {

        String cityJson = CityJsonReadUtil.getJson(context, "city_20170724.json");
        Type type = new TypeToken<ArrayList<ProvinceBean>>() {
        }.getType();

        mProvinceBeanArrayList = new Gson().fromJson(cityJson, type);
        mCityBeanArrayList = new ArrayList<>(mProvinceBeanArrayList.size());
        mDistrictBeanArrayList = new ArrayList<>(mProvinceBeanArrayList.size());

        //*/ 初始化默认选中的省、市、区，默认选中第一个省份的第一个市区中的第一个区县
        if (mProvinceBeanArrayList != null && !mProvinceBeanArrayList.isEmpty()) {
            mProvinceBean = mProvinceBeanArrayList.get(0);
            List<CityBean> cityList = mProvinceBean.getCityList();
            if (cityList != null && !cityList.isEmpty() && cityList.size() > 0) {
                mCityBean = cityList.get(0);
                List<DistrictBean> districtList = mCityBean.getCityList();
                if (districtList != null && !districtList.isEmpty() && districtList.size() > 0) {
                    mDistrictBean = districtList.get(0);
                }
            }
        }

        //省份数据
        mProvinceBeenArray = new ProvinceBean[mProvinceBeanArrayList.size()];

        for (int p = 0; p < mProvinceBeanArrayList.size(); p++) {

            //遍历每个省份
            ProvinceBean itemProvince = mProvinceBeanArrayList.get(p);

            //每个省份对应下面的市
            ArrayList<CityBean> cityList = itemProvince.getCityList();

            //当前省份下面的所有城市
            CityBean[] cityNames = new CityBean[cityList.size()];

            //遍历当前省份下面城市的所有数据
            for (int j = 0; j < cityList.size(); j++) {
                cityNames[j] = cityList.get(j);

                //当前省份下面每个城市下面再次对应的区或者县
                List<DistrictBean> districtList = cityList.get(j).getCityList();

                DistrictBean[] distrinctArray = new DistrictBean[districtList.size()];

                for (int k = 0; k < districtList.size(); k++) {

                    // 遍历市下面所有区/县的数据
                    DistrictBean districtModel = districtList.get(k);

                    //存放 省市区-区 数据
                    mDisMap.put(
                            itemProvince.getName() + cityNames[j].getName() + districtList.get(k)
                                    .getName(), districtModel);

                    distrinctArray[k] = districtModel;
                }
                // 市-区/县的数据，保存到mDistrictDatasMap
                mCity_DisMap.put(itemProvince.getName() + cityNames[j].getName(), distrinctArray);
            }

            // 省-市的数据，保存到mCitisDatasMap
            mPro_CityMap.put(itemProvince.getName(), cityNames);

            mCityBeanArrayList.add(cityList);

            ArrayList<ArrayList<DistrictBean>> array2DistrictLists =
                    new ArrayList<>(cityList.size());

            for (int c = 0; c < cityList.size(); c++) {
                CityBean cityBean = cityList.get(c);
                array2DistrictLists.add(cityBean.getCityList());
            }
            mDistrictBeanArrayList.add(array2DistrictLists);

            //赋值所有省份的名称
            mProvinceBeenArray[p] = itemProvince;
        }
    }

    private void setUpData() {
        int provinceDefault = -1;
        if (!TextUtils.isEmpty(defaultProvinceName) && mProvinceBeenArray.length > 0) {
            for (int i = 0; i < mProvinceBeenArray.length; i++) {
                if (mProvinceBeenArray[i].getName().contains(defaultProvinceName)) {
                    provinceDefault = i;
                    break;
                }
            }
        }

        if (-1 != provinceDefault) {
            mViewProvince.setSelected(provinceDefault);
        }

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < mProvinceBeenArray.length; i++) {
            strings.add(mProvinceBeenArray[i].getName());
        }
        mViewProvince.setItems(strings);

        updateCities();
        updateAreas();
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        //省份滚轮滑动的当前位置
        int pCurrent = mViewProvince.getSeletedIndex();
        //省份选中的名称
        mProvinceBean = mProvinceBeenArray[pCurrent];

        CityBean[] cities = mPro_CityMap.get(mProvinceBean.getName());
        if (cities == null) {
            return;
        }

        //设置最初的默认城市
        int cityDefault = -1;
        if (!TextUtils.isEmpty(defaultCityName) && cities.length > 0) {
            for (int i = 0; i < cities.length; i++) {
                if (defaultCityName.contains(cities[i].getName())) {
                    cityDefault = i;
                    break;
                }
            }
        }

        if (-1 != cityDefault) {
            mViewCity.setSelected(cityDefault);
        } else {
            mViewCity.setSelected(0);
        }

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < cities.length; i++) {
            strings.add(cities[i].getName());
        }
        mViewCity.setItems(strings);

        updateAreas();
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {

        int pCurrent = mViewCity.getSeletedIndex();

        mCityBean = mPro_CityMap.get(mProvinceBean.getName())[pCurrent];

        DistrictBean[] areas = mCity_DisMap.get(mProvinceBean.getName() + mCityBean.getName());

        if (areas == null) {
            return;
        }

        int districtDefault = -1;
        if (!TextUtils.isEmpty(defaultDistrict) && areas.length > 0) {
            for (int i = 0; i < areas.length; i++) {
                if (defaultDistrict.contains(areas[i].getName())) {
                    districtDefault = i;
                    break;
                }
            }
        }

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < areas.length; i++) {
            strings.add(areas[i].getName());
        }
        mViewDistrict.setItems(strings);

        if (-1 != districtDefault) {
            mViewDistrict.setSelected(districtDefault);
            //获取第一个区名称
            mDistrictBean =
                    mDisMap.get(mProvinceBean.getName() + mCityBean.getName() + defaultDistrict);
        } else {
            mViewDistrict.setSelected(0);
            if (areas.length > 0) {
                mDistrictBean = areas[0];
            }
        }
    }
}
