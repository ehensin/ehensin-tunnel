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
package com.ehensin.tunnel.server.exception;

public class BaseException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Throwable rootCause;
	/*self defined error code*/
	protected String errorCode;

	/**
	 * construction.
	 * 
	 */
	public BaseException() {
		super();
	}

	/**
	 * construction with exception message 
	 * 
	 * @param message
	 *            a <code>String</code> exception message
	 * 
	 */
	public BaseException(String message) {
		super(message);
	}
	/**
	 * construction with exception message and error code 
	 * 
	 * @param message
	 *            a <code>String</code> exception message
	 * @param errorCode self defined error code.
	 * 
	 */
	public BaseException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 *construction with exception message and root cause
	 * 
	 * @param message
	 *            a <code>String</code> exception message
	 * 
	 * @param rootCause
	 *            the <code>Throwable</code> root cause
	 * 
	 */
	public BaseException(String message, Throwable rootCause) {
		super(message, rootCause);
		this.rootCause = rootCause;
	}
	/**
	 *construction with exception message , root cause and error code
	 * 
	 * @param message
	 *            a <code>String</code> exception message
	 * 
	 * @param rootCause
	 *            the <code>Throwable</code> root cause
	 *      
	 * @param errorCode
	 *            a <code>String</code> self defined errorCode
	 */
	public BaseException(String message, Throwable rootCause, String errorCode) {
		super(message, rootCause);
		this.rootCause = rootCause;
		this.errorCode = errorCode;
	}

	/**
	 *construction with root cause
	 * 
	 * 
	 * @param rootCause
	 *            the <code>Throwable</code> root cause
	 * 
	 */
	public BaseException(Throwable rootCause) {
		super(rootCause);
		this.rootCause = rootCause;
	}

	/**
	 *get root cuase
	 */
	public Throwable getRootCause() {
		return rootCause;
	}
	/**
	 * get self defined error code
	 * */
	public String getErrorCode(){
		return this.errorCode;
	}
}
