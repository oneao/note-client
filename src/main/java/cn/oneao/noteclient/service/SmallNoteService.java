package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.dto.smallNote.*;
import cn.oneao.noteclient.pojo.entity.SmallNote;
import cn.oneao.noteclient.pojo.vo.SmallNoteOneVO;
import cn.oneao.noteclient.pojo.vo.SmallNoteVO;
import cn.oneao.noteclient.utils.ResponseUtils.PageResult;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SmallNoteService extends IService<SmallNote> {
    PageResult<SmallNoteVO> getSmallNoteInfo(SmallNotePageDTO smallNotePageDTO);

    void changeSmallNoteStatus(SmallNoteTopStatusDTO smallNoteTopStatusDTO);

    void deleteSmallNote(SmallNoteDeleteDTO smallNoteDeleteDTO);

    void addSmallNote(SmallNoteAddDTO smallNoteAddDTO);

    SmallNoteOneVO getOneSmallNote(Integer smallNoteId);

    void updateSmallNote(SmallNoteUpdateDTO smallNoteUpdateDTO);
    //获取小记中的待办事件的日期限制
    Result<Object> getSmallNoteForCalendar();
}
