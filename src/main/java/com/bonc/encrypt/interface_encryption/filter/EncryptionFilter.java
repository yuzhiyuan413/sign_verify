package com.bonc.encrypt.interface_encryption.filter;

import com.alibaba.fastjson2.JSON;
import com.bonc.encrypt.interface_encryption.utils.HttpEncryptUtil;
import com.bonc.encrypt.interface_encryption.wrapper.MyHttpServletRequestWrapper;
import com.bonc.encrypt.interface_encryption.wrapper.MyHttpServletResponseWrapper;
import com.bonc.encrypt.interface_encryption.wrapper.ParameterRequestWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Log4j2
@Component
@WebFilter(filterName = "EncryptionFilter", urlPatterns = {"/*"})
public class EncryptionFilter implements Filter {
    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/pay/hello")));
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request instanceof HttpServletRequest) {
            HttpServletRequest hRequest = (HttpServletRequest) request;
            String servletPath = hRequest.getServletPath();
            log.info("request_path:path="+servletPath);
            boolean allow = false;
            for (String allowedPath : ALLOWED_PATHS) {
                if(servletPath.contains(allowedPath)) {
                    allow = true;
                    break;
                }
            }
            if(allow) {
                chain.doFilter(request, response);
                log.info("no Encryption");
                return;
            }
            MyHttpServletResponseWrapper responseWrapper = new MyHttpServletResponseWrapper((HttpServletResponse) response);
            if(hRequest.getMethod().equalsIgnoreCase("post")) {
                MyHttpServletRequestWrapper requestWrapper = null;
                try {
                    requestWrapper = new MyHttpServletRequestWrapper(hRequest);
                } catch (Exception e) {
                    request.getRequestDispatcher("404.html").forward(request,response);
                }
                System.out.println(requestWrapper.requestBody);
                boolean verify = HttpEncryptUtil.verify(requestWrapper.requestBody);
                if(!verify){
                    returnErrorJson(response,"签名验证失败");
                    return;
                }
                chain.doFilter(requestWrapper, responseWrapper);
            }else if(hRequest.getMethod().equalsIgnoreCase("options")){
                log.info("收到 potions请求");
            } else { //其余默认get请求
                ParameterRequestWrapper requestWrapper = null;
                try {
                    requestWrapper = new ParameterRequestWrapper(hRequest);
                } catch (Exception e) {
                    request.getRequestDispatcher("404.html").forward(request,response);
                }
                boolean verify = HttpEncryptUtil.verify(requestWrapper.requestParam);
                if(!verify){
                    returnErrorJson(response,"签名验证失败");
                    return;
                }
                chain.doFilter(requestWrapper, responseWrapper);
            }
            String resp = responseWrapper.getContent(); //获取接口返回内容
            //加密处理返回
            response.getOutputStream().write(resp.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void returnErrorJson(ServletResponse response, String message) throws IOException {
        Map<String,Object> map = new HashMap<>();
        map.put("message","签名验证失败");
        map.put("success",true);
        map.put("code",500);
        map.put("result", "签名验证失败");
        String jsonString = JSON.toJSONString(map);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(jsonString.getBytes());
        outputStream.flush();
    }


}
