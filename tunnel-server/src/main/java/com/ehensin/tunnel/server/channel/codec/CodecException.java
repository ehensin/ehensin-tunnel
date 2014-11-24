/*
 * Copyright 2013 The Ehensin Project
 *
 * The Ehensin Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ehensin.tunnel.server.channel.codec;

import com.ehensin.tunnel.server.exception.BaseException;

public class CodecException extends BaseException {
	private static final long serialVersionUID = 1L;

	public CodecException(String message) {
		super(message);
	}
	public CodecException(String message, String errorCode) {
		super(message, errorCode);
	}

	public CodecException(String message, Throwable rootCause) {
		super(message, rootCause);
	}
	public CodecException(String message, Throwable rootCause, String errorCode) {
		super(message, rootCause, errorCode);
	}

	public CodecException(Throwable rootCause) {
		super(rootCause);
	}
}
