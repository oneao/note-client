package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.dto.SmallNoteTopStatusDTO;
import cn.oneao.noteclient.pojo.entity.SmallNote;
import cn.oneao.noteclient.pojo.vo.SmallNoteVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SmallNoteService extends IService<SmallNote> {
    List<SmallNoteVO> getSmallNoteInfo(Integer userId);

    void changeSmallNoteStatus(SmallNoteTopStatusDTO smallNoteTopStatusDTO);
}
