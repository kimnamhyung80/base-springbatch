package com.framework.springbatch.global.error.exception;

import com.framework.springbatch.global.error.ErrorCode;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException() {
        super(ErrorCode.RESOURCE_NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, 
              String.format("%s를 찾을 수 없습니다. (ID: %d)", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(ErrorCode.RESOURCE_NOT_FOUND, 
              String.format("%s를 찾을 수 없습니다. (%s)", resourceName, identifier));
    }
}
