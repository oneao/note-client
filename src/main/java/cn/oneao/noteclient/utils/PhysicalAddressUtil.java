package cn.oneao.noteclient.utils;

import com.alibaba.fastjson.JSONObject;

public class PhysicalAddressUtil {
    public static String getPhysicalAddress(String ipAddr){
        String url = "https://ip.taobao.com/outGetIpInfo?ip=" + ipAddr + "&accessKey=alibaba-inc";
        String response = HttpClientUtil.doGet(url, true);
        JSONObject jsonObject = JSONObject.parseObject(response);

        JSONObject dataObject = jsonObject.getJSONObject("data");
        if (dataObject != null) {
            String country = dataObject.getString("country");
            String region = dataObject.getString("region");
            String city = dataObject.getString("city");

            if (country != null && region != null && city != null) {
                return country + "-" + region + "-" + city;
            }
        }

        return "Unknown";
    }
}
