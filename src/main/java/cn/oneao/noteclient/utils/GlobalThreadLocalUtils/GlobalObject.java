package cn.oneao.noteclient.utils.GlobalThreadLocalUtils;

import cn.oneao.noteclient.pojo.entity.log.SqlActionLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalObject {
    private Integer userId;
    private SqlActionLog sqlActionLog;
    private List<String> sqlStatements;
    private List<List<String>> sqlParams;
}
