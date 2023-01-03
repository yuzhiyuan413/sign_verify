package com.bonc.encrypt.interface_encryption.wrapper;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public String requestBody = null;

    public MyHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        requestBody = readBody(request);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read(){
                return bais.read();
            }
        };
    }

    private static String readBody(ServletRequest request) throws IOException {
        String param = IOUtils.toString(request.getInputStream());
//        if(StringUtils.isNotBlank(param)) {
//            //解密请求参数
//            return HttpEncryptUtil.serverDecrypt(param);
//        }
        return param;
    }
}