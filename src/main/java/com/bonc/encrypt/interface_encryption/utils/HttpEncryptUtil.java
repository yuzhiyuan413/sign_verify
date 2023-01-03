package com.bonc.encrypt.interface_encryption.utils;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class HttpEncryptUtil {
    public static final String SM4_KEY = "ksac+nwp0US0upbPvhVzrw==";
    //解密请求内容
    public static boolean verify(String content) {
        SortedMap<String, Object> map = transfer2Map(content);
        String sign = map.get("_sign").toString();

        String result = "";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null && StringUtils.isNotBlank(entry.getValue().toString())) {
                result += entry.getKey() + entry.getValue();
            }
        }
        String md5Encode = MD5Util.MD5Encode(result, "utf-8");
        System.out.println("md5值："+md5Encode);
//        byte[] key = SM2Util.base64ToBytes(SM4_KEY);
//        SM4 sm4 = SmUtil.sm4(key);
//        String encrypt = SM4Util.encrypt(sm4, md5Encode);
//        System.out.println("sm4加密后的值："+encrypt);

        return true;
    }

    private static SortedMap transfer2Map(String content) {
        SortedMap map = new TreeMap();
        if(StringUtils.isBlank(content)){
            return map;
        }
        JSONObject result = JSONObject.parseObject(content);
        Iterator iter = result.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            map.put(entry.getKey().toString(),entry.getValue());
        }
        return map;
    }

}
