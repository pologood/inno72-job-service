package com.inno72.job.admin.common;

import com.inno72.job.admin.route.ExecutorRouter;
import com.inno72.job.admin.route.strategy.ExecutorRouteBusyover;
import com.inno72.job.admin.route.strategy.ExecutorRouteConsistentHash;
import com.inno72.job.admin.route.strategy.ExecutorRouteFailover;
import com.inno72.job.admin.route.strategy.ExecutorRouteFirst;
import com.inno72.job.admin.route.strategy.ExecutorRouteLFU;
import com.inno72.job.admin.route.strategy.ExecutorRouteLRU;
import com.inno72.job.admin.route.strategy.ExecutorRouteLast;
import com.inno72.job.admin.route.strategy.ExecutorRouteRandom;
import com.inno72.job.admin.route.strategy.ExecutorRouteRound;


public enum ExecutorRouteStrategyEnum {

    FIRST("第一个", new ExecutorRouteFirst()),
    LAST("最后一个", new ExecutorRouteLast()),
    ROUND("轮询", new ExecutorRouteRound()),
    RANDOM("随机", new ExecutorRouteRandom()),
    CONSISTENT_HASH("一致性HASH", new ExecutorRouteConsistentHash()),
    LEAST_FREQUENTLY_USED("最不经常使用", new ExecutorRouteLFU()),
    LEAST_RECENTLY_USED("最近最久未使用", new ExecutorRouteLRU()),
    FAILOVER("故障转移", new ExecutorRouteFailover()),
    BUSYOVER("忙碌转移", new ExecutorRouteBusyover()),
    SHARDING_BROADCAST("分片广播", null);

    ExecutorRouteStrategyEnum(String title, ExecutorRouter router) {
        this.title = title;
        this.router = router;
    }

    private String title;
    private ExecutorRouter router;

    public String getTitle() {
        return title;
    }
    public ExecutorRouter getRouter() {
        return router;
    }

    public static ExecutorRouteStrategyEnum match(String name, ExecutorRouteStrategyEnum defaultItem){
        if (name != null) {
            for (ExecutorRouteStrategyEnum item: ExecutorRouteStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }

}
