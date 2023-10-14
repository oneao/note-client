package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.dto.SmallNoteDeleteDTO;
import cn.oneao.noteclient.pojo.dto.SmallNotePageDTO;
import cn.oneao.noteclient.pojo.dto.SmallNoteTopStatusDTO;
import cn.oneao.noteclient.pojo.entity.SmallNote;
import cn.oneao.noteclient.pojo.vo.SmallNoteVO;
import cn.oneao.noteclient.utils.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SmallNoteService extends IService<SmallNote> {
    PageResult<SmallNoteVO> getSmallNoteInfo(SmallNotePageDTO smallNotePageDTO);

    void changeSmallNoteStatus(SmallNoteTopStatusDTO smallNoteTopStatusDTO);

    void deleteSmallNote(SmallNoteDeleteDTO smallNoteDeleteDTO);
}
