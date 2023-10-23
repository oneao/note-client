package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.service.UserService;
import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
import cn.oneao.noteclient.utils.MinioUtil;
import cn.oneao.noteclient.utils.ResponseUtils.Image;
import cn.oneao.noteclient.utils.ResponseUtils.ImageResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/image")
@Slf4j
public class ImageController {
    @Autowired
    private UserService userService;
    @Autowired
    private MinioUtil MinioUtil;
    @Value("${minio.endpoint}")
    private String address;
    @Value("${minio.bucketName}")
    private String bucketName;
    @PostMapping("/upload")
    public ImageResult uploadImage(@RequestParam("file") MultipartFile multipartFile){
        String fileName = MinioUtil.upload(multipartFile);
        String url = address + "/" + bucketName + "/" + fileName;
        Image image = new Image();
        image.setUrl(url);
        ImageResult imageResult = new ImageResult();
        imageResult.setErrno(0);
        imageResult.setData(image);
        return imageResult;
    }
    //@PostMapping("/upload")
    public ImageResult uploadImage(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest httpServletRequest){
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = "";
        if (!ObjectUtils.isEmpty(originalFilename)) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String fileName = UUID.randomUUID().toString()+suffix;
        User user = userService.getById(UserContext.getUserId());
        String email = user.getEmail();
        String userNoteImagePath = "E:/Code/Mine/note/img/userNoteImage/" + email;
        File file = new File(userNoteImagePath);
        if (!file.exists()){
            if (file.mkdirs()){
                log.info("创建文件夹成功");
            }else{
                log.info("创建文件夹失败");
            }
        }
        ImageResult imageResult = new ImageResult();
        try {
            multipartFile.transferTo(new File(userNoteImagePath + "/" + fileName));
            Image image = new Image();
            image.setUrl("/img/userNoteImage/"+email+"/"+fileName);
            imageResult.setErrno(0);
            imageResult.setData(image);
        }catch (IOException e){
            imageResult.setData(1);
            e.printStackTrace();
        }
        return imageResult;
    }

}
