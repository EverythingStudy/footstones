package com.whw.footstones.core.message.advisor;

import com.whw.footstones.core.error.DefaultSystemError;
import com.whw.footstones.core.error.IThirdServiceException;
import com.whw.footstones.core.message.OutputMessage;
import com.whw.footstones.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author
 * @description 异常处理
 * @creatTime
 * @since 1.0.0
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class ExceptionAdvisor {
    private final static Logger logger = LoggerFactory.getLogger(ExceptionAdvisor.class);

    /**
     * 业务异常拦截
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = ServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public OutputMessage businessExceptionHandler(ServiceException e) {
        logger.debug(e.getExceptionEnums().getMessage());
        return OutputMessage.error(e.getExceptionEnums(), e.getData());
    }


    /**
     * 参数异常，参考JSR303
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public OutputMessage argumentExceptionHandler(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();

        StringBuffer sb = new StringBuffer();
        sb.append(DefaultSystemError.BAD_PARAM.getMessage());
        if (!CollectionUtils.isEmpty(errors)) {
            for (ObjectError objectError : errors) {
                if (objectError instanceof FieldError) {
                    sb.append(((FieldError) objectError).getField()).append(objectError.getDefaultMessage()).append("\n");
                }
            }
        }

        logger.debug(sb.toString());
        return OutputMessage.error(DefaultSystemError.BAD_PARAM.getCode(), sb.toString());
    }

    /**
     * 参数异常，参考JSR303
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public OutputMessage bindExceptionHandler(BindException e) {
        logger.debug(e.getMessage());
        return OutputMessage.error(DefaultSystemError.BAD_PARAM.getCode(), e.getMessage());
    }

    /**
     * 参数异常，参考JSR303
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = ServletRequestBindingException.class)
    @ResponseStatus(HttpStatus.OK)
    public OutputMessage servletRequestParameterExceptionHandler(ServletRequestBindingException e) {
        logger.debug(e.getMessage());
        return OutputMessage.error(DefaultSystemError.BAD_PARAM.getCode(), e.getMessage());
    }


    /**
     * 参数
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public OutputMessage messageExceptionHandler(HttpMessageNotReadableException e) {
        logger.debug(e.getMessage());
        return OutputMessage.error(DefaultSystemError.BAD_PARAM);
    }

    /**
     * 默认的错误拦截
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    public OutputMessage throwableExceptionHandler(Throwable e) {
        if ((e != null && e instanceof ServiceException)) {
            return OutputMessage.error(((ServiceException) e).getExceptionEnums());
        }
        if ((e.getCause() != null && e.getCause() instanceof ServiceException)) {
            return OutputMessage.error(((ServiceException) e.getCause()).getExceptionEnums());
        }
        if ((e != null && e instanceof IThirdServiceException)) {
            return OutputMessage.error(((IThirdServiceException) e).getExceptionEnums());
        }
        if ((e.getCause() != null && e.getCause() instanceof IThirdServiceException)) {
            return OutputMessage.error(((IThirdServiceException) e.getCause()).getExceptionEnums());
        }
        logger.error(e.getMessage(), e);
        return OutputMessage.error(DefaultSystemError.SERVER_BUSY);
    }
}
