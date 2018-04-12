/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.conciso.redis;

import redis.clients.jedis.Jedis;

/**
 *
 * @author ahmad
 */
public class RedisConnection {

    public static Jedis getConnection() {
        System.out.println("get Redis connection for cache");
        Jedis jedis = new Jedis("localhost");
        return jedis;
    }
}
