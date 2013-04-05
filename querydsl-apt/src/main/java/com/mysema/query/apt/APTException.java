/*
 * Copyright 2011, Mysema Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mysema.query.apt;

/**
 * Exception thrown on APT processing errors
 * 
 * @author tiwe
 *
 */
public class APTException extends RuntimeException {

    private static final long serialVersionUID = -8456970330509020462L;

    public APTException(String msg) {
        super(msg);
    }

    public APTException(String msg, Throwable t) {
        super(msg, t);
    }

    public APTException(Throwable t) {
        super(t);
    }

}
