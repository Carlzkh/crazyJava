package com.example.crazyjava.common;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;


@Data
public class PageResult<T> {
    /*
    字段	类型	含义	示例值
    pageNum	int	当前页码	1
    pageSize	int	每页显示条数	10
    size	int	当前页实际有多少条数据	5（最后一页只有 5 条）
    startRow	int	当前页第一条数据在总数据中的位置（从 1 开始）	1
    endRow	int	当前页最后一条数据在总数据中的位置	5
    pages	int	总页数	1（总共只有 1 页）
    prePage	int	上一页页码	0（没有上一页）
    nextPage	int	下一页页码	0（没有下一页）
    isFirstPage	boolean	是否是第一页	true
    isLastPage	boolean	是否是最后一页	true
    hasPreviousPage	boolean	是否有上一页	false
    hasNextPage	boolean	是否有下一页	false
    navigatePages	int	导航栏显示的页码数量	8（配置项）
    navigatepageNums	int[]	导航栏显示的页码列表	[1]
    navigateFirstPage	int	导航栏第一页	1
    navigateLastPage	int	导航栏最后一页	1
    list	List<T>	当前页的数据列表	[{"id":1,...}, ...]（最重要的字段）
    想要什么，自己加
    * */

    private Long total;        // 总记录数
    private Integer pageNum;   // 当前页码
    private Integer pageSize;  // 每页条数
    private List<T> list;      // 当前页的数据列表

    // 构造方法：从 PageInfo 中提取我们需要的字段
    public PageResult(PageInfo<T> pageInfo) {
        this.total = pageInfo.getTotal();
        this.pageNum = pageInfo.getPageNum();
        this.pageSize = pageInfo.getPageSize();
        this.list = pageInfo.getList();
    }
}