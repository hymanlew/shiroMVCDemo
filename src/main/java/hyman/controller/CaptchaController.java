package hyman.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import hyman.entity.ResponseData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class CaptchaController extends BaseController {

    /**
     *
     * <p><b>方法描述：</b>获取图形验证码方法</p>
     * @param request 请求对象
     * @param response 响应对象
     * @return 图形验证码
     * @throws IOException IO异常
     */
    @RequestMapping("captcha/image")
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 验证码生成器
        Producer captchaProducer = (Producer) ContextLoader.getCurrentWebApplicationContext()
                .getBean("captchaProducer");
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        // return a jpeg
        response.setContentType("image/jpeg");
        // create the text for the image
        String capText = captchaProducer.createText();
        // store the text in the session and database.
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        // appIdentityService.saveCaptcha(appUser);
        // create the image with the text
        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        // write the data out
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;
    }

    /**
     *
     * <p><b>方法描述：</b>验证图形验证码</p>
     * @param request 请求对象
     * @return 返回结果
     */
    @ResponseBody
    @RequestMapping(value = "/login/verifyImgCode")
    public ResponseData verifyImgCode(HttpServletRequest request) {
        String paramCode = request.getParameter("code");
        if(request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY) == null || StringUtils.isNoneBlank(paramCode)){
            return this.operateFailed("验证码错误，请重新输入");
        }

        String serverCode = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (!serverCode.equals(paramCode)) {
            return this.operateFailed("验证码错误，请重新输入");
        }
        return this.operateSucess();
    }
}
