package com.whw.footstones.config.exception;

import com.whw.footstones.common.exception.ExceptionEnums;
import lombok.Getter;
import lombok.ToString;

/**
 * 业务异常类
 * 此异常保留了异常栈追踪信息
 */
@Getter
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ExceptionEnums exceptionEnums;

    /**
     * 返回数据，业务异常也可返回相应数据
     */
    private Object data;

    public ServiceException(ExceptionEnums exceptionEnums) {
        this.exceptionEnums = exceptionEnums;
    }

    public ServiceException(Object data, ExceptionEnums exceptionEnums) {
        this.data = data;
        this.exceptionEnums = exceptionEnums;
    }


    public ServiceException(final int errorCode, final String message){
        this(new ExceptionEnums() {
            @Override
            public int getCode() {
                return errorCode;
            }

            @Override
            public String getMessage() {
                return message;
            }
        });
    }

    public ServiceException(final int errorCode, final String format, final Object... args){
        this(new ExceptionEnums() {
            @Override
            public int getCode() {
                return errorCode;
            }

            @Override
            public String getMessage() {
                String message = format;
                if (args != null && args.length > 0) {
                    for (int i = 0; i < args.length; i++) {
                        message = message.replaceFirst("\\{\\}",args[i] == null ? "" : args[i].toString());
                    }
                }
                return message;
            }
        });
    }

    public ServiceException(final ExceptionEnums exceptionEnums, final Object... args){
        this(new ExceptionEnums() {
            @Override
            public int getCode() {
                return exceptionEnums.getCode();
            }

            @Override
            public String getMessage() {
                String message = exceptionEnums.getMessage();
                if (args != null && args.length > 0) {
                    for (int i = 0; i < args.length; i++) {
                        message = message.replaceFirst("\\{\\}",args[i] == null ? "" : args[i].toString());
                    }
                }
                return message;
            }
        });
    }

    public static ServiceExceptionBuilder builder() {
        return new ServiceExceptionBuilder();
    }

    @ToString
    public static class ServiceExceptionBuilder {
        private ExceptionEnums exceptionEnums;
        private Object data;

        ServiceExceptionBuilder() {
        }

        public ServiceException.ServiceExceptionBuilder exceptionEnums(final int errorCode, final String message) {
            ExceptionEnums exceptionEnums = new ExceptionEnums() {
                @Override
                public int getCode() {
                    return errorCode;
                }

                @Override
                public String getMessage() {
                    return message;
                }
            };
            this.exceptionEnums = exceptionEnums;
            return this;
        }

        public ServiceException.ServiceExceptionBuilder exceptionEnums(final int errorCode, final String format, final Object... args) {
            ExceptionEnums exceptionEnums = new ExceptionEnums() {
                @Override
                public int getCode() {
                    return errorCode;
                }

                @Override
                public String getMessage() {
                    String message = format;
                    if (args != null && args.length > 0) {
                        for (int i = 0; i < args.length; i++) {
                            message = message.replaceFirst("\\{\\}", args[i] == null ? "" : args[i].toString());
                        }
                    }
                    return message;
                }
            };
            this.exceptionEnums = exceptionEnums;
            return this;
        }

        public ServiceException.ServiceExceptionBuilder exceptionEnums(final ExceptionEnums exceptionEnums, final Object... args) {
            this.exceptionEnums = new ExceptionEnums() {
                @Override
                public int getCode() {
                    return exceptionEnums.getCode();
                }

                @Override
                public String getMessage() {
                    String message = exceptionEnums.getMessage();
                    if (args != null && args.length > 0) {
                        for (int i = 0; i < args.length; i++) {
                            message = message.replaceFirst("\\{\\}",args[i] == null ? "" : args[i].toString());
                        }
                    }
                    return message;
                }
            };
            return this;
        }

        public ServiceException.ServiceExceptionBuilder exceptionEnums(ExceptionEnums exceptionEnums) {
            this.exceptionEnums = exceptionEnums;
            return this;
        }

        public ServiceException.ServiceExceptionBuilder data(Object data) {
            this.data = data;
            return this;
        }

        public ServiceException build() {
            return new ServiceException(this.data, this.exceptionEnums);
        }

    }

    public ExceptionEnums getExceptionEnums() {
        return exceptionEnums;
    }

    @Override
    public String getMessage() {
        if (exceptionEnums == null) {
            return super.getMessage();
        }
        return exceptionEnums.getMessage();
    }

    public Object getData() {
        return data;
    }

}
