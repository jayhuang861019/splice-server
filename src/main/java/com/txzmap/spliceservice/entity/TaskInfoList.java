package com.txzmap.spliceservice.entity;


import com.txzmap.spliceservice.util.TileUtil;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务信息
 */


@Component
public class TaskInfoList {
    public enum TaskStatus {
        START, RUNNING, DOWNLOADING, SPLICING, FINISHED, ERROR
    }


    //任务信息类
    @Data
    public static class TaskInfo {
        String uuid;
        String name = "";
        Long startTime;
        Long endTime;
        TaskStatus status;
        MapSource mapSource;
        //地图源id
        Integer mapSourceId;
        GeoPos leftTop, rightBottom;
        //网格类型 默认为0
        Integer grid = 0;
        Integer zoom = 0;
        Integer total = 0;
        Integer process = 0;
        Integer size = 0;
        //最后生成的文件存放的地址
        String url = "";

        //颜色值 默认为黑色
        String gridColor = "#000000";
        Integer gridWidth = 1;

        //是否是vip
        Boolean vip = false;


        public Integer getMinX() {
            if (leftTop == null || rightBottom == null)
                return 0;
            int[] leftTopXY = TileUtil.getTileXY(leftTop.getLat(), leftTop.getLng(), zoom);
            int[] rightBottomXY = TileUtil.getTileXY(rightBottom.getLat(), rightBottom.getLng(), zoom);
            return Math.min(leftTopXY[0], rightBottomXY[0]);
        }

        public Integer getMaxX() {
            if (leftTop == null || rightBottom == null)
                return 0;

            int[] leftTopXY = TileUtil.getTileXY(leftTop.getLat(), leftTop.getLng(), zoom);
            int[] rightBottomXY = TileUtil.getTileXY(rightBottom.getLat(), rightBottom.getLng(), zoom);

            return Math.max(leftTopXY[0], rightBottomXY[0]);

        }

        public Integer getMinY() {
            if (leftTop == null || rightBottom == null)
                return 0;
            int[] leftTopXY = TileUtil.getTileXY(leftTop.getLat(), leftTop.getLng(), zoom);
            int[] rightBottomXY = TileUtil.getTileXY(rightBottom.getLat(), rightBottom.getLng(), zoom);

            return Math.min(leftTopXY[1], rightBottomXY[1]);


        }

        public Integer getMaxY() {
            if (leftTop == null || rightBottom == null)
                return 0;

            int[] leftTopXY = TileUtil.getTileXY(leftTop.getLat(), leftTop.getLng(), zoom);
            int[] rightBottomXY = TileUtil.getTileXY(rightBottom.getLat(), rightBottom.getLng(), zoom);

            return Math.max(leftTopXY[1], rightBottomXY[1]);
        }

        public Integer getTotal() {
            this.total = (getMaxX() - getMinX() + 1) * (1 + getMaxY() - getMinY());
            return total;
        }

        @Override
        public String toString() {
            return "TaskInfo{" +
                    "uuid='" + uuid + '\'' +
                    ", name='" + name + '\'' +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", status=" + status +
                    ", mapSource=" + mapSource +
                    ", leftTop=" + leftTop.toString() +
                    ", rightBottom=" + rightBottom.toString() +
                    ", grid=" + grid +
                    ", zoom=" + zoom +
                    ", total=" + total +
                    ", process=" + process +
                    ", size=" + size +
                    ", url='" + url + '\'' +
                    ", gridColor='" + gridColor + '\'' +
                    '}';
        }
    }

    public static Map<String, TaskInfo> taskList = new HashMap<>();

    /**
     * 添加新任务并返回当前所有任务的总数
     *
     * @param task
     * @return
     */
    public Integer addNewTask(TaskInfo task) {
        taskList.put(task.getUuid(), task);
        return taskList.size();
    }


    /**
     * 更新任务状态
     *
     * @param task
     */
    public void update(TaskInfo task) {

        taskList.put(task.getUuid(), task);

    }


    /**
     * 根据uuid移除相关的任务信息
     *
     * @param task
     */
    public void remove(TaskInfo task) {

        taskList.remove(task.getUuid());
    }


    /**
     * 根据uuid查询
     *
     * @param uuid
     * @return
     */
    public TaskInfo search(String uuid) {
        return taskList.get(uuid);
    }

    //系统初始化的时候
    @PostConstruct
    private void init() {
    }

    //系统结束运行的时候
    @PreDestroy
    public void destroy() {
        //系统运行结束

        taskList.clear();
    }
}

