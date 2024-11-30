package com.txzmap.spliceservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MyInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    /***
     * 在请求处理之前进行调用(Controller方法调用之前)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            HttpSession session = request.getSession();
            //统一拦截（查询当前session是否存在user）(这里user会在每次登录成功后，写入session)
//            UserOld userOld = (UserOld) session.getAttribute("userOld");
//            if (userOld == null) {
//                response.sendRedirect(request.getContextPath() + "/login.html");
//            }
//            String requestPath = request.getServletPath();

            //如果是拼接请求则判断账号是否过期
//            if (requestPath.equals("/splice")) {
//                if (userOld.isAvaliable())
//                    return true;
//                logger.error("the account is over expiration....");
//                String resultJson = new ObjectMapper().writeValueAsString(new MyResult(0, "您的账号已过期，请联系客服！"));
//                response.setContentType("application/json;charset=utf-8");
//                response.getWriter().print(resultJson);
//                return false;
//            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
        //如果设置为false时，被请求时，拦截器执行到此处将不会继续操作
        //如果设置为true时，请求将会继续执行后面的操作
    }

    /***
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /***
     * 整个请求结束之后被调用，也就是在DispatchServlet渲染了对应的视图之后执行（主要用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
