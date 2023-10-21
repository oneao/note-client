package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.dto.*;
import cn.oneao.noteclient.pojo.entity.SmallNote;
import cn.oneao.noteclient.pojo.vo.SmallNoteOneVO;
import cn.oneao.noteclient.pojo.vo.SmallNoteVO;
import cn.oneao.noteclient.utils.ResponseUtils.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SmallNoteService extends IService<SmallNote> {
    PageResult<SmallNoteVO> getSmallNoteInfo(SmallNotePageDTO smallNotePageDTO);

    void changeSmallNoteStatus(SmallNoteTopStatusDTO smallNoteTopStatusDTO);

    void deleteSmallNote(SmallNoteDeleteDTO smallNoteDeleteDTO);

    void addSmallNote(SmallNoteAddDTO smallNoteAddDTO);

    SmallNoteOneVO getOneSmallNote(Integer smallNoteId);

    void updateSmallNote(SmallNoteUpdateDTO smallNoteUpdateDTO);
}
