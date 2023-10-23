package cn.oneao.noteclient.utils.ResponseUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImageResult {
    private Integer errno;
    private Object data;
}
