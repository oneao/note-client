package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.constant.RedisKeyConstant;
import cn.oneao.noteclient.enums.NoteActionEnums;
import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.mapper.NoteShareMapper;
import cn.oneao.noteclient.pojo.dto.note.NoteShareAddDTO;
import cn.oneao.noteclient.pojo.dto.note.NoteShareGetDTO;
import cn.oneao.noteclient.pojo.dto.note.NoteShareLickDTO;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.pojo.entity.log.NoteLog;
import cn.oneao.noteclient.pojo.entity.note.Note;
import cn.oneao.noteclient.pojo.entity.note.NoteShare;
import cn.oneao.noteclient.pojo.vo.NoteShareVO;
import cn.oneao.noteclient.server.DirectSender;
import cn.oneao.noteclient.service.NoteService;
import cn.oneao.noteclient.service.NoteShareService;
import cn.oneao.noteclient.service.UserService;
import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
import cn.oneao.noteclient.utils.IPUtil;
import cn.oneao.noteclient.utils.RedisCache;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class NoteShareServiceImpl extends ServiceImpl<NoteShareMapper, NoteShare> implements NoteShareService {
    @Autowired
    private NoteShareMapper noteShareMapper;
    @Autowired
    private NoteService noteService;
    @Autowired
    private UserService userService;
    @Autowired
    private DirectSender directSender;
    @Autowired
    private RedisCache redisCache;
    @Value("${front.share.address}")
    private String shareUrl;

    @Override
    public Result<Object> getNoteIsShare(Integer noteId) {
        if (ObjectUtils.isEmpty(noteId)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        LambdaQueryWrapper<NoteShare> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NoteShare::getNoteId, noteId);
        NoteShare noteShare = this.getOne(queryWrapper);
        if (ObjectUtils.isEmpty(noteShare)) {
            return Result.success(ResponseEnums.NOTE_SHARE_ALLOW_ADD);
        }
        Integer isShare = noteShare.getIsShare();
        //分享过了
        if (isShare == 1) {
            Note note = noteService.getById(noteId);
            Integer userId = note.getUserId();
            //已经分享过了
            String noteShareKey = "NOTE_SHARE_USERID:" + userId + "_NOTEID:" + noteId;
            if (!redisCache.hasKey(noteShareKey)) {
                return Result.success(ResponseEnums.NOTE_SHARE_ALLOW_ADD);
            } else {
                String url = redisCache.getCacheObject(noteShareKey);
                return Result.success(url, ResponseEnums.NOTE_SHARE_NOT_ALLOW_ADD);
            }
        }
        //没有分享过，直接允许添加
        return Result.success(ResponseEnums.NOTE_SHARE_ALLOW_ADD);
    }

    @Override
    public Result<Object> addNoteShare(NoteShareAddDTO noteShareAddDTO) {
        Integer noteId = noteShareAddDTO.getNoteId();
        if (ObjectUtils.isEmpty(noteId)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }

        String noteShareKey = "NOTE_SHARE_USERID:" + UserContext.getUserId() + "_NOTEID:" + noteId;
        if (redisCache.hasKey(noteShareKey)) {
            return Result.success(redisCache.getCacheObject(noteShareKey), ResponseEnums.NOTE_SHARE_ERROR);
        }

        NoteShare noteShare = new NoteShare();
        BeanUtils.copyProperties(noteShareAddDTO, noteShare);
        noteShare.setIsShare(1);//分享
        Integer noteShareTime = noteShare.getNoteShareTime();//获取存储时间


        noteShare.setNoteLikeNumber(0);//初始点赞数量为0
        noteShare.setNoteShareThatTime(new Date());
        this.save(noteShare);

        String url = shareUrl + "?n_sid=" + noteShare.getId();

        if (noteShareTime == -1) {
            //永久存储
            redisCache.setCacheObject(noteShareKey, url);
        } else {
            //按照天数存储
            redisCache.setCacheObject(noteShareKey, url, noteShareTime, TimeUnit.DAYS);
        }

        //日志
        NoteLog noteLog = new NoteLog();
        noteLog.setUserId(UserContext.getUserId());
        noteLog.setSmallNoteId(noteId);
        noteLog.setAction(NoteActionEnums.USER_SHARE_NOTE.getActionName());
        noteLog.setActionDesc(NoteActionEnums.USER_SHARE_NOTE.getActionDesc());
        //返回成功信息。
        return Result.success(url, ResponseEnums.NOTE_SHARE_SUCCESS);
    }

    //获取分享笔记的信息
    @Override
    public Result<Object> getShareNote(NoteShareGetDTO noteShareGetDTO, HttpServletRequest httpServletRequest) {
        Integer id = noteShareGetDTO.getN_sid();
        String noteSharePassword = noteShareGetDTO.getNoteSharePassword();
        if (ObjectUtils.isEmpty(id)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        //获取分享笔记的内容
        NoteShare noteShare = this.getById(id);
        //检验是否需要锁
        Integer isNeedPassword = noteShare.getIsNeedPassword();
        if (isNeedPassword == 1) {
            if (ObjectUtils.isEmpty(noteSharePassword)) {
                return Result.error(ResponseEnums.NOTE_SHARE_LOCK_PASSWORD_ERROR);
            }
            //检查输入的密码是否相等
            if (!noteSharePassword.equals(noteShare.getNoteSharePassword())) {
                return Result.error(ResponseEnums.NOTE_SHARE_LOCK_PASSWORD_ERROR);
            }
        }
        //更新访问量
        Integer noteShareVisitNumber = noteShare.getNoteShareVisitNumber();
        if (ObjectUtils.isEmpty(noteShareVisitNumber)) {
            noteShareVisitNumber = 0;
        }
        LambdaUpdateWrapper<NoteShare> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NoteShare::getId, noteShare.getId());
        updateWrapper.set(NoteShare::getNoteShareVisitNumber, ++noteShareVisitNumber);
        this.update(new NoteShare(), updateWrapper);
        //密码相等，然后就需要输出分享笔记的信息.
        NoteShareVO noteShareVO = new NoteShareVO();
        noteShareVO.setNoteShareTitle(noteShare.getNoteShareTitle());//标题
        noteShareVO.setNoteShareTags(noteShare.getNoteShareTags());//标签
        noteShareVO.setNoteShareRemark(noteShare.getNoteShareRemark());//备注
        noteShareVO.setNoteLikeNumber(noteShare.getNoteLikeNumber());//点赞数量
        noteShareVO.setNoteShareTime(noteShare.getNoteShareTime());//分享天数
        noteShareVO.setNoteShareThatTime(noteShare.getNoteShareThatTime());//分享的时间
        noteShareVO.setNoteShareVisitNumber(noteShareVisitNumber);  //访问量
        //获取笔记内容
        Note note = noteService.getById(noteShare.getNoteId());
        noteShareVO.setNoteShareContent(note.getNoteContent());
        //获取用户的昵称
        User user = userService.getById(note.getUserId());
        noteShareVO.setNickName(user.getNickName());
        //检查该用户是否进行了点赞
        String redisKey = RedisKeyConstant.SHARE_NOTE_LIKE_IP_NSID + noteShare.getId();
        if (redisCache.hasKey(redisKey)) {
            Set<Object> cacheSet = redisCache.getCacheSet(redisKey);
            String clientIP = IPUtil.getIpAddr(httpServletRequest);
            if (cacheSet.contains(clientIP)) {
                noteShareVO.setIsLike(1);//点赞
            } else {
                noteShareVO.setIsLike(0);//未点赞
            }
        } else {
            noteShareVO.setIsLike(0); //该ip未进行点赞
        }
        return Result.success(noteShareVO, ResponseEnums.NOTE_SHARE_GET_SUCCESS);
    }

    //检查笔记是否需要锁
    @Override
    public Result<Object> getShareNoteIsLock(Integer n_sid) {
        if (ObjectUtils.isEmpty(n_sid)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        NoteShare noteShare = this.getById(n_sid);
        //判断是否能查找到
        if (ObjectUtils.isEmpty(noteShare)) {
            return Result.error(ResponseEnums.NOTE_SHARE_NOT_EXISTS);
        }
        //判断是否分享了没有，可能发布人取消了分享
        if (noteShare.getIsShare() == 0) {
            return Result.error(ResponseEnums.NOTE_SHARE_OVER);
        }
        //检查是否有锁
        Integer isNeedPassword = noteShare.getIsNeedPassword();
        //有锁
        if (isNeedPassword == 1) {
            Integer noteId = noteShare.getNoteId();
            Note note = noteService.getById(noteId);
            if (ObjectUtils.isEmpty(note)) {
                return Result.error(ResponseEnums.NOTE_SHARE_OVER);
            }
            Integer userId = note.getUserId();
            String noteShareKey = "NOTE_SHARE_USERID:" + userId + "_NOTEID:" + noteId;
            if (!redisCache.hasKey(noteShareKey)) {
                return Result.error(ResponseEnums.NOTE_SHARE_OVER);
            } else {
                return Result.success(ResponseEnums.NOTE_SHARE_NEED_LOCK);
            }
        }
        //没锁
        return Result.success(ResponseEnums.NOTE_SHARE_NOT_NEED_LOCK);
    }

    //点赞和取消点赞
    @Override
    public Result<Object> goToLick(NoteShareLickDTO noteShareLickDTO, HttpServletRequest httpServletRequest) {
        Integer id = noteShareLickDTO.getId();
        Integer likeHeart = noteShareLickDTO.getLikeHeart();
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(likeHeart)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }

        NoteShare noteShare = this.getById(id);
        Integer noteLikeNumber = noteShare.getNoteLikeNumber();
        String redisKey = RedisKeyConstant.SHARE_NOTE_LIKE_IP_NSID + id;
        String lickIp = IPUtil.getIpAddr(httpServletRequest); //用户的ip地址
        if (redisCache.hasKey(redisKey)) {
            Set<String> likeIpSet = redisCache.getCacheSet(redisKey);
            boolean flag = likeIpSet.add(lickIp);
            if (!flag) {
                //取消点赞:就需要移除IP
                redisCache.deleteCacheSetValue(redisKey, lickIp);//移除ip
            } else {
                redisCache.setCacheSetValue(redisKey, lickIp);
            }
        } else {
            Set<String> set = new HashSet<>();
            set.add(lickIp);
            redisCache.setCacheSet(redisKey, set);
        }
        if (likeHeart == 1) {
            noteLikeNumber = noteLikeNumber + 1;
        } else {
            noteLikeNumber = noteLikeNumber - 1;
        }
        LambdaUpdateWrapper<NoteShare> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NoteShare::getId, id);
        updateWrapper.set(NoteShare::getNoteLikeNumber, noteLikeNumber);
        this.update(new NoteShare(), updateWrapper);
        //获取笔记拥有者的id TODO:可以对note_share表进行优化。
        Integer noteId = noteShare.getNoteId();
        Note note = noteService.getById(noteId);
        Integer userId = note.getUserId();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(date);
        if (likeHeart == 1) {
            //前端及时响应
            String likeMsg = "";
            //存储到redis中
            String likeMessage = formattedDate + "^" + "你的文章《" + noteShare.getNoteShareTitle() + "》收获到了一个赞哦😋";
            String redisKeyMessage = RedisKeyConstant.SHARE_NOTE_LIKE_MESSAGE_UID + userId;
            if (redisCache.hasKey(redisKeyMessage)) {
                List<String> cacheList = redisCache.getCacheList(redisKeyMessage);
                int size = cacheList.size();
                redisCache.addCacheListValue(redisKeyMessage, size + "^" + likeMessage);
                likeMsg = userId + "^" + size + "^" + formattedDate + "^" + "你的文章《" + noteShare.getNoteShareTitle() + "》收获到了一个赞哦😋";
            } else {
                List<String> list = new ArrayList<>();
                list.add(0 + "^" + likeMessage);
                redisCache.setCacheList(redisKeyMessage, list);
                likeMsg = userId + "^" + 0 + "^" + formattedDate + "^" + "你的文章《" + noteShare.getNoteShareTitle() + "》收获到了一个赞哦😋";
            }
            directSender.sendDirect(likeMsg);
            return Result.success(ResponseEnums.NOTE_SHARE_LICK_SUCCESS);
        } else {
            //前端及时响应
            String likeMessage = formattedDate + "^" + "你的文章《" + noteShare.getNoteShareTitle() + "》减少了一个赞😭";
            String redisKeyMessage = RedisKeyConstant.SHARE_NOTE_LIKE_MESSAGE_UID + userId;
            String likeMsg = "";
            if (redisCache.hasKey(redisKeyMessage)) {
                List<Object> cacheList = redisCache.getCacheList(redisKeyMessage);
                int size = cacheList.size();
                redisCache.addCacheListValue(redisKeyMessage,size + "^" + likeMessage);
                likeMsg = userId + "^" + size + "^" + formattedDate + "^" + "你的文章《" + noteShare.getNoteShareTitle() + "》减少了一个赞😭";
            } else {
                List<String> list = new ArrayList<>();
                list.add(0+"^"+likeMessage);
                redisCache.setCacheList(redisKeyMessage, list);
                likeMsg =  userId + "^" + 0 + "^" + formattedDate + "^" + "你的文章《" + noteShare.getNoteShareTitle() + "》减少了一个赞😭";
            }
            directSender.sendDirect(likeMsg);
            return Result.success(ResponseEnums.NOTE_SHARE_LICK_CANCEL_SUCCESS);
        }
    }
}
