package cloud.agileframework.security.menu;

import cloud.agileframework.common.util.collection.TreeBase;

import java.io.Serializable;

/**
 * @author 佟盟
 * 日期 2020-12-29 11:40
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractMenuDetail<I extends Serializable> extends TreeBase<I> {

    /**
     * 描述
     *
     * @return 描述
     */
    public abstract String getDescription();

    /**
     * 显示名
     *
     * @return 显示名
     */
    public abstract String getTitle();

    /**
     * 英文名
     *
     * @return 英文名
     */
    public abstract String getEnName();

    /**
     * 路由路径
     *
     * @return 路由路径
     */
    public abstract String getPath();

    /**
     * 组件
     *
     * @return 组件
     */
    public abstract String getComponent();

    /**
     * 图标
     *
     * @return 图标
     */
    public abstract String getIcon();
}
