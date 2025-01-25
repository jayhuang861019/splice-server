package com.txzmap.spliceservice.util;


import io.jsonwebtoken.*;

import java.util.Date;
import java.util.UUID;

/**
 * jwt是可以携带部分的用户信息的
 * 这里的jwt就是携带了用户的id
 */
public class JwtUtils {


    private static long time = 1000 * 60 * 60 * 24;//表示有效期为24h
    private static String signature = "jayhuang";//定义签名

    public static String CreateToken(Integer userId) {//创建token的方法
        //JwtBuilder用来构建jwt
        JwtBuilder jwtBuilder = Jwts.builder();
        String jwtToken = jwtBuilder
                //header
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                //payload
                .claim("username", "admin")
                .claim("role", "user")
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date(System.currentTimeMillis() + time))//设置过期时间
                .setId(UUID.randomUUID().toString())
                //signature
                .signWith(SignatureAlgorithm.HS256, signature)
                .compact();//把三部分拼接起来
        return jwtToken;
    }

    //校验token的方法,其实就是解析token，如果解析成功就是可以放行
    public static boolean checkToken(String token) {
        if (token == null) {
            return false;
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(signature).parseClaimsJws(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 通过token获取UserId
     *
     * @param token
     * @return
     */
    public static Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signature)
                .parseClaimsJws(token)
                .getBody();

        String subject = claims.getSubject();

        try {
            return Integer.parseInt(subject);
        } catch (NumberFormatException e) {
            // 处理错误，比如返回null, 0, 或者另一个默认值
            return null; // 或者你也可以选择抛出自定义异常
        }
    }
}
