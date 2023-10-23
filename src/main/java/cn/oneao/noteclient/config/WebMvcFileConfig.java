package cn.oneao.noteclient.config;

import cn.oneao.noteclient.constant.FileConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcFileConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/userAvatar/**")
                .addResourceLocations(FileConstants.USER_AVATAR);
        registry.addResourceHandler("/img/userNoteImage/**")
                .addResourceLocations(FileConstants.USER_NOTE_IMAGE);
    }

}
