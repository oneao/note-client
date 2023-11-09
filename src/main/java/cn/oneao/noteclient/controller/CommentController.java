package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.pojo.dto.comment.CommentAddDTO;
import cn.oneao.noteclient.pojo.dto.comment.CommentQueryDTO;
import cn.oneao.noteclient.service.CommentService;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 新增评论
     * @param httpServletRequest 获取评论地址
     * @param file  文件
     * @param data  数据
     * @return 返回添加评论的信息
     */
    @PostMapping(value = "/addComment")
    public Result<Object> addComment(HttpServletRequest httpServletRequest, @RequestPart(required = false) MultipartFile file, @RequestPart CommentAddDTO data){
        return commentService.addComment(httpServletRequest,file,data);
    }
    /**
     * 查询：分页
     * @param commentQueryDTO 查询对象
     * @return 返回评论树形集合
     */
    @PostMapping("/getComments")
    public Result<Object> getComments(@RequestBody CommentQueryDTO commentQueryDTO){
        return commentService.getComments(commentQueryDTO);
    }
    @PatchMapping("/goToLike/{id}/{isLike}")
    public Result<Object> goToLike(@PathVariable("id") Integer id,@PathVariable("isLike") Integer isLike){
        return commentService.goToLike(id,isLike);
    }
}
