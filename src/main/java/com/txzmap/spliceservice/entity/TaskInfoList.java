package com.txzmap.spliceservice.entity;


import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
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
    public static class TaskInfo {
        String uuid;
        String name = "";
        Long startTime;
        Long endTime;
        TaskStatus status;
        MapSource mapSource;
        Integer[] leftTop, rightBottom;
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

        public String getGridColor() {
            return gridColor;
        }

        public void setGridColor(String gridColor) {
            this.gridColor = gridColor;
        }


        public Integer getGrid() {
            return grid;
        }

        public void setGrid(Integer grid) {
            this.grid = grid;
        }

        public Integer getMinX() {
            if (leftTop == null || rightBottom == null)
                return 0;
            return Math.min(leftTop[0], rightBottom[0]);
        }

        public Integer getMaxX() {
            if (leftTop == null || rightBottom == null)
                return 0;
            return Math.max(leftTop[0], rightBottom[0]);
        }

        public Integer getMinY() {
            if (leftTop == null || rightBottom == null)
                return 0;
            return Math.min(leftTop[1], rightBottom[1]);
        }

        public Integer getMaxY() {
            if (leftTop == null || rightBottom == null)
                return 0;
            return Math.max(leftTop[1], rightBottom[1]);
        }

        public TaskInfo(String uuid, String name, MapSource mapSource, Integer[] leftTop, Integer[] rightBottom, Integer zoom, Integer grid, String gridColor) {
            this.uuid = uuid;
            this.name = name;
            this.mapSource = mapSource;
            this.leftTop = leftTop;
            this.rightBottom = rightBottom;
            this.zoom = zoom;
            this.total = (getMaxX() - getMinX() + 1) * (1 + getMaxY() - getMinY());
            this.grid = grid;
            this.gridColor = gridColor;
        }

        public MapSource getMapSource() {
            return mapSource;
        }

        public void setMapSource(MapSource mapSource) {
            this.mapSource = mapSource;
        }

        public Integer getZoom() {
            return zoom;
        }

        public void setZoom(Integer zoom) {
            this.zoom = zoom;
        }

        public Integer[] getLeftTop() {
            return leftTop;
        }

        public void setLeftTop(Integer[] leftTop) {
            this.leftTop = leftTop;
        }

        public Integer[] getRightBottom() {
            return rightBottom;
        }

        public void setRightBottom(Integer[] rightBottom) {
            this.rightBottom = rightBottom;
        }

        public TaskInfo(String uuid, String name) {
            this.uuid = uuid;
            this.name = name;
            this.status = TaskStatus.START;
        }

        public TaskInfo(String uuid, Integer total, Integer process, Integer size) {
            this.uuid = uuid;
            this.startTime = System.currentTimeMillis();
            this.total = total;
            this.process = process;
            this.size = size;
            this.status = TaskStatus.START;
        }

        public Long getStartTime() {
            return startTime;
        }

        public void setStartTime(Long startTime) {
            this.startTime = startTime;
        }

        public Long getEndTime() {
            return endTime;
        }

        public void setEndTime(Long endTime) {
            this.endTime = endTime;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }


        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getProcess() {
            return process;
        }

        public void setProcess(Integer process) {
            this.process = process;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public void setStatus(TaskStatus status) {
            this.status = status;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
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
                    ", leftTop=" + Arrays.toString(leftTop) +
                    ", rightBottom=" + Arrays.toString(rightBottom) +
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

