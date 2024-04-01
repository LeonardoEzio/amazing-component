package leonardo.ezio.personal.job.constant;

/**
 * @author LeonardoEzio
 * @version v1.0.0 create at 2024-04-01 23:00
 */
public enum BlockStrategy {

    /**
     * 单机串行
     * */
    SERIAL_EXECUTION("单机串行"),

    /**
     * 丢弃后续调度
     * */
    DISCARD_LATER("丢弃后续调度"),

    /**
     * 覆盖前一个
     * */
    COVER_EARLY("覆盖前一个"),

    ;


    private final String desc;

    BlockStrategy(String desc) {
        this.desc = desc;
    }


    public String getDesc() {
        return desc;
    }
}
