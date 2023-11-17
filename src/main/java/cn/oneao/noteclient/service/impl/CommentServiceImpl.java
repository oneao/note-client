package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.annotations.ObserveUserLevel;
import cn.oneao.noteclient.constant.RedisKeyConstant;
import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.enums.WebSocketMarkEnums;
import cn.oneao.noteclient.mapper.CommentMapper;
import cn.oneao.noteclient.mapper.NoteMapper;
import cn.oneao.noteclient.mapper.NoteShareMapper;
import cn.oneao.noteclient.pojo.dto.comment.CommentAddDTO;
import cn.oneao.noteclient.pojo.dto.comment.CommentQueryDTO;
import cn.oneao.noteclient.pojo.entity.UserLevel;
import cn.oneao.noteclient.pojo.entity.comment.Comment;
import cn.oneao.noteclient.pojo.entity.comment.CommentUser;
import cn.oneao.noteclient.pojo.entity.note.Note;
import cn.oneao.noteclient.pojo.entity.note.NoteShare;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyMessage;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMCommentReplyNotice;
import cn.oneao.noteclient.pojo.vo.*;
import cn.oneao.noteclient.server.DirectSender;
import cn.oneao.noteclient.service.CommentService;
import cn.oneao.noteclient.service.CommentUserService;
import cn.oneao.noteclient.service.UserLevelService;
import cn.oneao.noteclient.utils.IPUtil;
import cn.oneao.noteclient.utils.MinioUtil;
import cn.oneao.noteclient.utils.PhysicalAddressUtil;
import cn.oneao.noteclient.utils.RedisCache;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private MinioUtil minioUtil;
    @Value("${minio.endpoint}")
    private String address;
    @Value("${minio.bucketName}")
    private String bucketName;
    @Value("${front.share.address}")
    private String shareUrl;
    @Autowired
    private CommentUserService commentUserService;
    @Autowired
    private DirectSender directSender;//rabbitmq发送
    @Autowired
    private NoteShareMapper noteShareMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserLevelService userLevelService;
    //新增评论
    @Override
    @ObserveUserLevel
    public Result<Object> addComment(HttpServletRequest httpServletRequest, MultipartFile multipartFile, CommentAddDTO commentAddDTO) {
        Integer commentUserId = commentAddDTO.getCommentUserId();//评论用户表id
        Integer noteShareId = commentAddDTO.getNoteShareId();//笔记分享表id
        Integer parentId = commentAddDTO.getParentId();//父级id
        String content = commentAddDTO.getContent();//内容
        if (ObjectUtils.isEmpty(commentUserId) || ObjectUtils.isEmpty(noteShareId)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        Comment comment = new Comment();
        comment.setCommentUserId(commentUserId);
        comment.setNoteShareId(noteShareId);
        comment.setContent(content);
        //设置顶级评论id为0
        if (ObjectUtils.isEmpty(parentId)) {
            parentId = 0;
        }
        comment.setParentId(parentId);
        //图片
        String imgUrl = "";
        if (!ObjectUtils.isEmpty(multipartFile)) {
            String fileName = minioUtil.upload(multipartFile);
            imgUrl = address + "/" + bucketName + "/" + fileName;
            comment.setContentImg(imgUrl);
        }
        //ip地址
        String ipAddr = IPUtil.getIpAddr(httpServletRequest);
        //转化为具体地址
        //String physicalAddress = PhysicalAddressUtil.getPhysicalAddress(ipAddr);
        String physicalAddress = "测试地址";
        comment.setAddress(physicalAddress);
        //点赞数
        comment.setLikes(0);
        this.save(comment);
        //更新UserLevel表
        Integer userId = noteShareMapper.getUserIdByNoteShareId(noteShareId);
        UserLevel userLevel = userLevelService.getOne(new LambdaQueryWrapper<UserLevel>().eq(UserLevel::getUserId, userId));
        Integer shareNoteCommentNumber = userLevel.getShareNoteCommentNumber();
        userLevel.setShareNoteCommentNumber(shareNoteCommentNumber + 1);
        userLevelService.updateById(userLevel);
        //创建vo视图对象
        CommentVO commentVO = new CommentVO();
        commentVO.setId(comment.getId());
        commentVO.setParentId(parentId);
        commentVO.setUid(commentUserId);
        commentVO.setAddress(physicalAddress);
        commentVO.setLikes(0);
        commentVO.setContent(content);
        commentVO.setContentImg(imgUrl);
        commentVO.setCreateTime(comment.getCreateTime());
        //创建用户vo对象
        CommentUserVO commentUserVO = new CommentUserVO();
        CommentUser commentUser = commentUserService.getById(commentUserId);
        commentUserVO.setUsername(commentUser.getCommentUserName());    //用户名
        commentUserVO.setAvatar(commentUser.getCommentUserAvatar());      //头像
        commentUserVO.setLevel(commentUser.getCommentUserLevel());       //等级
        commentVO.setCommentUserVO(commentUserVO);
        if (parentId != 0){
            String commentUserEmail = commentUser.getCommentUserEmail();//邮箱号
            Comment parentComment = this.getById(parentId);
            String parentContent = parentComment.getContent();//内容
            String url = shareUrl + "?n_sid=" + noteShareId;//链接
            //noteShareId
            RMCommentReplyMessage rmCommentReplyMessage = new RMCommentReplyMessage(commentUserEmail,url,parentContent);
            //发送rabbitmq通知
            directSender.sendCommentReplyMessage(rmCommentReplyMessage);
        }else {
            NoteShare noteShare = noteShareMapper.selectById(noteShareId);
            Note note = noteMapper.selectById(noteShare.getNoteId());
            RMCommentReplyNotice rmCommentReplyNotice = new RMCommentReplyNotice();
            rmCommentReplyNotice.setUserId(note.getUserId());
            rmCommentReplyNotice.setTime(comment.getCreateTime());
            rmCommentReplyNotice.setMessage(noteShare.getNoteShareTitle());
            //存储到redis中
            String redisKey = RedisKeyConstant.SHARE_NOTE_COMMENT_UID + note.getUserId();
            if(!redisCache.hasKey(redisKey)){
                List<RMCommentReplyNotice> list = new ArrayList<>();
                rmCommentReplyNotice.setIndex(0);
                list.add(rmCommentReplyNotice);
                redisCache.setCacheList(redisKey,list);
            }else{
                List<RMCommentReplyNotice> cacheList = redisCache.getCacheList(redisKey);
                int size = cacheList.size();
                rmCommentReplyNotice.setIndex(size);
                redisCache.addCacheListObjectValue(redisKey,rmCommentReplyNotice);
            }
            rmCommentReplyNotice.setMark(WebSocketMarkEnums.COMMENT_REPLY_NOTICE.getMark());
            directSender.sendCommentReplyNotice(rmCommentReplyNotice);
        }
        return Result.success(commentVO);
    }
    //获取评论列表
    @Override
    public Result<Object> getComments(CommentQueryDTO commentQueryDTO) {
        Integer pageNum = commentQueryDTO.getPageNum();
        Integer pageSize = commentQueryDTO.getPageSize();
        Integer isLatest = commentQueryDTO.getIsLatest();
        if (ObjectUtils.isEmpty(isLatest)) {
            isLatest = 1;
        }
        Integer noteShareId = commentQueryDTO.getNoteShareId();
        if (ObjectUtils.isEmpty(noteShareId)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getNoteShareId, noteShareId);
        if (isLatest == 0) {
            queryWrapper.orderByDesc(Comment::getLikes);
        }
        queryWrapper.orderByDesc(Comment::getCreateTime);
        List<Comment> commentList = this.list(queryWrapper);//查询出来的评论列表
        List<CommentsVO> commentsList = new ArrayList<>();//最终结果集
        // 获取顶级评论
        List<Comment> topLevelComments = getTopLevelComments(commentList);
        // 构建评论树
        for (Comment comment : topLevelComments) {
            CommentsVO commentsVO = buildCommentTree(comment, commentList);
            commentsList.add(commentsVO);
        }
        //总记录数
        int total = commentsList.size();
        List<CommentsVO> pagedComments = commentsList.stream()
                .skip((long) (pageNum - 1) * pageSize) // 跳过前面的 (pageNum - 1) * pageSize 条记录
                .limit(pageSize) // 限制结果集的大小为 pageSize
                .toList(); // 将结果收集到一个新的 List 中

        //结果
        CommentsPageVO commentsPageVO = new CommentsPageVO();
        commentsPageVO.setTotal(total);
        commentsPageVO.setResult(pagedComments);
        return Result.success(commentsPageVO);
    }
    private CommentsVO buildCommentTree(Comment comment, List<Comment> commentList) {
        CommentsVO commentsVO = new CommentsVO();
        commentsVO.setId(comment.getId());
        commentsVO.setParentId(comment.getParentId());
        commentsVO.setUid(comment.getCommentUserId());
        commentsVO.setAddress(comment.getAddress());
        commentsVO.setContent(comment.getContent());
        commentsVO.setLikes(comment.getLikes());
        commentsVO.setContentImg(comment.getContentImg());
        commentsVO.setCreateTime(comment.getCreateTime());
        CommentUser commentUser = commentUserService.getById(comment.getCommentUserId());
        //创建vo
        CommentUserVO commentUserVO = new CommentUserVO();
        commentUserVO.setUsername(commentUser.getCommentUserName());
        commentUserVO.setAvatar(commentUser.getCommentUserAvatar());
        commentUserVO.setLevel(commentUser.getCommentUserLevel());
        commentsVO.setUser(commentUserVO);

        List<CommentsVO> childComments = new ArrayList<>();
        for (Comment childComment : commentList) {
            if (childComment.getParentId() != null && childComment.getParentId().equals(comment.getId())) {
                CommentsVO childCommentsVO = buildCommentTree(childComment, commentList);
                childComments.add(childCommentsVO);
            }
        }
        commentsVO.setReply(new CommentsReplyVO(childComments.size(), childComments));

        return commentsVO;
    }
    private List<Comment> getTopLevelComments(List<Comment> commentList) {
        List<Comment> topLevelComments = new ArrayList<>();
        for (Comment comment : commentList) {
            if (comment.getParentId() == 0) {
                topLevelComments.add(comment);
            }
        }
        return topLevelComments;
    }
    //点赞
    @Override
    @Transactional
    public Result<Object> goToLike(Integer id, Integer isLike) {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(isLike)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        Comment comment = this.getById(id);
        if (ObjectUtils.isEmpty(comment)) {
            return Result.error(ResponseEnums.UNKNOWN_ERROR);
        }
        Integer likes = comment.getLikes();
        if (isLike == 1) {
            likes = likes + 1;
        } else if (isLike == 0 && likes > 0) {
            likes = likes - 1;
        }
        LambdaUpdateWrapper<Comment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Comment::getId, id);
        updateWrapper.set(Comment::getLikes, likes);
        this.update(new Comment(), updateWrapper);
        return Result.success();
    }
}
