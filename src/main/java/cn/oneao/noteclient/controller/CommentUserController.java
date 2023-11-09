package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.pojo.entity.comment.CommentUser;
import cn.oneao.noteclient.service.CommentUserService;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commentUser")
@Slf4j
public class CommentUserController {
    @Autowired
    private CommentUserService commentUserService;
    @PostMapping("/addCommentUser")
    public Result<Object> addCommentUser(@RequestBody CommentUser commentUser){
        return commentUserService.addCommentUser(commentUser);
    }
    @GetMapping("/getCommentUserLikes")
    public Result<Object> getCommentUserLikes(@RequestParam("id")Integer id){
        return commentUserService.getCommentUserLikes(id);
    }
    @PatchMapping("/goToLikes/{id}/{likeIds}")
    public Result<Object> goToLicks(@PathVariable("id")Integer id,@PathVariable("likeIds")String likeIds){
        return commentUserService.goToLikes(id,likeIds);
    }
}
