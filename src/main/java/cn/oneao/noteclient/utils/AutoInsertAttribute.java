package cn.oneao.noteclient.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AutoInsertAttribute implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("level",1,metaObject);
        this.setFieldValByName("status",0,metaObject);
        this.setFieldValByName("isDelete",0,metaObject);
        this.setFieldValByName("type",0,metaObject);
        this.setFieldValByName("isNewBuild",0,metaObject);
        this.setFieldValByName("isCollection",0,metaObject);
        this.setFieldValByName("isTop",0,metaObject);
        this.setFieldValByName("isLock",0,metaObject);
        this.setFieldValByName("commentUserLevel",1,metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("isNewBuild",1,metaObject);
    }

}
