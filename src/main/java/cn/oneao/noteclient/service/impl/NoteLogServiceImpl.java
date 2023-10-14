package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.mapper.NoteLogMapper;
import cn.oneao.noteclient.pojo.entity.NoteLog;
import cn.oneao.noteclient.service.NoteLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class NoteLogServiceImpl extends ServiceImpl<NoteLogMapper, NoteLog> implements NoteLogService {
}
