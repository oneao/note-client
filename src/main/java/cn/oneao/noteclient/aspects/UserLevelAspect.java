package cn.oneao.noteclient.aspects;

import cn.oneao.noteclient.enums.WebSocketMarkEnums;
import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.pojo.entity.UserLevel;
import cn.oneao.noteclient.pojo.entity.rabbitmq.RMUserLevelUpNotice;
import cn.oneao.noteclient.server.DirectSender;
import cn.oneao.noteclient.service.UserLevelService;
import cn.oneao.noteclient.service.UserService;
import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@Slf4j
public class UserLevelAspect {
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private UserService userService;
    @Autowired
    private DirectSender directSender;
    @Pointcut("@annotation(cn.oneao.noteclient.annotations.ObserveUserLevel)")
    public void pointCut() {
    }

    @AfterReturning(pointcut = "pointCut()", returning = "result")
    public void afterReturningAdvice(Object result) {
        int userId = UserContext.getUserId();
        if (userId == -1) {
            List<UserLevel> userLevelList = userLevelService.list();
            for (UserLevel userLevel : userLevelList) {
                updateLevelIfNeeded(userLevel);
            }
        } else {
            UserLevel userLevel = userLevelService.getOne(new LambdaQueryWrapper<UserLevel>().eq(UserLevel::getUserId, userId));
            updateLevelIfNeeded(userLevel);
        }
    }

    private void updateLevelIfNeeded(UserLevel userLevel) {
        Integer level = userLevel.getLevel();
        if (level == 6) {
            return;
        }
        Integer collectionNoteNumber = userLevel.getCollectionNoteNumber();
        Integer shareNoteNumber = userLevel.getShareNoteNumber();
        Integer shareNoteVisitNumber = userLevel.getShareNoteVisitNumber();
        Integer shareNoteCommentNumber = userLevel.getShareNoteCommentNumber();
        int computedLevel = level(collectionNoteNumber, shareNoteNumber, shareNoteVisitNumber, shareNoteCommentNumber);
        if (computedLevel > level) {
            userLevel.setLevel(computedLevel);
            userLevelService.updateById(userLevel);
            User user = userService.getById(userLevel.getUserId());
            user.setLevel(computedLevel);
            userService.updateById(user);
            //发送给rabbitmq
            RMUserLevelUpNotice rmUserLevelUpNotice = new RMUserLevelUpNotice();
            rmUserLevelUpNotice.setMark(WebSocketMarkEnums.USER_LEVEL_UP.getMark());
            rmUserLevelUpNotice.setUserId(user.getId());
            rmUserLevelUpNotice.setLevel(computedLevel);
            directSender.sendUserLevelUpNotice(rmUserLevelUpNotice);
        }
    }

    private int level(Integer collectionNoteNumber, Integer shareNoteNumber, Integer shareNoteVisitNumber, Integer shareNoteCommentNumber) {
        if (isLevel6(shareNoteVisitNumber, shareNoteCommentNumber)) {
            return 6;
        } else if (isLevel5(shareNoteVisitNumber, shareNoteCommentNumber)) {
            return 5;
        } else if (isLevel4(shareNoteVisitNumber, shareNoteCommentNumber)) {
            return 4;
        } else if (isLevel3(collectionNoteNumber, shareNoteNumber, shareNoteVisitNumber, shareNoteCommentNumber)) {
            return 3;
        } else if (isLevel2(collectionNoteNumber, shareNoteNumber, shareNoteVisitNumber, shareNoteCommentNumber)) {
            return 2;
        } else {
            return 1;
        }
    }

    private boolean isLevel2(Integer collectionNoteNumber, Integer shareNoteNumber, Integer shareNoteVisitNumber, Integer shareNoteCommentNumber) {
        return (collectionNoteNumber >= 5 && collectionNoteNumber < 10)
                || (shareNoteNumber >= 3 && shareNoteNumber < 6)
                || (shareNoteVisitNumber >= 10 && shareNoteVisitNumber < 30)
                || (shareNoteCommentNumber >= 5 && shareNoteCommentNumber < 15);
    }

    private boolean isLevel3(Integer collectionNoteNumber, Integer shareNoteNumber, Integer shareNoteVisitNumber, Integer shareNoteCommentNumber) {
        return (collectionNoteNumber >= 10 && collectionNoteNumber < 15)
                || (shareNoteNumber >= 6 && shareNoteNumber < 9)
                || (shareNoteVisitNumber >= 30 && shareNoteVisitNumber < 90)
                || (shareNoteCommentNumber >= 15 && shareNoteCommentNumber < 30);
    }

    private boolean isLevel4(Integer shareNoteVisitNumber, Integer shareNoteCommentNumber) {
        return (shareNoteVisitNumber >= 90 && shareNoteVisitNumber < 180)
                && (shareNoteCommentNumber >= 30 && shareNoteCommentNumber < 60);
    }

    private boolean isLevel5(Integer shareNoteVisitNumber, Integer shareNoteCommentNumber) {
        return (shareNoteVisitNumber >= 180 && shareNoteVisitNumber < 360)
                && (shareNoteCommentNumber >= 60 && shareNoteCommentNumber < 120);
    }

    private boolean isLevel6(Integer shareNoteVisitNumber, Integer shareNoteCommentNumber) {
        return (shareNoteVisitNumber >= 360)
                && (shareNoteCommentNumber >= 120);
    }
}
