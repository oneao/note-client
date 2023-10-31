package cn.oneao.noteclient.mapper;

import cn.oneao.noteclient.pojo.entity.RecycleBin;
import cn.oneao.noteclient.pojo.vo.RecycleBinVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecycleBinMapper extends BaseMapper<RecycleBin> {
    //记录
    List<RecycleBin> selectRecycleBin(@Param("userId") Integer userId,
                                      @Param("page") Integer page,
                                      @Param("pageSize") Integer pageSize,
                                      @Param("searchValue") String searchValue,
                                      @Param("classifyValue") String classifyValue);
    //总数
    Integer selectTotal(@Param("userId") Integer userId,
                        @Param("searchValue") String searchValue,
                        @Param("classifyValue") String classifyValue);
}
